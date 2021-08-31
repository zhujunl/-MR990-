package com.miaxis.attendance.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Finger {

    @PrimaryKey(autoGenerate = true)
    public long id;
    /**
     * 用户ID
     */
    public String UserId;//用户ID
    public byte[] FingerFeature;
    public long create_time;//创建时间
    public long update_time;//修改时间

    public Finger() {
        this.create_time=System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Finger{" +
                "id=" + id +
                ", UserId='" + UserId + '\'' +
                ", FingerFeature=" + (FingerFeature == null ? null : FingerFeature.length) +
                ", create_time=" + create_time +
                ", update_time=" + update_time +
                '}';
    }
}
