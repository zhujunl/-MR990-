package com.miaxis.attendance;

import android.app.Application;

import com.miaxis.attendance.api.HttpApi;
import com.miaxis.attendance.data.AppDataBase;

/**
 * @author Tank
 * @date 2021/8/19 5:39 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class App extends Application {

    private static App context;

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


