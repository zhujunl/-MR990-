package com.miaxis.attendance.data.model;


import com.miaxis.attendance.data.AppDataBase;
import com.miaxis.attendance.data.entity.Finger;
import com.miaxis.common.utils.ListUtils;

import java.util.List;

public class FingerModel {

    //private static CopyOnWriteArrayList<Face> FaceCache = new CopyOnWriteArrayList<>();

    public static long insert(Finger finger) {
        return AppDataBase.getInstance().FingerDao().insert(finger);
    }

    public static void update(Finger finger) {
        AppDataBase.getInstance().FingerDao().update(finger);
    }

    public static void delete(Finger finger) {
        AppDataBase.getInstance().FingerDao().delete(finger);
    }

    public static int delete(String userId) {
        return AppDataBase.getInstance().FingerDao().delete(userId);
    }

    public static void delete(List<Finger> fingers) {
        if (!ListUtils.isNullOrEmpty(fingers)) {
            for (Finger finger : fingers) {
                AppDataBase.getInstance().FingerDao().delete(finger);
            }
        }
    }

    public static int delete(long id) {
        return AppDataBase.getInstance().FingerDao().delete(id);
    }

    public static void deleteAll() {
        AppDataBase.getInstance().FingerDao().deleteAll();
    }

    public static List<Finger> findAll() {
        return AppDataBase.getInstance().FingerDao().findAll();
    }

    public static int allCounts() {
        return AppDataBase.getInstance().FingerDao().allCounts();
    }

    public static List<Finger> findByUserID(String userID) {
        return AppDataBase.getInstance().FingerDao().findByUserID(userID);
    }

    public static List<Finger> findPage(int pageSize, int offset) {
        return AppDataBase.getInstance().FingerDao().findPage(pageSize, offset);
    }

}
