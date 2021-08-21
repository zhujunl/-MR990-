package com.miaxis.attendance.api;


import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface HttpApi {

    //获取设备状态
    @FormUrlEncoded
    @POST("api/v1/device/getDeviceStatus")
    Call<ResponseEntity<String>> getDeviceStatusSync(@Field("macAddress") String macAddress);


    @Multipart
    @POST("api/v1/order/updateOrderFromApp")
    Call<ResponseEntity> updateOrderFromApp(
            @Part("sendAddress") String sendAddress,
            @Part("sendPhone") String sendPhone,
            @Part("sendName") String sendName,
            @Part("orderCode") String orderCode,
            @Part("goodsName") String goodsName,
            @Part("weight") String weight,
            @Part("addresseeName") String addresseeName,
            @Part("addresseeAddress") String addresseeAddress,
            @Part("addresseePhone") String addresseePhone,
            @Part List<MultipartBody.Part> file
    );

    /**
     * 上传图片
     */
    @Multipart
    @POST("api/v1/order/saveOrderPhoto")
    Call<ResponseEntity> saveOrderPhoto(
            @Part MultipartBody.Part file
    );


    @Multipart
    @POST("api/v1/order/deleteOrderPhoto")
    Call<ResponseEntity> deletePhoto(
            @Part("path") String path
    );


}
