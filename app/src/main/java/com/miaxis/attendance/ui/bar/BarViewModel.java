package com.miaxis.attendance.ui.bar;

import com.miaxis.attendance.service.HttpServer;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;
import fi.iki.elonen.NanoHTTPD;

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
            this.httpServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT * 10, false);
            this.httpServerStatus.setValue(1);
        } catch (Exception e) {
            e.printStackTrace();
            this.httpServerStatus.setValue(-1);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void stopHttpServer() {
        if (this.httpServer != null) {
            this.httpServer.stop();
            this.httpServer = null;
            this.httpServerStatus.setValue(0);
        }
    }

}