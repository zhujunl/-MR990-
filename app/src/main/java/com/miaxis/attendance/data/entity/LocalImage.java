package com.miaxis.attendance.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author Tank
 * @date 2021/8/23 1:36 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
@Entity
public class LocalImage {

    @PrimaryKey(autoGenerate = true)
    public long id;
    /**
     * 图片类型  0无  1人脸图片(注册图片)   2人脸抓拍图片  3指纹图片(注册)  4指纹图片(采集)  5人脸截图
     */
    public int Type;
    public String ImagePath;
    public String create_time;//创建时间
    public String update_time;//修改时间

    public LocalImage() {
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", Type=" + Type +
                ", ImagePath='" + ImagePath + '\'' +
                '}';
    }

}
