package com.miaxis.attendance.api;


import java.io.File;

import retrofit2.Call;

public class PostalApi extends BaseAPI {

    public static Call<ResponseEntity> saveOrderPhoto(File file) {
//        MultipartBody.Part fileBody = null;
//        if (file != null) {
//            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//            fileBody = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
//        }
//        return getPostalNetSync().saveOrderPhoto(fileBody);
        return null;
    }





}
