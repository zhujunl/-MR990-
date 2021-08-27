package com.miaxis.attendance;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.miaxis.attendance.api.HttpApi;
import com.miaxis.attendance.callback.ActivityCallbacks;
import com.miaxis.attendance.config.AppConfig;
import com.miaxis.attendance.data.AppDataBase;
import com.miaxis.attendance.device.MR990Device;
import com.miaxis.attendance.task.UploadAttendance;
import com.miaxis.common.utils.FileUtils;

import org.zz.api.MXFaceIdAPI;
import org.zz.api.MXResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Tank
 * @date 2021/8/19 5:39 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class App extends Application {

    private static final String TAG = "App";
    private static App context;
    public ExecutorService threadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    private final UploadAttendance mUploadAttendance = new UploadAttendance();

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        registerActivityLifecycleCallbacks(new ActivityCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                if (activity.getClass() == MainActivity.class) {
                    MR990Device.getInstance().CameraPower(true);
                }
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                if (activity.getClass() == MainActivity.class) {
                    MR990Device.getInstance().CameraPower(false);
                    System.exit(0);
                }
            }
        });
    }

    public static App getInstance() {
        return context;
    }

    public MXResult<?> init() {
        boolean initFile = FileUtils.initFile(AppConfig.Path_DataBase);
        if (!initFile) {
            return MXResult.CreateFail(-2, "初始化文件错误");
        }
        AppDataBase.getInstance().init(AppConfig.Path_DataBase, this);
        HttpApi.init(this);
        return MXFaceIdAPI.getInstance().mxInitAlg(this, null, null);
    }

    public void startUploadAttendance() {
        if (!this.mUploadAttendance.isRunning()) {
            this.threadExecutor.execute(this.mUploadAttendance);
        }
    }
}


