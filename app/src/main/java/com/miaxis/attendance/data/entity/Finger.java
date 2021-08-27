package com.miaxis.attendance.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Finger {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String UserId;
    public String FingerImage;
    public byte[] FingerFeature;
    public long create_time;//创建时间
    public long update_time;//修改时间

    public Finger() {
        this.create_time=System.currentTimeMillis();
    }


}
