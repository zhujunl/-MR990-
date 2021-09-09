package com.miaxis.attendance;

import android.os.Handler;

import com.miaxis.attendance.config.AppConfig;
import com.miaxis.attendance.data.bean.AttendanceBean;
import com.miaxis.attendance.device.MR990Device;
import com.miaxis.attendance.service.HttpServer;
import com.miaxis.common.response.ZZResponse;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class MainViewModel extends ViewModel {


    private Handler mHandler_Door = new Handler();
    private Handler mHandler_Dog = new Handler();

    public MutableLiveData<Integer> httpServerStatus = new MutableLiveData<>(0);
    /**
     * 设备是否空闲，空闲时显示轮播
     */
    public MutableLiveData<Boolean> isIdle = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> startService = new MutableLiveData<>(false);
    public MutableLiveData<ZZResponse<AttendanceBean>> mAttendance = new MutableLiveData<>();
    //public MutableLiveData<Boolean> EnableNirProcess = new MutableLiveData<>();

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
        if (mHandler_Door != null) {
            mHandler_Door.removeCallbacksAndMessages(null);
            mHandler_Door.postDelayed(() -> MR990Device.getInstance().DoorPower(false), AppConfig.CloseDoorDelay);
        }
    }

    public void destroy() {
        timeOutCancel();
        if (mHandler_Door!=null){
            mHandler_Door.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 空闲倒计时重置
     */
    public void timeOutReset(boolean isStartTimeOut) {
        timeOutCancel();
        Timber.e("timeOutReset: %s", isStartTimeOut);
        if (isStartTimeOut) {
            if (this.mHandler_Dog != null) {
                this.mHandler_Dog.postDelayed(() -> {
                    isIdle.postValue(true);
                }, AppConfig.IdleTimeOut);
            }
        }
    }

    /**
     * 取消空闲倒计时
     */
    public void timeOutCancel() {
        Timber.e("timeOutCancel");
        if (this.mHandler_Dog != null) {
            this.mHandler_Dog.removeCallbacksAndMessages(null);
        }
    }

}