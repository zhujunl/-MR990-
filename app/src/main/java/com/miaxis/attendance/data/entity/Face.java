package com.miaxis.attendance.data.entity;

import android.text.TextUtils;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Face {

    @PrimaryKey(autoGenerate = true)
    public long id;
    /**
     * 用户ID
     */
    public String UserId;//用户ID
    public byte[] FaceFeature;//人脸特征
    public long create_time;//创建时间
    public long update_time;//修改时间

    public Face() {
        this.create_time = System.currentTimeMillis();
    }

    public boolean isIllegal() {
        return TextUtils.isEmpty(UserId) || FaceFeature == null || FaceFeature.length <= 0;
    }

    public static boolean isIllegal(Face face) {
        return face == null || face.isIllegal();
    }

    @Override
    public String toString() {
        return "Face{" +
                "id=" + id +
                ", UserId='" + UserId + '\'' +
                ", FaceFeature=" + (FaceFeature == null ? null : FaceFeature.length) +
                '}';
    }

}
