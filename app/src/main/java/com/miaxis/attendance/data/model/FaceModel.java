package com.miaxis.attendance.data.model;


import com.miaxis.attendance.data.AppDataBase;
import com.miaxis.attendance.data.entity.Face;

import java.util.List;

public class FaceModel {

    public static long insert(Face face) {
       return AppDataBase.getInstance().FaceDao().insert(face);
    }

    public static void update(Face face) {
        AppDataBase.getInstance().FaceDao().update(face);
    }

    public static void delete(Face face) {
        AppDataBase.getInstance().FaceDao().delete(face);
    }

    public static void delete(String userId) {
        AppDataBase.getInstance().FaceDao().delete(userId);
    }

    public static void deleteAll() {
        AppDataBase.getInstance().FaceDao().deleteAll();
    }

    public static List<Face> findAll() {
        return AppDataBase.getInstance().FaceDao().findAll();
    }

    public static int allCounts() {
        return AppDataBase.getInstance().FaceDao().allCounts();
    }

    public static List<Face> findByUserID(String userID) {
        return AppDataBase.getInstance().FaceDao().findByUserID(userID);
    }

    public static List<Face> findPage(int pageSize, int offset) {
        return AppDataBase.getInstance().FaceDao().findPage(pageSize, offset);
    }

}
