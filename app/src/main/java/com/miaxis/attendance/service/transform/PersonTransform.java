package com.miaxis.attendance.service.transform;

import android.graphics.BitmapFactory;

import com.miaxis.attendance.config.AppConfig;
import com.miaxis.attendance.data.entity.Face;
import com.miaxis.attendance.data.entity.Finger;
import com.miaxis.attendance.data.entity.LocalImage;
import com.miaxis.attendance.data.entity.Person;
import com.miaxis.attendance.data.model.FaceModel;
import com.miaxis.attendance.data.model.FingerModel;
import com.miaxis.attendance.data.model.LocalImageModel;
import com.miaxis.attendance.data.model.PersonModel;
import com.miaxis.attendance.service.MxResponse;
import com.miaxis.attendance.service.MxResponseCode;
import com.miaxis.attendance.service.bean.User;
import com.miaxis.attendance.ui.finger.MR990FingerStrategy;
import com.miaxis.common.response.ZZResponse;
import com.miaxis.common.utils.DownloadClient;
import com.miaxis.common.utils.FileUtils;
import com.miaxis.common.utils.ListUtils;
import com.miaxis.common.utils.StringUtils;

import org.zz.api.MXFace;
import org.zz.api.MXFaceIdAPI;
import org.zz.api.MXImageToolsAPI;
import org.zz.api.MXResult;
import org.zz.api.MxImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Tank
 * @date 2021/8/23 7:24 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class PersonTransform {

    private static final String TAG = "PersonTransform";

    private static final Random RANDOM = new Random();

    public static MxResponse<?> insert(User user) {
        if (user == null || user.isIllegal()) {
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, MxResponseCode.MSG_ILLEGAL_PARAMETER);
        }
        if (StringUtils.isNullOrEmpty(user.url_face)) {
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, "face image url can not be null or empty");
        }
        if (ListUtils.isNullOrEmpty(user.url_fingers)) {
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, "finger images can not be null or empty");
        }
        for (User.Finger finger : user.url_fingers) {
            if (finger.isIllegal()) {
                return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, "finger data is illegal");
            }
        }
        List<Person> byUserID = PersonModel.findByUserID(String.valueOf(user.id));
        if (!ListUtils.isNullOrEmpty(byUserID)) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "already exists");
        }
        String facePath = AppConfig.Path_FaceImage + "face_" + user.id + "_" + RANDOM.nextLong() + ".jpeg";
        ZZResponse<?> downloadFace = new DownloadClient()
                .bindDownloadInfo(user.url_face, facePath)
                .bindDownloadTimeOut(3 * 1000, 3 * 1000)
                .download();
        if (!ZZResponse.isSuccess(downloadFace)) {
            return MxResponse.CreateFail(downloadFace.getCode(), downloadFace.getMsg());
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(facePath, options);
        if (options.outWidth <= 0 || options.outHeight <= 0) {
            FileUtils.delete(facePath);
            return MxResponse.CreateFail(MxResponseCode.Code_Illegal_Image_Face, MxResponseCode.Msg_Illegal_Image_Face);
        }
        List<String> fingerList = new ArrayList<>();
        if (user.url_fingers != null) {
            for (User.Finger finger : user.url_fingers) {
                String fingerPath = AppConfig.Path_FingerImage + "finger_" + user.id + "_" + finger.position + "_" + RANDOM.nextLong() + ".jpeg";
                ZZResponse<?> downloadFinger = new DownloadClient()
                        .bindDownloadInfo(finger.url, fingerPath)
                        .bindDownloadTimeOut(3 * 1000, 3 * 1000)
                        .download();
                if (!ZZResponse.isSuccess(downloadFinger)) {
                    FileUtils.delete(facePath);
                    FileUtils.delete(fingerList);
                    return MxResponse.CreateFail(MxResponseCode.Code_Illegal_Image_Finger, downloadFinger.getMsg());
                } else {
                    BitmapFactory.Options fingerOptions = new BitmapFactory.Options();
                    fingerOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(fingerPath, fingerOptions);
                    if (fingerOptions.outWidth <= 0 || fingerOptions.outHeight <= 0) {
                        FileUtils.delete(facePath);
                        FileUtils.delete(fingerList);
                        return MxResponse.CreateFail(MxResponseCode.Code_Illegal_Image_Finger, MxResponseCode.Msg_Illegal_Image_Finger);
                    } else {
                        fingerList.add(fingerPath);
                    }
                }
            }
        }

        MxResponse<byte[]> featureExtract = doFaceProcess(facePath);
        if (!MxResponse.isSuccess(featureExtract)) {
            FileUtils.delete(facePath);
            FileUtils.delete(fingerList);
            return featureExtract;
        }
        List<Finger> fingers = new ArrayList<>();
        for (String localPath : fingerList) {
            MXResult<MxImage> imageLoad = MXImageToolsAPI.getInstance().ImageLoad(localPath);
            if (!MXResult.isSuccess(imageLoad)) {
                FileUtils.delete(facePath);
                FileUtils.delete(fingerList);
                return MxResponse.CreateFail(imageLoad.getCode(), imageLoad.getMsg());
            }
            MxImage fingerImage = imageLoad.getData();
            MXResult<byte[]> extractFeature = MR990FingerStrategy.getInstance().extractFeature(fingerImage.buffer, fingerImage.width, fingerImage.height);
            if (!MXResult.isSuccess(extractFeature)) {
                FileUtils.delete(facePath);
                FileUtils.delete(fingerList);
                return MxResponse.CreateFail(extractFeature.getCode(), extractFeature.getMsg());
            }
            Finger finger = new Finger();
            finger.UserId = "" + user.id;
            finger.FingerFeature = extractFeature.getData();
            fingers.add(finger);
        }

        LocalImage localImage = new LocalImage();
        localImage.UserId = "" + user.id;
        localImage.Type = 1;
        localImage.LocalPath = facePath;
        localImage.id = LocalImageModel.insert(localImage);
        if (localImage.id <= 0) {
            FileUtils.delete(facePath);
            FileUtils.delete(fingerList);
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert face image failed");
        }

        for (String fingerPath : fingerList) {
            LocalImage finger = new LocalImage();
            finger.UserId = "" + user.id;
            finger.Type = 1;
            finger.LocalPath = fingerPath;
            finger.id = LocalImageModel.insert(finger);
            if (finger.id <= 0) {
                FileUtils.delete(facePath);
                FileUtils.delete(fingerList);
                FingerModel.delete(fingers);
                return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert finger image failed");
            }
        }
        Person person = new Person();
        person.UserId = "" + user.id;
        person.IdCardNumber = user.id_number;
        person.Number = user.id_number;
        person.Name = user.name;
        person.id = PersonModel.insert(person);
        if (person.id <= 0) {
            LocalImageModel.delete(localImage);
            FileUtils.delete(facePath);
            FileUtils.delete(fingerList);
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert person failed");
        }
        for (Finger finger : fingers) {
            finger.id = FingerModel.insert(finger);
            if (finger.id <= 0) {
                PersonModel.delete(person);
                FileUtils.delete(facePath);
                FileUtils.delete(fingerList);
                FingerModel.delete(fingers);
                return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert finger failed");
            }
        }
        Face face = new Face();
        face.UserId = "" + user.id;
        face.FaceFeature = featureExtract.getData();
        face.id = FaceModel.insert(face);
        if (face.id <= 0) {
            PersonModel.delete(person);
            FaceModel.delete(face);
            LocalImageModel.delete(localImage);
            FingerModel.delete(fingers);
            FileUtils.delete(facePath);
            FileUtils.delete(fingerList);
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert face failed");
        }
        return MxResponse.CreateSuccess(person);
    }

    private static MxResponse<byte[]> doFaceProcess(String facePath) {
        MXResult<MxImage> imageLoad = MXImageToolsAPI.getInstance().ImageLoad(facePath);
        if (!MXResult.isSuccess(imageLoad)) {
            return MxResponse.CreateFail(imageLoad.getCode(), imageLoad.getMsg());
        }
        MxImage image = imageLoad.getData();
        byte[] decode = image.buffer;
        MXResult<List<MXFace>> mxResult = MXFaceIdAPI.getInstance().mxDetectFace(decode, image.width, image.height);
        if (!MXResult.isSuccess(mxResult)) {
            return MxResponse.CreateFail(mxResult.getCode(), mxResult.getMsg());
        }
        MXFace maxFace = MXFaceIdAPI.getInstance().getMaxFace(mxResult.getData());
        MXResult<Integer> faceQuality = MXFaceIdAPI.getInstance().mxFaceQuality(decode, image.width, image.height, maxFace);
        if (!MXResult.isSuccess(faceQuality)) {
            return MxResponse.CreateFail(faceQuality.getCode(), faceQuality.getMsg());
        }
        if (faceQuality.getData() < MXFaceIdAPI.getInstance().FaceQuality) {
            return MxResponse.CreateFail(MxResponseCode.Code_Illegal_Image_Face, "low quality");
        }
        MXResult<byte[]> featureExtract = MXFaceIdAPI.getInstance().mxFeatureExtract(decode, image.width, image.height, maxFace);
        if (!MXResult.isSuccess(featureExtract)) {
            return MxResponse.CreateFail(featureExtract.getCode(), featureExtract.getMsg());
        }
        return MxResponse.CreateSuccess(featureExtract.getData());
    }

    public static MxResponse<?> update(User user) {
        if (user == null || user.isIllegal()) {
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, MxResponseCode.MSG_ILLEGAL_PARAMETER);
        }
        if (StringUtils.isNullOrEmpty(user.url_face)) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "image url can not be null or empty");
        }
        List<Person> byUserID = PersonModel.findByUserID(String.valueOf(user.id));
        if (ListUtils.isNullOrEmpty(byUserID)) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "not exists");
        }
        String facePath = AppConfig.Path_FaceImage + user.id + "_" + System.currentTimeMillis() + "_" + RANDOM.nextInt() + ".jpeg";
        ZZResponse<?> download = new DownloadClient()
                .bindDownloadInfo(user.url_face, facePath)
                .bindDownloadTimeOut(3 * 1000, 3 * 1000)
                .download();
        if (!ZZResponse.isSuccess(download)) {
            return MxResponse.CreateFail(download.getCode(), download.getMsg());
        }
        List<String> fingerList = new ArrayList<>();
        for (User.Finger finger : user.url_fingers) {
            String fingerPath = AppConfig.Path_FingerImage + "finger_" + user.id + "_" + finger.position + "_" + RANDOM.nextLong() + ".jpeg";
            ZZResponse<?> downloadFinger = new DownloadClient()
                    .bindDownloadInfo(finger.url, fingerPath)
                    .bindDownloadTimeOut(3 * 1000, 3 * 1000)
                    .download();
            if (!ZZResponse.isSuccess(downloadFinger)) {
                FileUtils.delete(facePath);
                FileUtils.delete(fingerList);
                return MxResponse.CreateFail(MxResponseCode.Code_Illegal_Image_Finger, downloadFinger.getMsg());
            } else {
                BitmapFactory.Options fingerOptions = new BitmapFactory.Options();
                fingerOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(fingerPath, fingerOptions);
                if (fingerOptions.outWidth <= 0 || fingerOptions.outHeight <= 0) {
                    FileUtils.delete(facePath);
                    FileUtils.delete(fingerList);
                    return MxResponse.CreateFail(MxResponseCode.Code_Illegal_Image_Finger, MxResponseCode.Msg_Illegal_Image_Finger);
                } else {
                    fingerList.add(fingerPath);
                }
            }
        }

        MxResponse<byte[]> featureExtract = doFaceProcess(facePath);
        if (!MxResponse.isSuccess(featureExtract)) {
            FileUtils.delete(facePath);
            FileUtils.delete(fingerList);
            return featureExtract;
        }

        List<Finger> fingers = new ArrayList<>();
        for (String localPath : fingerList) {
            MXResult<MxImage> imageLoad = MXImageToolsAPI.getInstance().ImageLoad(localPath);
            if (!MXResult.isSuccess(imageLoad)) {
                FileUtils.delete(facePath);
                FileUtils.delete(fingerList);
                return MxResponse.CreateFail(imageLoad.getCode(), imageLoad.getMsg());
            }
            MxImage fingerImage = imageLoad.getData();
            MXResult<byte[]> extractFeature = MR990FingerStrategy.getInstance().extractFeature(fingerImage.buffer, fingerImage.width, fingerImage.height);
            if (!MXResult.isSuccess(extractFeature)) {
                FileUtils.delete(facePath);
                FileUtils.delete(fingerList);
                return MxResponse.CreateFail(extractFeature.getCode(), extractFeature.getMsg());
            }
            Finger finger = new Finger();
            finger.UserId = "" + user.id;
            finger.FingerFeature = extractFeature.getData();
            fingers.add(finger);
        }

        LocalImage localImage = new LocalImage();
        localImage.UserId = "" + user.id;
        localImage.Type = 1;
        localImage.LocalPath = facePath;
        localImage.id = LocalImageModel.insert(localImage);
        if (localImage.id <= 0) {
            LocalImageModel.delete(localImage);
            FingerModel.delete(fingers);
            FileUtils.delete(facePath);
            FileUtils.delete(fingerList);
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert image failed");
        }
        Person person = byUserID.get(0);
        person.UserId = "" + user.id;
        person.IdCardNumber = user.id_number;
        person.Number = user.id_number;
        person.Name = user.name;
        person.id = PersonModel.insert(person);
        if (person.id <= 0) {
            PersonModel.delete(person);
            LocalImageModel.delete(localImage);
            FingerModel.delete(fingers);
            FileUtils.delete(facePath);
            FileUtils.delete(fingerList);
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert person failed");
        }
        Face face = new Face();
        face.UserId = "" + user.id;
        face.FaceFeature = featureExtract.getData();
        face.id = FaceModel.insert(face);
        if (face.id <= 0) {
            PersonModel.delete(person);
            FaceModel.delete(face);
            LocalImageModel.delete(localImage);
            FingerModel.delete(fingers);
            FileUtils.delete(facePath);
            FileUtils.delete(fingerList);
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert face failed");
        }
        return MxResponse.CreateSuccess();
    }

    public static MxResponse<?> delete(User user) {
        if (user == null || user.id <= 0) {
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, MxResponseCode.MSG_ILLEGAL_PARAMETER);
        }
        List<Person> byUserID = PersonModel.findByUserID(String.valueOf(user.id));
        if (ListUtils.isNullOrEmpty(byUserID)) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "not exists");
        }
        for (Person person : byUserID) {
            FaceModel.delete(person.UserId);
            FingerModel.delete(person.UserId);
            List<LocalImage> byUserId = LocalImageModel.findByUserId(person.UserId);
            for (LocalImage localImage : byUserId) {
                FileUtils.delete(localImage.LocalPath);
                LocalImageModel.delete(localImage);
            }
            PersonModel.delete(person);
        }
        return MxResponse.CreateSuccess();
    }
}
