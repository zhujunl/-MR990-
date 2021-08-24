package com.miaxis.attendance.service.bean;

import com.miaxis.common.utils.StringUtils;

/**
 * @author Tank
 * @date 2021/8/23 11:53 上午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class User {

    public long id;
    public String name;//姓名（唯一，汉字）
    public String job_no;//工号（唯一，大小写字母和数字）
    public boolean ia_admin;//是否是管理员 0-否 1-是
    public String department_id;//部门id
    public String password;//密码（大小写字母，数字，特殊字符）
    public String base_pic;//底图
    public String pass_number;//当日通行次数（每日零点更新）
    public String id_number;//身份证号
    public String birthday;//生日
    public String is_delete;//是否删除 0-否 1-是
    public String create_time;//创建时间
    public String update_time;//修改时间

    public User() {
    }

    public boolean isIllegal() {
        return this.id <= 0 ||
                StringUtils.isNullOrEmpty(this.name) ||
                StringUtils.isNullOrEmpty(this.job_no) ||
                StringUtils.isNullOrEmpty(this.base_pic) ||
                StringUtils.isNullOrEmpty(this.id_number);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", job_no='" + job_no + '\'' +
                ", ia_admin=" + ia_admin +
                ", department_id='" + department_id + '\'' +
                ", password='" + password + '\'' +
                ", base_pic='" + base_pic + '\'' +
                ", pass_number='" + pass_number + '\'' +
                ", id_number='" + id_number + '\'' +
                ", birthday='" + birthday + '\'' +
                ", is_delete='" + is_delete + '\'' +
                ", create_time='" + create_time + '\'' +
                ", update_time='" + update_time + '\'' +
                '}';
    }

}


