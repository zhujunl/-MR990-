package com.miaxis.attendance;

import android.os.Handler;

import com.miaxis.attendance.config.AppConfig;
import com.miaxis.attendance.data.bean.AttendanceBean;
import com.miaxis.attendance.device.MR990Device;
import com.miaxis.attendance.service.HttpServer;
import com.miaxis.common.response.ZZResponse;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {


    private Handler mHandler = new Handler();
    public MutableLiveData<Integer> httpServerStatus = new MutableLiveData<>(0);
    public MutableLiveData<Boolean> showAdvertising = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> startService = new MutableLiveData<>(false);
    public MutableLiveData<ZZResponse<AttendanceBean>> mAttendance = new MutableLiveData<>();

    public MutableLiveData<Boolean> EnableNirProcess = new MutableLiveData<>();

    private HttpServer httpServer;

    public MainViewModel() {
    }

    public void startHttpServer(int port) {
        if (this.httpServerStatus.getValue() != null && this.httpServerStatus.getValue() == 1) {
            return;
        }
        stopHttpServer();
        this.httpServer = new HttpServer(port);
        try {
            this.httpServer.start();
            this.httpServerStatus.setValue(1);
        } catch (Exception e) {
            e.printStackTrace();
            this.httpServerStatus.setValue(-1);
        }
    }

    public void stopHttpServer() {
        if (this.httpServer != null) {
            this.httpServer.stop();
            this.httpServer = null;
            this.httpServerStatus.setValue(0);
        }
    }

    public void openDoor() {
        MR990Device.getInstance().DoorPower(true);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(() -> MR990Device.getInstance().DoorPower(false), AppConfig.CloseDoorDelay);
    }

    public void destroy() {
        mHandler.removeCallbacksAndMessages(null);
    }
}