package com.miaxis.attendance.service.transform;

import android.graphics.BitmapFactory;

import com.miaxis.attendance.config.AppConfig;
import com.miaxis.attendance.data.entity.Face;
import com.miaxis.attendance.data.entity.LocalImage;
import com.miaxis.attendance.data.entity.Person;
import com.miaxis.attendance.data.model.FaceModel;
import com.miaxis.attendance.data.model.LocalImageModel;
import com.miaxis.attendance.data.model.PersonModel;
import com.miaxis.attendance.service.MxResponse;
import com.miaxis.attendance.service.MxResponseCode;
import com.miaxis.attendance.service.bean.User;
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
        if (user == null) {
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, MxResponseCode.MSG_ILLEGAL_PARAMETER);
        }
        List<Person> byUserID = PersonModel.findByUserID(String.valueOf(user.id));
        if (!ListUtils.isNullOrEmpty(byUserID)) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "already exists");
        }

        if (StringUtils.isNullOrEmpty(user.base_pic)) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "image url can not be null or empty");
        }
        String savePath = AppConfig.Path_FaceImage + user.id + "_" + System.currentTimeMillis() + "_" + RANDOM.nextInt() + ".jpeg";
        ZZResponse<?> download = new DownloadClient()
                .bindDownloadInfo(user.base_pic, savePath)
                .bindDownloadTimeOut(3 * 1000, 3 * 1000)
                .download();
        if (!ZZResponse.isSuccess(download)) {
            return MxResponse.CreateFail(download.getCode(), download.getMsg());
        }

        //byte[] decode = android.util.Base64.decode(user.base_pic, Base64.URL_SAFE);
        //boolean b = FileUtils.writeFile(savePath, decode);
        //if (!b) {
        //    return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "save image failed");
        //}

        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        //BitmapFactory.decodeByteArray(decode, 0, decode.length, options);
        //if (options.outWidth <= 0 || options.outHeight <= 0) {
        //    FileUtils.delete(savePath);
        //    return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_IMAGE, MxResponseCode.MSG_ILLEGAL_IMAGE);
        //}

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(savePath, options);
        if (options.outWidth <= 0 || options.outHeight <= 0) {
            FileUtils.delete(savePath);
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_IMAGE, MxResponseCode.MSG_ILLEGAL_IMAGE);
        }
        MXResult<MxImage> imageLoad = MXImageToolsAPI.getInstance().ImageLoad(savePath, options.outWidth, options.outHeight);
        if (!MXResult.isSuccess(imageLoad)) {
            FileUtils.delete(savePath);
            return MxResponse.CreateFail(imageLoad.getCode(), imageLoad.getMsg());
        }
        byte[] decode = imageLoad.getData().buffer;

        MXResult<List<MXFace>> mxResult = MXFaceIdAPI.getInstance().mxDetectFace(decode, options.outWidth, options.outHeight);
        if (!MXResult.isSuccess(mxResult)) {
            FileUtils.delete(savePath);
            return MxResponse.CreateFail(mxResult.getCode(), mxResult.getMsg());
        }
        MXFace maxFace = MXFaceIdAPI.getInstance().getMaxFace(mxResult.getData());
        MXResult<Integer> faceQuality = MXFaceIdAPI.getInstance().mxFaceQuality(decode, options.outWidth, options.outHeight, maxFace);
        if (!MXResult.isSuccess(faceQuality)) {
            FileUtils.delete(savePath);
            return MxResponse.CreateFail(faceQuality.getCode(), faceQuality.getMsg());
        }
        if (faceQuality.getData() < MXFaceIdAPI.getInstance().FaceQuality) {
            FileUtils.delete(savePath);
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_IMAGE, "low quality");
        }
        MXResult<byte[]> featureExtract = MXFaceIdAPI.getInstance().mxFeatureExtract(decode, options.outWidth, options.outHeight, maxFace);
        if (!MXResult.isSuccess(featureExtract)) {
            FileUtils.delete(savePath);
            return MxResponse.CreateFail(featureExtract.getCode(), featureExtract.getMsg());
        }
        LocalImage localImage = new LocalImage();
        localImage.Type = 1;
        localImage.ImagePath = savePath;
        localImage.id = LocalImageModel.insert(localImage);
        if (localImage.id <= 0) {
            FileUtils.delete(savePath);
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert image failed");
        }
        Face face = new Face();
        face.UserId = "" + user.id;
        face.FaceFeature = featureExtract.getData();
        face.id = FaceModel.insert(face);
        if (face.id <= 0) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert face failed");
        }
        Person person = new Person();
        person.UserId = "" + user.id;
        person.IdCardNumber = user.id_number;
        person.Number = user.id_number;
        person.Name = user.name;
        person.FaceImage = localImage.id;
        person.FaceID = face.id;
        person.id = PersonModel.insert(person);
        if (person.id <= 0) {
            FaceModel.delete(face);
            LocalImageModel.delete(localImage);
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert person failed");
        }
        return MxResponse.CreateSuccess(person);
    }


    public static MxResponse<?> update(User user) {
        if (user == null) {
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, MxResponseCode.MSG_ILLEGAL_PARAMETER);
        }
        if (StringUtils.isNullOrEmpty(user.base_pic)) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "image url can not be null or empty");
        }
        List<Person> byUserID = PersonModel.findByUserID(String.valueOf(user.id));
        if (ListUtils.isNullOrEmpty(byUserID)) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "not exists");
        }
        String savePath = AppConfig.Path_FaceImage + user.id + "_" + System.currentTimeMillis() + "_" + RANDOM.nextInt() + ".jpeg";
        ZZResponse<?> download = new DownloadClient()
                .bindDownloadInfo(user.base_pic, savePath)
                .bindDownloadTimeOut(3 * 1000, 3 * 1000)
                .download();
        if (!ZZResponse.isSuccess(download)) {
            return MxResponse.CreateFail(download.getCode(), download.getMsg());
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(savePath, options);
        if (options.outWidth <= 0 || options.outHeight <= 0) {
            FileUtils.delete(savePath);
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_IMAGE, MxResponseCode.MSG_ILLEGAL_IMAGE);
        }
        MXResult<MxImage> imageLoad = MXImageToolsAPI.getInstance().ImageLoad(savePath, options.outWidth, options.outHeight);
        if (!MXResult.isSuccess(imageLoad)) {
            FileUtils.delete(savePath);
            return MxResponse.CreateFail(imageLoad.getCode(), imageLoad.getMsg());
        }
        byte[] decode = imageLoad.getData().buffer;
        MXResult<List<MXFace>> mxResult = MXFaceIdAPI.getInstance().mxDetectFace(decode, options.outWidth, options.outHeight);
        if (!MXResult.isSuccess(mxResult)) {
            FileUtils.delete(savePath);
            return MxResponse.CreateFail(mxResult.getCode(), mxResult.getMsg());
        }
        MXFace maxFace = MXFaceIdAPI.getInstance().getMaxFace(mxResult.getData());
        MXResult<Integer> faceQuality = MXFaceIdAPI.getInstance().mxFaceQuality(decode, options.outWidth, options.outHeight, maxFace);
        if (!MXResult.isSuccess(faceQuality)) {
            FileUtils.delete(savePath);
            return MxResponse.CreateFail(faceQuality.getCode(), faceQuality.getMsg());
        }
        if (faceQuality.getData() < MXFaceIdAPI.getInstance().FaceQuality) {
            FileUtils.delete(savePath);
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_IMAGE, "low quality");
        }
        MXResult<byte[]> featureExtract = MXFaceIdAPI.getInstance().mxFeatureExtract(decode, options.outWidth, options.outHeight, maxFace);
        if (!MXResult.isSuccess(featureExtract)) {
            FileUtils.delete(savePath);
            return MxResponse.CreateFail(featureExtract.getCode(), featureExtract.getMsg());
        }
        LocalImage localImage = new LocalImage();
        localImage.Type = 1;
        localImage.ImagePath = savePath;
        localImage.id = LocalImageModel.insert(localImage);
        if (localImage.id <= 0) {
            FileUtils.delete(savePath);
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert image failed");
        }
        Face face = new Face();
        face.UserId = "" + user.id;
        face.FaceFeature = featureExtract.getData();
        face.id = FaceModel.insert(face);
        if (face.id <= 0) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert face failed");
        }
        Person person = byUserID.get(0);
        person.UserId = "" + user.id;
        person.IdCardNumber = user.id_number;
        person.Number = user.id_number;
        person.Name = user.name;
        person.FaceImage = localImage.id;
        person.FaceID = face.id;
        person.id = PersonModel.insert(person);
        if (person.id <= 0) {
            FaceModel.delete(face);
            LocalImageModel.delete(localImage);
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "insert person failed");
        }
        return MxResponse.CreateSuccess();
    }

    public static MxResponse<?> delete(User user) {
        if (user == null) {
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, MxResponseCode.MSG_ILLEGAL_PARAMETER);
        }
        if (StringUtils.isNullOrEmpty(user.base_pic)) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "image url can not be null or empty");
        }
        List<Person> byUserID = PersonModel.findByUserID(String.valueOf(user.id));
        if (ListUtils.isNullOrEmpty(byUserID)) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "not exists");
        }
        for (Person person : byUserID) {
            FaceModel.delete(person.UserId);
            List<LocalImage> byID = LocalImageModel.findByID(person.FaceImage);
            for (LocalImage localImage : byID) {
                FileUtils.delete(localImage.ImagePath);
            }
            LocalImageModel.delete(person.FaceImage);
            PersonModel.delete(person);
        }
        return MxResponse.CreateSuccess();
    }
}
