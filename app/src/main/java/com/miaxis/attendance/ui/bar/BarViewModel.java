package com.miaxis.attendance.ui.bar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BarViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    public MutableLiveData<Integer> httpServerStatus = new MutableLiveData<>(0);

    public BarViewModel() {
    }



}