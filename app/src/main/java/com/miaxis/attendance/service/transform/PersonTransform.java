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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    //    public static MxResponse<?> insert(User user) {
    //        if (user == null || user.isIllegal()) {
    //            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, MxResponseCode.MSG_ILLEGAL_PARAMETER);
    //        }
    //        if (StringUtils.isNullOrEmpty(user.url_face)) {
    //            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, "face image url can not be null or empty");
    //        }
    //        if (ListUtils.isNullOrEmpty(user.getUrl_fingers())) {
    //            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, "finger images can not be null or empty");
    //        }
    //        for (User.Finger finger : user.getUrl_fingers()) {
    //            if (finger.isIllegal()) {
    //                return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, "finger data is illegal");
    //            }
    //        }
    //        Person person = PersonModel.findByUserID(String.valueOf(user.id));
    //        if (person != null) {
    //            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "already exists");
    //        }
    //        String facePath = AppConfig.Path_FaceImage + "face_" + user.id + "_" + RANDOM.nextLong() + ".jpeg";
    //        ZZResponse<?> downloadFace = new DownloadClient()
    //                .bindDownloadInfo(user.url_face, facePath)
    //                .bindDownloadTimeOut(3 * 1000, 3 * 1000)
    //                .download();
    //        if (!ZZResponse.isSuccess(downloadFace)) {
    //            return MxResponse.CreateFail(downloadFace.getCode(), downloadFace.getMsg());
    //        }
    //        BitmapFactory.Options options = new BitmapFactory.Options();
    //        options.inJustDecodeBounds = true;
    //        BitmapFactory.decodeFile(facePath, options);
    //        if (options.outWidth <= 0 || options.outHeight <= 0) {
    //            FileUtils.delete(facePath);
    //            return MxResponse.CreateFail(MxResponseCode.Code_Illegal_Image_Face, MxResponseCode.Msg_Illegal_Image_Face);
    //        }
    //        List<String> fingerList = new ArrayList<>();
    //        if (user.getUrl_fingers() != null) {
    //            for (User.Finger finger : user.getUrl_fingers()) {
    //                String fingerPath = AppConfig.Path_FingerImage + "finger_" + user.id + "_" + finger.position + "_" + RANDOM.nextLong() + ".jpeg";
    //                ZZResponse<?> downloadFinger = new DownloadClient()
    //                        .bindDownloadInfo(finger.url, fingerPath)
    //                        .bindDownloadTimeOut(3 * 1000, 3 * 1000)
    //                        .download();
    //                if (!ZZResponse.isSuccess(downloadFinger)) {
    //                    FileUtils.delete(facePath);
    //                    FileUtils.delete(fingerList);
    //                    return MxResponse.CreateFail(MxResponseCode.Code_Illegal_Image_Finger, downloadFinger.getMsg());
    //                } else {
    //                    BitmapFactory.Options fingerOptions = new BitmapFactory.Options();
    //                    fingerOptions.inJustDecodeBounds = true;
    //                    BitmapFactory.decodeFile(fingerPath, fingerOptions);
    //                    if (fingerOptions.outWidth <= 0 || fingerOptions.outHeight <= 0) {
    //                        FileUtils.delete(facePath);
    //                        FileUtils.delete(fingerList);
    //                        return MxResponse.CreateFail(MxResponseCode.Code_Illegal_Image_Finger, MxResponseCode.Msg_Illegal_Image_Finger);
    //                    } else {
    //                        fingerList.add(fingerPath);
    //                    }
    //                }
    //            }
    //        }
    //
    //        MxResponse<byte[]> featureExtract = doFaceProcess(facePath);
    //        if (!MxResponse.isSuccess(featureExtract)) {
    //            FileUtils.delete(facePath);
    //            FileUtils.delete(fingerList);
    //            return featureExtract;
    //        }
    //        List<Finger> fingers = new ArrayList<>();
    //        for (String localPath : fingerList) {
    //            MXResult<MxImage> imageLoad = MXImageToolsAPI.getInstance().ImageLoad(localPath, 1);
    //            if (!MXResult.isSuccess(imageLoad)) {
    //                FileUtils.delete(facePath);
    //                FileUtils.delete(fingerList);
    //                return MxResponse.CreateFail(imageLoad.getCode(), imageLoad.getMsg());
    //            }
    //            MxImage fingerImage = imageLoad.getData();
    //            MXResult<byte[]> extractFeature = MR990FingerStrategy.getInstance().extractFeature(fingerImage.buffer, fingerImage.width, fingerImage.height);
    //            if (!MXResult.isSuccess(extractFeature)) {
    //                Timber.e("imageLoad:%s", imageLoad);
    //                FileUtils.delete(facePath);
    //                FileUtils.delete(fingerList);
    //                return MxResponse.CreateFail(extractFeature.getCode(), extractFeature.getMsg());
    //            }
    //            Finger finger = new Finger();
    //            finger.UserId = "" + user.id;
    //            finger.FingerFeature = extractFeature.getData();
    //            fingers.add(finger);
    //        }
    //
    //        LocalImage localImage = new LocalImage();
    //        localImage.UserId = "" + user.id;
    //        localImage.Type = 1;
    //        localImage.LocalPath = facePath;
    //        localImage.id = LocalImageModel.insert(localImage);
    //        if (localImage.id <= 0) {
    //            FileUtils.delete(facePath);
    //            FileUtils.delete(fingerList);
    //            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert face image failed");
    //        }
    //
    //        for (String fingerPath : fingerList) {
    //            LocalImage finger = new LocalImage();
    //            finger.UserId = "" + user.id;
    //            finger.Type = 1;
    //            finger.LocalPath = fingerPath;
    //            finger.id = LocalImageModel.insert(finger);
    //            if (finger.id <= 0) {
    //                FileUtils.delete(facePath);
    //                FileUtils.delete(fingerList);
    //                FingerModel.delete(fingers);
    //                return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert finger image failed");
    //            }
    //        }
    //        person = new Person();
    //        person.UserId = "" + user.id;
    //        person.IdCardNumber = user.id_number;
    //        person.Number = user.id_number;
    //        person.Name = user.name;
    //        person.id = PersonModel.insert(person);
    //        if (person.id <= 0) {
    //            LocalImageModel.delete(localImage);
    //            FileUtils.delete(facePath);
    //            FileUtils.delete(fingerList);
    //            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert person failed");
    //        }
    //        for (Finger finger : fingers) {
    //            finger.id = FingerModel.insert(finger);
    //            if (finger.id <= 0) {
    //                PersonModel.delete(person);
    //                FileUtils.delete(facePath);
    //                FileUtils.delete(fingerList);
    //                FingerModel.delete(fingers);
    //                return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert finger failed");
    //            }
    //        }
    //        Face face = new Face();
    //        face.UserId = "" + user.id;
    //        face.FaceFeature = featureExtract.getData();
    //        face.id = FaceModel.insert(face);
    //        if (face.id <= 0) {
    //            PersonModel.delete(person);
    //            FaceModel.delete(face);
    //            LocalImageModel.delete(localImage);
    //            FingerModel.delete(fingers);
    //            FileUtils.delete(facePath);
    //            FileUtils.delete(fingerList);
    //            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert face failed");
    //        }
    //        return MxResponse.CreateSuccess(person);
    //    }

    public static MxResponse<?> insert(User user) {
        if (user == null || user.isIllegal()) {
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, MxResponseCode.MSG_ILLEGAL_PARAMETER);
        }
        String userId = String.valueOf(user.id);
        Person person = PersonModel.findByUserID(userId);
        if (person != null) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "already exists");
        }
        MxResponse<Face> faceMxResponse = processFace(userId, user.url_face);
        if (!MxResponse.isSuccess(faceMxResponse)) {
            return faceMxResponse;
        }
        MxResponse<AbstractMap.SimpleEntry<List<Finger>, List<LocalImage>>> fingerMxResponse = processFinger(userId, user.getUrl_fingers());
        if (!MxResponse.isSuccess(fingerMxResponse)) {
            FaceModel.delete(faceMxResponse.getData());
            return fingerMxResponse;
        }
        person = new Person();
        person.UserId = userId;
        person.IdCardNumber = user.id_number;
        person.Number = user.id_number;
        person.Name = user.name;
        person.id = PersonModel.insert(person);
        if (person.id <= 0) {
            PersonModel.delete(person);
            FaceModel.delete(faceMxResponse.getData());
            AbstractMap.SimpleEntry<List<Finger>, List<LocalImage>> data = fingerMxResponse.getData();
            FingerModel.delete(data.getKey());
            LocalImageModel.deleteList(data.getValue());
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert person failed");
        }
        return MxResponse.CreateSuccess();
    }

    private static MxResponse<byte[]> doFaceProcess(String facePath) {
        MXResult<MxImage> imageLoad = MXImageToolsAPI.getInstance().ImageLoad(facePath, 3);
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
            return MxResponse.CreateFail(MxResponseCode.Code_Illegal_Image_Face, "low quality,"+faceQuality.getData());
        }
        MXResult<byte[]> featureExtract = MXFaceIdAPI.getInstance().mxFeatureExtract(decode, image.width, image.height, maxFace);
        if (!MXResult.isSuccess(featureExtract)) {
            return MxResponse.CreateFail(featureExtract.getCode(), featureExtract.getMsg());
        }
        return MxResponse.CreateSuccess(featureExtract.getData());
    }

    private static MxResponse<Face> processFace(String userId, String url_face) {
        if (StringUtils.isNullOrEmpty(userId)) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "user id error");
        }
        if (StringUtils.isNullOrEmpty(url_face)) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "image url can not be null or empty");
        }
        boolean needUpdateFaceImage = false;
        LocalImage faceImageByUserId = LocalImageModel.findFaceImageByUserId("" + userId);
        if (faceImageByUserId == null) {
            needUpdateFaceImage = true;
        } else {
            needUpdateFaceImage = !Objects.equals(url_face, faceImageByUserId.RemotePath);
        }
        if (needUpdateFaceImage) {
            String facePath = AppConfig.Path_FaceImage + userId + "_" + System.currentTimeMillis() + "_" + RANDOM.nextInt() + ".jpeg";
            ZZResponse<?> download = new DownloadClient()
                    .bindDownloadInfo(url_face, facePath)
                    .bindDownloadTimeOut(5 * 1000, 5 * 1000)
                    .download();
            if (!ZZResponse.isSuccess(download)) {
                return MxResponse.CreateFail(download.getCode(), download.getMsg());
            }
            MxResponse<byte[]> featureExtract = doFaceProcess(facePath);
            if (!MxResponse.isSuccess(featureExtract)) {
                FileUtils.delete(facePath);
                return MxResponse.CreateFail(featureExtract);
            }
            Face face = new Face();
            face.UserId = userId;
            face.FaceFeature = featureExtract.getData();
            face.id = FaceModel.insert(face);
            if (face.id <= 0) {
                FileUtils.delete(facePath);
                return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert face failed");
            }
            return MxResponse.CreateSuccess(face);
        } else {
            return MxResponse.CreateSuccess();
        }
    }

    private static MxResponse<AbstractMap.SimpleEntry<List<Finger>, List<LocalImage>>> processFinger(String userId, List<User.Finger> url_fingers) {
        if (StringUtils.isNullOrEmpty(userId)) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "user id error");
        }
        //HashMap<Finger, LocalImage> fingerLocalImage = new HashMap<>();
        List<Finger> fingerList = new ArrayList<>();
        List<LocalImage> imageList = new ArrayList<>();
        AbstractMap.SimpleEntry<List<Finger>, List<LocalImage>> listListSimpleEntry = new AbstractMap.SimpleEntry<>(fingerList, imageList);
        if (ListUtils.isNullOrEmpty(url_fingers)) {
            FingerModel.delete(userId);
            LocalImageModel.deleteFingerImage(userId);
        } else {
            for (User.Finger uFinger : url_fingers) {
                String fingerPath = AppConfig.Path_FingerImage + "finger_" + userId + "_" + uFinger.position + "_" + RANDOM.nextLong() + ".jpeg";
                ZZResponse<?> downloadFinger = new DownloadClient()
                        .bindDownloadInfo(uFinger.url, fingerPath)
                        .bindDownloadTimeOut(5 * 1000, 5 * 1000)
                        .download();
                if (!ZZResponse.isSuccess(downloadFinger)) {
                    LocalImageModel.deleteList(imageList);
                    FingerModel.delete(fingerList);
                    return MxResponse.CreateFail(MxResponseCode.Code_Illegal_Image_Finger, downloadFinger.getMsg());
                } else {
                    BitmapFactory.Options fingerOptions = new BitmapFactory.Options();
                    fingerOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(fingerPath, fingerOptions);
                    if (fingerOptions.outWidth <= 0 || fingerOptions.outHeight <= 0) {
                        LocalImageModel.deleteList(imageList);
                        FingerModel.delete(fingerList);
                        return MxResponse.CreateFail(MxResponseCode.Code_Illegal_Image_Finger, MxResponseCode.Msg_Illegal_Image_Finger);
                    } else {
                        LocalImage localImage = new LocalImage();
                        localImage.UserId = userId;
                        localImage.Type = 3;
                        localImage.LocalPath = fingerPath;
                        localImage.id = LocalImageModel.insert(localImage);
                        if (localImage.id <= 0) {
                            LocalImageModel.deleteList(imageList);
                            FingerModel.delete(fingerList);
                            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert image failed");
                        }
                        imageList.add(localImage);
                        MXResult<MxImage> imageLoad = MXImageToolsAPI.getInstance().ImageLoad(fingerPath, 1);
                        if (!MXResult.isSuccess(imageLoad)) {
                            LocalImageModel.deleteList(imageList);
                            FingerModel.delete(fingerList);
                            return MxResponse.CreateFail(imageLoad.getCode(), imageLoad.getMsg());
                        }
                        MxImage fingerImage = imageLoad.getData();
                        MXResult<byte[]> extractFeature = MR990FingerStrategy.getInstance().extractFeature(fingerImage.buffer, fingerImage.width, fingerImage.height);
                        if (!MXResult.isSuccess(extractFeature)) {
                            LocalImageModel.deleteList(imageList);
                            FingerModel.delete(fingerList);
                            return MxResponse.CreateFail(extractFeature.getCode(), extractFeature.getMsg());
                        }
                        Finger finger = new Finger();
                        finger.UserId = userId;
                        finger.FingerFeature = extractFeature.getData();
                        finger.id = FingerModel.insert(finger);
                        if (finger.id <= 0) {
                            LocalImageModel.deleteList(imageList);
                            FingerModel.delete(fingerList);
                            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert finger failed");
                        }
                        fingerList.add(finger);
                    }
                }
            }
        }
        return MxResponse.CreateSuccess(listListSimpleEntry);
    }

    public static MxResponse<?> update(User user) {
        if (user == null || user.isIllegal()) {
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, MxResponseCode.MSG_ILLEGAL_PARAMETER);
        }
        String userId = String.valueOf(user.id);
        Person person = PersonModel.findByUserID(userId);
        if (person == null) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "not exists");
        }
        MxResponse<Face> faceMxResponse = processFace(userId, user.url_face);
        if (!MxResponse.isSuccess(faceMxResponse)) {
            return faceMxResponse;
        }
        MxResponse<AbstractMap.SimpleEntry<List<Finger>, List<LocalImage>>> fingerMxResponse = processFinger(userId, user.getUrl_fingers());
        if (!MxResponse.isSuccess(fingerMxResponse)) {
            FaceModel.delete(faceMxResponse.getData());
            return fingerMxResponse;
        }
        person.UserId = userId;
        person.IdCardNumber = user.id_number;
        person.Number = user.id_number;
        person.Name = user.name;
        person.id = PersonModel.insert(person);
        if (person.id <= 0) {
            PersonModel.delete(person);
            FaceModel.delete(faceMxResponse.getData());
            AbstractMap.SimpleEntry<List<Finger>, List<LocalImage>> data = fingerMxResponse.getData();
            FingerModel.delete(data.getKey());
            LocalImageModel.deleteList(data.getValue());
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert person failed");
        }
        return MxResponse.CreateSuccess();
        //        String facePath = AppConfig.Path_FaceImage + user.id + "_" + System.currentTimeMillis() + "_" + RANDOM.nextInt() + ".jpeg";
        //        ZZResponse<?> download = new DownloadClient()
        //                .bindDownloadInfo(user.url_face, facePath)
        //                .bindDownloadTimeOut(3 * 1000, 3 * 1000)
        //                .download();
        //        if (!ZZResponse.isSuccess(download)) {
        //            return MxResponse.CreateFail(download.getCode(), download.getMsg());
        //        }

        //        List<String> fingerList = new ArrayList<>();
        //        for (User.Finger finger : user.getUrl_fingers()) {
        //            String fingerPath = AppConfig.Path_FingerImage + "finger_" + user.id + "_" + finger.position + "_" + RANDOM.nextLong() + ".jpeg";
        //            ZZResponse<?> downloadFinger = new DownloadClient()
        //                    .bindDownloadInfo(finger.url, fingerPath)
        //                    .bindDownloadTimeOut(3 * 1000, 3 * 1000)
        //                    .download();
        //            if (!ZZResponse.isSuccess(downloadFinger)) {
        //                FileUtils.delete(facePath);
        //                FileUtils.delete(fingerList);
        //                return MxResponse.CreateFail(MxResponseCode.Code_Illegal_Image_Finger, downloadFinger.getMsg());
        //            } else {
        //                BitmapFactory.Options fingerOptions = new BitmapFactory.Options();
        //                fingerOptions.inJustDecodeBounds = true;
        //                BitmapFactory.decodeFile(fingerPath, fingerOptions);
        //                if (fingerOptions.outWidth <= 0 || fingerOptions.outHeight <= 0) {
        //                    FileUtils.delete(facePath);
        //                    FileUtils.delete(fingerList);
        //                    return MxResponse.CreateFail(MxResponseCode.Code_Illegal_Image_Finger, MxResponseCode.Msg_Illegal_Image_Finger);
        //                } else {
        //                    fingerList.add(fingerPath);
        //                }
        //            }
        //        }

        //        MxResponse<byte[]> featureExtract = doFaceProcess(facePath);
        //        if (!MxResponse.isSuccess(featureExtract)) {
        //            FileUtils.delete(facePath);
        //            FileUtils.delete(fingerList);
        //            return featureExtract;
        //        }

        //        List<Finger> fingers = new ArrayList<>();
        //        for (String localPath : fingerList) {
        //            MXResult<MxImage> imageLoad = MXImageToolsAPI.getInstance().ImageLoad(localPath, 1);
        //            if (!MXResult.isSuccess(imageLoad)) {
        //                FileUtils.delete(facePath);
        //                FileUtils.delete(fingerList);
        //                return MxResponse.CreateFail(imageLoad.getCode(), imageLoad.getMsg());
        //            }
        //            MxImage fingerImage = imageLoad.getData();
        //            MXResult<byte[]> extractFeature = MR990FingerStrategy.getInstance().extractFeature(fingerImage.buffer, fingerImage.width, fingerImage.height);
        //            if (!MXResult.isSuccess(extractFeature)) {
        //                FileUtils.delete(facePath);
        //                FileUtils.delete(fingerList);
        //                return MxResponse.CreateFail(extractFeature.getCode(), extractFeature.getMsg());
        //            }
        //            Finger finger = new Finger();
        //            finger.UserId = "" + user.id;
        //            finger.FingerFeature = extractFeature.getData();
        //            fingers.add(finger);
        //        }

        //        LocalImage localImage = new LocalImage();
        //        localImage.UserId = "" + user.id;
        //        localImage.Type = 1;
        //        localImage.LocalPath = facePath;
        //        localImage.id = LocalImageModel.insert(localImage);
        //        if (localImage.id <= 0) {
        //            LocalImageModel.delete(localImage);
        //            FingerModel.delete(fingers);
        //            FileUtils.delete(facePath);
        //            FileUtils.delete(fingerList);
        //            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert image failed");
        //        }

        //        person.UserId = "" + user.id;
        //        person.IdCardNumber = user.id_number;
        //        person.Number = user.id_number;
        //        person.Name = user.name;
        //        person.id = PersonModel.insert(person);
        //        if (person.id <= 0) {
        //            PersonModel.delete(person);
        //            LocalImageModel.delete(localImage);
        //            FingerModel.delete(fingers);
        //            FileUtils.delete(facePath);
        //            FileUtils.delete(fingerList);
        //            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert person failed");
        //        }
        //        Face face = new Face();
        //        face.UserId = "" + user.id;
        //        face.FaceFeature = featureExtract.getData();
        //        face.id = FaceModel.insert(face);
        //        if (face.id <= 0) {
        //            PersonModel.delete(person);
        //            FaceModel.delete(face);
        //            LocalImageModel.delete(localImage);
        //            FingerModel.delete(fingers);
        //            FileUtils.delete(facePath);
        //            FileUtils.delete(fingerList);
        //            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert face failed");
        //        }
        //        return MxResponse.CreateSuccess();
    }

    public static MxResponse<?> delete(User user) {
        if (user == null || user.id <= 0) {
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, MxResponseCode.MSG_ILLEGAL_PARAMETER);
        }
        Person person = PersonModel.findByUserID(String.valueOf(user.id));
        if (person == null) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "not exists");
        }
        FaceModel.delete(person.UserId);
        FingerModel.delete(person.UserId);
        List<LocalImage> byUserId = LocalImageModel.findByUserId(person.UserId);
        for (LocalImage localImage : byUserId) {
            FileUtils.delete(localImage.LocalPath);
            LocalImageModel.delete(localImage);
        }
        PersonModel.delete(person);
        return MxResponse.CreateSuccess();
    }

    public static MxResponse<?> insertOrUpdate(User user) {
        if (user == null || user.isIllegal()) {
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, MxResponseCode.MSG_ILLEGAL_PARAMETER);
        }
        String userId = String.valueOf(user.id);
        Person person = PersonModel.findByUserID(userId);
        if (person == null) {
            person = new Person();
        }
        MxResponse<Face> faceMxResponse = processFace(userId, user.url_face);
        if (!MxResponse.isSuccess(faceMxResponse)) {
            return faceMxResponse;
        }
        MxResponse<AbstractMap.SimpleEntry<List<Finger>, List<LocalImage>>> fingerMxResponse = processFinger(userId, user.getUrl_fingers());
        if (!MxResponse.isSuccess(fingerMxResponse)) {
            FaceModel.delete(faceMxResponse.getData());
            return fingerMxResponse;
        }
        person.UserId = userId;
        person.IdCardNumber = user.id_number;
        person.Number = user.id_number;
        person.Name = user.name;
        person.id = PersonModel.insert(person);
        if (person.id <= 0) {
            PersonModel.delete(person);
            FaceModel.delete(faceMxResponse.getData());
            AbstractMap.SimpleEntry<List<Finger>, List<LocalImage>> data = fingerMxResponse.getData();
            FingerModel.delete(data.getKey());
            LocalImageModel.deleteList(data.getValue());
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert person failed");
        }
        return MxResponse.CreateSuccess();
    }


}
