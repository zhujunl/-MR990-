package com.miaxis.attendance.data.bean;

/**
 * @author Tank
 * @date 2021/8/23 1:34 下午
 * @des
 * @updateAuthor
 * @updateDes
 */

public class AttendanceBean {

    public int Mode;//考勤方式  0无  1人脸   2指纹
    public String UserId;//用户ID(Person)
    public String BaseImage;//底图
    public String CaptureImage;//现场对比图
    public int Status;//考勤状态  0无  1成功   2失败
    public String create_time;//创建时间
    public String update_time;//修改时间

    public AttendanceBean() {
    }

    @Override
    public String toString() {
        return "Attendance{" +
                ", Mode=" + Mode +
                ", UserId='" + UserId + '\'' +
                ", BaseImage=" + BaseImage +
                ", CaptureImage=" + CaptureImage +
                ", Status=" + Status +
                ", create_time='" + create_time + '\'' +
                ", update_time='" + update_time + '\'' +
                '}';
    }
}
