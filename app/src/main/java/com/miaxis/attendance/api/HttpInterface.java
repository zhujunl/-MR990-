package com.miaxis.attendance.api;


import com.miaxis.attendance.api.bean.UserBean;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface HttpInterface {

    /**
     * 获取用户列表
     */
    @FormUrlEncoded
    @POST("api/app/user/list")
    Call<HttpResponse<List<UserBean>>> getUserList();


    /**
     * 上传图片
     *
     * @return 图片地址
     */
    @Multipart
    @POST("api/file/upload")
    Call<HttpResponse<String>> uploadImage(
            @Part MultipartBody.Part file
    );

    /**
     * 上传考勤记录
     *
     * @param status 识别状态 0-成功 1-失败
     * @param type   通行类型（0-面部识别 1-指纹识别）
     * @param url    图片地址
     */
    @FormUrlEncoded
    @POST("api/app/attendance/add")
    Call<HttpResponse<List<UserBean>>> uploadAttendance(@Field("userId") String userId,
                                                        @Field("status") int status,
                                                        @Field("direction") int direction,
                                                        @Field("attendanceDeviceId") String attendanceDeviceId,
                                                        @Field("attendanceTime") String attendanceTime,
                                                        @Field("address") String address,
                                                        @Field("type") int type,
                                                        @Field("url") String url
    );


}
