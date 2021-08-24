package com.miaxis.attendance;

import android.Manifest;
import android.os.Bundle;

import com.miaxis.attendance.databinding.ActivityMainBinding;
import com.miaxis.attendance.ui.permission.PermissionFragment;
import com.miaxis.common.activity.BaseBindingFragmentActivity;
import com.miaxis.common.camera.CameraHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainActivity extends BaseBindingFragmentActivity<ActivityMainBinding> {

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(@NonNull ActivityMainBinding binding, @Nullable Bundle savedInstanceState) {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE};
        replace(R.id.container, PermissionFragment.newInstance(permissions));
        //MyMqttService.startService(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        CameraHelper.getInstance().free();
    }
}