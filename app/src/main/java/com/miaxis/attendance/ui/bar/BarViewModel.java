package com.miaxis.attendance.ui.bar;

import com.miaxis.attendance.service.HttpServer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BarViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    public MutableLiveData<Integer> httpServerStatus = new MutableLiveData<>(0);
    private HttpServer httpServer;

    public BarViewModel() {
    }

    public void startHttpServer(int port) {
        stopHttpServer();
        this.httpServer = new HttpServer(port);
        try {
            this.httpServer.start( );
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