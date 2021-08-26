package com.miaxis.attendance.api;


import android.content.Context;

import com.miaxis.attendance.api.bean.UserBean;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class HttpApi {

    public static final String BaseUrl = "http://192.168.5.164:8080/policebus/";

    public static void init(Context context) {
        BaseAPI.getInstance().init(context);
    }

    public static Call<HttpResponse<List<UserBean>>> getUserList() {
        return BaseAPI.getInstance().getHttpInterface(BaseUrl).getUserList();
    }

    public static Call<HttpResponse<String>> uploadImage(File file) {
        MultipartBody.Part fileBody = null;
        if (file != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            fileBody = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        }
        return BaseAPI.getInstance().getHttpInterface(BaseUrl).uploadImage(fileBody);
    }

}
