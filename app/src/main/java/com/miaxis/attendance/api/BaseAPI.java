package com.miaxis.attendance.api;

import android.content.Context;
import android.text.TextUtils;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.miaxis.attendance.BuildConfig;
import com.miaxis.common.utils.HardWareUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BaseAPI {

    private final ConcurrentHashMap<String, Retrofit> clients = new ConcurrentHashMap<>();

    private final Retrofit.Builder RETROFIT_BUILDER = new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

    //    private final OkHttpClient OKHTTP_CLIENT = new OkHttpClient.Builder()
    //            .connectTimeout(20, TimeUnit.SECONDS)
    //            .readTimeout(20, TimeUnit.SECONDS)
    //            .writeTimeout(20, TimeUnit.SECONDS)
    //            //builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
    //            //builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
    //            .build();

    private String DevicesId = "";
    private String MacAddress = "";

    private BaseAPI() {
    }

    public static BaseAPI getInstance() {
        return BaseAPI.SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final BaseAPI instance = new BaseAPI();
    }

    public void init(Context context) {
        this.DevicesId = HardWareUtils.getDeviceId(context);
        this.MacAddress = HardWareUtils.getWifiMac(context);
    }

    private synchronized Retrofit getRetrofit(String baseUrl) {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new NullPointerException("Base url can not be null or empty");
        }
        Retrofit retrofit = this.clients.get(baseUrl);
        if (retrofit != null) {
            return retrofit;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                // 设置拦截器，添加统一的请求头
                .addInterceptor(chain -> {
                    // 以拦截到的请求为基础创建一个新的请求对象，然后插入Header
                    Request request = chain.request().newBuilder()
                            .addHeader("device_id", DevicesId)
                            .addHeader("mac_address", MacAddress)
                            .addHeader("version_name", BuildConfig.VERSION_NAME)
                            .addHeader("build_type", BuildConfig.BUILD_TYPE)
                            .addHeader("application_id", BuildConfig.APPLICATION_ID)
                            .build();
                    // 开始请求
                    return chain.proceed(request);
                })
                //builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
                //builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
                .build();
        retrofit = this.RETROFIT_BUILDER
                .client(client)
                .baseUrl(baseUrl)
                .build();
        this.clients.put(baseUrl, retrofit);
        return retrofit;
    }

    public HttpInterface getHttpInterface(String baseUrl) {
        return getRetrofit(baseUrl).create(HttpInterface.class);
    }


}
