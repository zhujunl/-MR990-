package com.miaxis.attendance.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author Tank
 * @date 2021/8/23 11:53 上午
 * @des
 * @updateAuthor
 * @updateDes
 */
@Entity
public class Person {

    @PrimaryKey(autoGenerate = true)
    public long id;
    /**
     * 用户ID
     */
    public String UserId;//用户ID
    /**
     * 姓名
     */
    public String Name;//姓名
    /**
     * 工号
     */
    public String Number;//工号
    /**
     * 性别
     */
    public String Gender;//性别
    /**
     * 身份证号码
     */
    public String IdCardNumber;//身份证号码
    /**
     * 是否启用
     */
    public boolean Enable;//是否启用
    /**
     * 人脸图片ID
     */
    public long FaceImage;//人脸图片ID
    public long FaceID;//人脸特征ID

    public String create_time;//创建时间
    public String update_time;//修改时间

    public Person() {
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", UserId='" + UserId + '\'' +
                ", Name='" + Name + '\'' +
                ", Number='" + Number + '\'' +
                ", Gender='" + Gender + '\'' +
                ", IdCardNumber='" + IdCardNumber + '\'' +
                ", Enable=" + Enable +
                ", FaceImage=" + FaceImage +
                ", FaceID=" + FaceID +
                ", create_time='" + create_time + '\'' +
                ", update_time='" + update_time + '\'' +
                '}';
    }
}
