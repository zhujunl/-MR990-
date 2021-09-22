package com.miaxis.attendance.data.model;


import com.miaxis.attendance.data.AppDataBase;
import com.miaxis.attendance.data.entity.LocalImage;
import com.miaxis.common.utils.FileUtils;
import com.miaxis.common.utils.ListUtils;

import java.util.Collection;
import java.util.List;

public class LocalImageModel {

    public static long insert(LocalImage localImage) {
        long insert = AppDataBase.getInstance().LocalImageDao().insert(localImage);
        localImage.id = insert;
        return insert;
    }

    public static void update(LocalImage localImage) {
        AppDataBase.getInstance().LocalImageDao().update(localImage);
    }

    public static void delete(LocalImage localImage) {
        if (localImage!=null){
            FileUtils.delete(localImage.LocalPath);
        }
        AppDataBase.getInstance().LocalImageDao().delete(localImage);
    }

    public static void delete(long id) {
        AppDataBase.getInstance().LocalImageDao().delete(id);
    }

    public static void delete(String userId) {
        AppDataBase.getInstance().LocalImageDao().delete(userId);
    }

    public static void deleteList(Collection<LocalImage> list) {
        if (list != null && !list.isEmpty()) {
            for (LocalImage localImage : list) {
                delete(localImage);
            }
        }
    }

    public static void deleteFaceImage(String userId) {
        AppDataBase.getInstance().LocalImageDao().deleteFace(userId);
    }

    public static void deleteFingerImage(String userId) {
        AppDataBase.getInstance().LocalImageDao().deleteFinger(userId);
    }

    public static void deleteAll() {
        AppDataBase.getInstance().LocalImageDao().deleteAll();
    }

    public static List<LocalImage> findAll() {
        return AppDataBase.getInstance().LocalImageDao().findAll();
    }

    public static LocalImage findFaceImageByUserId(String UserId) {
        List<LocalImage> faceImageByUserId = AppDataBase.getInstance().LocalImageDao().findFaceImageByUserId(UserId);
        if (ListUtils.isNullOrEmpty(faceImageByUserId)) {
            return null;
        }
        return faceImageByUserId.get(0);
    }

    public static LocalImage findFingerImageByUserId(String UserId) {
        List<LocalImage> fingerImageByUserId = AppDataBase.getInstance().LocalImageDao().findFingerImageByUserId(UserId);
        if (ListUtils.isNullOrEmpty(fingerImageByUserId)) {
            return null;
        }
        return fingerImageByUserId.get(0);
    }

    public static int allCounts() {
        return AppDataBase.getInstance().LocalImageDao().allCounts();
    }

    public static List<LocalImage> findByID(long id) {
        return AppDataBase.getInstance().LocalImageDao().findByID(id);
    }

    public static List<LocalImage> findByUserId(String userId) {
        return AppDataBase.getInstance().LocalImageDao().findByUserId(userId);
    }

    public static List<LocalImage> findPage(int pageSize, int offset) {
        return AppDataBase.getInstance().LocalImageDao().findPage(pageSize, offset);
    }

}
