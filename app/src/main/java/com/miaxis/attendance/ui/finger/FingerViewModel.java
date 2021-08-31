package com.miaxis.attendance.ui.finger;

import androidx.lifecycle.ViewModel;

public class FingerViewModel extends ViewModel {

    public FingerViewModel() {
    }

    public void readFinger(MR990FingerStrategy.ReadFingerCallBack readFingerCallBack){
        MR990FingerStrategy.getInstance().readFinger(readFingerCallBack);
    }

    public void stopRead() {
        MR990FingerStrategy.getInstance().stopRead();
    }
}