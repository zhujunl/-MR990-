package com.miaxis.attendance.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Face {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String UserId;
    public byte[] FaceFeature;//人脸特征
    public long create_time;//创建时间
    public long update_time;//修改时间

    public Face() {
        this.create_time=System.currentTimeMillis();
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
