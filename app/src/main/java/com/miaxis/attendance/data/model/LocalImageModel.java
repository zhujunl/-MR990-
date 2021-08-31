package com.miaxis.attendance.data.model;


import com.miaxis.attendance.data.AppDataBase;
import com.miaxis.attendance.data.entity.LocalImage;

import java.util.List;

public class LocalImageModel {

    public static long insert(LocalImage localImage) {
       return AppDataBase.getInstance().LocalImageDao().insert(localImage);
    }

    public static void update(LocalImage localImage) {
        AppDataBase.getInstance().LocalImageDao().update(localImage);
    }

    public static void delete(LocalImage localImage) {
        AppDataBase.getInstance().LocalImageDao().delete(localImage);
    }

    public static void delete(long id) {
        AppDataBase.getInstance().LocalImageDao().delete(id);
    }

    public static void delete(String userId) {
        AppDataBase.getInstance().LocalImageDao().delete(userId);
    }

    public static void deleteFace(String userId) {
        AppDataBase.getInstance().LocalImageDao().deleteFace(userId);
    }

    public static void deleteFinger(String userId) {
        AppDataBase.getInstance().LocalImageDao().deleteFinger(userId);
    }

    public static void deleteAll() {
        AppDataBase.getInstance().LocalImageDao().deleteAll();
    }

    public static List<LocalImage> findAll() {
        return AppDataBase.getInstance().LocalImageDao().findAll();
    }

    public static List<LocalImage> findFaceImageByUserId(String UserId) {
        return AppDataBase.getInstance().LocalImageDao().findFaceImageByUserId(UserId);
    }

    public static List<LocalImage> findFingerImageByUserId(String UserId) {
        return AppDataBase.getInstance().LocalImageDao().findFingerImageByUserId(UserId);
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
