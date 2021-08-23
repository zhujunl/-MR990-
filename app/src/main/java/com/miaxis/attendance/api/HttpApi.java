package com.miaxis.attendance.api;


import android.content.Context;

import com.miaxis.attendance.api.bean.UserBean;

import java.util.List;

import retrofit2.Call;

public class HttpApi {

    public static final String BaseUrl = "http://192.168.5.164:8080/policebus/";

    public static void init(Context context) {
        BaseAPI.getInstance().init(context);
    }

    public static Call<HttpResponse<List<UserBean>>> getUserList() {
        return BaseAPI.getInstance().getHttpInterface(BaseUrl).getUserList();
    }







}
