package com.miaxis.attendance.ui.finger;

import android.graphics.Bitmap;

import com.miaxis.attendance.data.bean.AttendanceBean;
import com.miaxis.attendance.data.entity.Finger;
import com.miaxis.common.response.ZZResponse;
import com.mx.finger.common.MxImage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FingerViewModel extends ViewModel implements MR990FingerStrategy.ReadFingerCallBack {

    MutableLiveData<Boolean> StartCountdown = new MutableLiveData<>(true);
    MutableLiveData<ZZResponse<AttendanceBean>> mAttendance = new MutableLiveData<>();

    public FingerViewModel() {
    }

    public void readFinger() {
        MR990FingerStrategy.getInstance().readFinger(this);
    }

    public void stopRead() {
        MR990FingerStrategy.getInstance().stopRead();
    }

    public void resume() {
        this.StartCountdown.setValue(true);
    }

    public void pause() {
        this.StartCountdown.setValue(false);
    }

    @Override
    public void onReadFinger(MxImage finger) {
        this.StartCountdown.postValue(true);
    }

    @Override
    public void onExtractFeature(MxImage finger, byte[] feature) {

    }

    @Override
    public void onFeatureMatch(MxImage image, byte[] feature, Finger finger, Bitmap bitmap) {


        this.mAttendance.postValue(ZZResponse.CreateSuccess());
    }

}