package com.miaxis.attendance.data.model;


import com.miaxis.attendance.data.AppDataBase;
import com.miaxis.attendance.data.entity.LocalImage;
import com.miaxis.common.utils.FileUtils;

import java.util.Collection;
import java.util.List;

public class LocalImageModel {

    public static long insert(LocalImage localImage) {
        if (localImage != null) {
            localImage.create_time = System.currentTimeMillis();
        }
        long insert = AppDataBase.getInstance().LocalImageDao().insert(localImage);
        if (localImage != null) {
            localImage.id = insert;
        }
        return insert;
    }

    public static void update(LocalImage localImage) {
        AppDataBase.getInstance().LocalImageDao().update(localImage);
    }

    public static void delete(LocalImage localImage) {
        if (localImage != null) {
            FileUtils.delete(localImage.LocalPath);
        }
        AppDataBase.getInstance().LocalImageDao().delete(localImage);
    }

    public static void delete(long id) {
        AppDataBase.getInstance().LocalImageDao().delete(id);
    }

    public static void deleteList(Collection<LocalImage> list) {
        if (list != null && !list.isEmpty()) {
            for (LocalImage localImage : list) {
                delete(localImage);
            }
        }
    }

    public static void deleteAll() {
        AppDataBase.getInstance().LocalImageDao().deleteAll();
    }

    public static List<LocalImage> findAll() {
        return AppDataBase.getInstance().LocalImageDao().findAll();
    }

    public static int allCounts() {
        return AppDataBase.getInstance().LocalImageDao().allCounts();
    }

    public static List<LocalImage> findByID(long id) {
        return AppDataBase.getInstance().LocalImageDao().findByID(id);
    }

    public static List<LocalImage> findByLocalPath(String localPath) {
        return AppDataBase.getInstance().LocalImageDao().findByLocalPath(localPath);
    }

    public static List<LocalImage> findByRemotePath(String remotePath) {
        return AppDataBase.getInstance().LocalImageDao().findByRemotePath(remotePath);
    }

    public static List<LocalImage> findPage(int pageSize, int offset) {
        return AppDataBase.getInstance().LocalImageDao().findPage(pageSize, offset);
    }

}
