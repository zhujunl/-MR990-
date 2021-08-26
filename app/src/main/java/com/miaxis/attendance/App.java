package com.miaxis.attendance;

import android.app.Application;

import com.miaxis.attendance.api.HttpApi;
import com.miaxis.attendance.config.AppConfig;
import com.miaxis.attendance.data.AppDataBase;
import com.miaxis.attendance.task.UploadAttendance;

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
    private UploadAttendance mUploadAttendance = new UploadAttendance();

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        AppDataBase.getInstance().init(AppConfig.Path_DataBase, this);
        HttpApi.init(this);
    }

    public static App getInstance() {
        return context;
    }


    public void startUploadAttendance() {
        if (!this.mUploadAttendance.isRunning()) {
            this.threadExecutor.execute(this.mUploadAttendance);
        }
    }
}


