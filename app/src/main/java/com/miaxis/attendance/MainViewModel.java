package com.miaxis.attendance;

import com.miaxis.attendance.data.bean.AttendanceBean;
import com.miaxis.attendance.service.HttpServer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    public MutableLiveData<Integer> httpServerStatus = new MutableLiveData<>(0);
    public MutableLiveData<Boolean> showAdvertising = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> startService = new MutableLiveData<>(false);
    public MutableLiveData<AttendanceBean> mAttendance = new MutableLiveData<>();

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
}