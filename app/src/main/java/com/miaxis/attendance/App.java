package com.miaxis.attendance;

import android.app.Application;

import com.miaxis.attendance.api.HttpApi;
import com.miaxis.attendance.data.AppDataBase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Tank
 * @date 2021/8/19 5:39 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class App extends Application {

    private static App context;
    public ExecutorService threadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        AppDataBase.getInstance().init( "attendance.db",this);
        HttpApi.init(this);
    }

    public static App getInstance() {
        return context;
    }


}


