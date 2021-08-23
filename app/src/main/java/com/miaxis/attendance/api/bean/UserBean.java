package com.miaxis.attendance.api.bean;

/**
 * @author Tank
 * @date 2021/8/23 4:23 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class UserBean {

    public long id;
    public String name;
    public String jobNo;
    public String department;
    public boolean isAdmin;
    public String basePic;
    public String birthday;
    public String idNumber;

    public UserBean() {
    }


    @Override
    public String toString() {
        return "UserBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", jobNo='" + jobNo + '\'' +
                ", department='" + department + '\'' +
                ", isAdmin=" + isAdmin +
                ", basePic='" + basePic + '\'' +
                ", birthday='" + birthday + '\'' +
                ", idNumber='" + idNumber + '\'' +
                '}';
    }

}
