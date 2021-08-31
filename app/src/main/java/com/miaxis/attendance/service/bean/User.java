package com.miaxis.attendance.service.bean;

import com.miaxis.common.utils.StringUtils;

import java.util.List;

/**
 * @author Tank
 * @date 2021/8/23 11:53 上午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class User {

    public long id;
    public boolean ia_admin;//是否是管理员 0-否 1-是
    public String name;//姓名（唯一，汉字）
    public String id_number;//身份证号
    public String job_no;//工号（唯一，大小写字母和数字）
    public String department_id;//部门id
    public String url_face;//人脸底图(图片URL)
    public List<User.Finger> url_fingers;//指纹底图(图片URL)
    public String is_delete;//是否删除 0-否 1-是
    public String create_time;//创建时间
    public String update_time;//修改时间

    public User() {
    }

    public boolean isIllegal() {
        return this.id <= 0 ||
                StringUtils.isNullOrEmpty(this.name) ||
                StringUtils.isNullOrEmpty(this.job_no) ||
                StringUtils.isNullOrEmpty(this.id_number);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", ia_admin=" + ia_admin +
                ", name='" + name + '\'' +
                ", id_number='" + id_number + '\'' +
                ", job_no='" + job_no + '\'' +
                ", department_id='" + department_id + '\'' +
                ", url_face=" + url_face +
                ", url_fingers=" + url_fingers +
                ", is_delete='" + is_delete + '\'' +
                ", create_time='" + create_time + '\'' +
                ", update_time='" + update_time + '\'' +
                '}';
    }

    public static class Finger {

        public long userId;
        public int position;
        public String url;

        public Finger() {
        }

        public boolean isIllegal() {
            return this.userId <= 0 ||
                    this.position < 0 ||
                    this.position > 9 ||
                    StringUtils.isNullOrEmpty(this.url);
        }

        @Override
        public String toString() {
            return "Finger{" +
                    "userId=" + userId +
                    ", position=" + position +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

}


