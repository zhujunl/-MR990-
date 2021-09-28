package com.miaxis.attendance.data.model;


import android.text.TextUtils;

import com.miaxis.attendance.data.AppDataBase;
import com.miaxis.attendance.data.entity.Finger;
import com.miaxis.common.utils.ListUtils;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FingerModel {

    private static final ConcurrentHashMap<String, Finger> FingerMapCache = new ConcurrentHashMap<>();

    public static void init() {
        List<Finger> all = AppDataBase.getInstance().FingerDao().findAll();
        if (ListUtils.isNullOrEmpty(all)) {
            return;
        }
        for (Finger finger : all) {
            if (finger != null && !TextUtils.isEmpty(finger.UserId)) {
                Finger put = FingerModel.FingerMapCache.put(finger.UserId, finger);
                if (put != null) {
                    long delete = AppDataBase.getInstance().FingerDao().delete(put);
                }
            }
        }
    }

    public static long insert(Finger finger) {
        if (Finger.isIllegal(finger)) {
            return -99;
        }
        long insert = AppDataBase.getInstance().FingerDao().insert(finger);
        if (insert > 0) {
            Finger put = FingerModel.FingerMapCache.put(finger.UserId, finger);
            if (put != null) {
                long delete = AppDataBase.getInstance().FingerDao().delete(put);
            }
        }
        finger.id = insert;
        return insert;
    }

    public static long update(Finger finger) {
        if (Finger.isIllegal(finger)) {
            return -99;
        }
        int update = AppDataBase.getInstance().FingerDao().update(finger);
        if (update > 0) {
            Finger put = FingerModel.FingerMapCache.put(finger.UserId, finger);
            if (put != null) {
                long delete = AppDataBase.getInstance().FingerDao().delete(put);
            }
        }
        return update;
    }

    private static long deleteCache(String userId) {
        if (userId == null) {
            return -99;
        }
        Finger put = FingerModel.FingerMapCache.remove(userId);
        if (put != null) {
            long delete = AppDataBase.getInstance().FingerDao().delete(put);
        }
        return put == null ? 0 : 1;
    }

    public static void delete(Finger finger) {
        int delete = AppDataBase.getInstance().FingerDao().delete(finger);
        //FingerModel.FingerMapCache.get()
    }

    public static int delete(String userId) {
        int delete = AppDataBase.getInstance().FingerDao().delete(userId);
        if (userId != null) {
            FingerModel.FingerMapCache.remove(userId);
        }
        return delete;
    }

    public static void delete(List<Finger> fingers) {
        if (!ListUtils.isNullOrEmpty(fingers)) {
            for (Finger finger : fingers) {
                delete(finger);
                if (finger.UserId != null) {
                    FingerModel.FingerMapCache.remove(finger.UserId);
                }
            }
        }
    }

    //public static int delete(long id) {
    //    return AppDataBase.getInstance().FingerDao().delete(id);
    //}

    public static void deleteAll() {
        FingerModel.FingerMapCache.clear();
        AppDataBase.getInstance().FingerDao().deleteAll();
    }

    public static HashMap<String, Finger> findAll() {
        return new HashMap<>(FingerModel.FingerMapCache);
    }

    public static int allCounts() {
        //return AppDataBase.getInstance().FingerDao().allCounts();
        return FingerModel.FingerMapCache.size();
    }

    public static List<Finger> findByUserID(String userId) {
        return AppDataBase.getInstance().FingerDao().findByUserID(userId);
        //        if (TextUtils.isEmpty(userId)) {
        //            return null;
        //        }
        //        return FingerModel.FingerMapCache.get(userId);
    }

    public static Finger findByID(long id) {
        List<Finger> byID = AppDataBase.getInstance().FingerDao().findByID(id);
        if (ListUtils.isNullOrEmpty(byID)) {
            return null;
        } else {
            return byID.get(0);
        }
        //        if (TextUtils.isEmpty(userId)) {
        //            return null;
        //        }
        //        return FingerModel.FingerMapCache.get(userId);
    }

    public static List<Finger> findPage(int pageSize, int offset) {
        return AppDataBase.getInstance().FingerDao().findPage(pageSize, offset);
    }

}
