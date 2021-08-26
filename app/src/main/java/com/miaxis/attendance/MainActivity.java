package com.miaxis.attendance;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import com.miaxis.attendance.databinding.ActivityMainBinding;
import com.miaxis.attendance.ui.advertising.AdvertisingFragment;
import com.miaxis.attendance.ui.permission.PermissionFragment;
import com.miaxis.common.activity.BaseBindingFragmentActivity;
import com.miaxis.common.camera.CameraHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends BaseBindingFragmentActivity<ActivityMainBinding> {

    private MainViewModel mainViewModel;

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(@NonNull ActivityMainBinding binding, @Nullable Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE};
        replace(R.id.container, PermissionFragment.newInstance(permissions));
        mainViewModel.showAdvertising.observe(this, aBoolean -> {
            if (aBoolean) {
                replace(R.id.container, AdvertisingFragment.newInstance());
            }
        });

        mainViewModel.startService.observe(this, aBoolean -> {
            if (aBoolean) {
                mainViewModel.startHttpServer(8090);
            }
        });
        mainViewModel.httpServerStatus.observe(this, integer -> {
            switch (integer) {
                case 1:
                    Toast.makeText(MainActivity.this, "服务已启动", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(MainActivity.this, "服务启动失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(MainActivity.this, "服务已关闭", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mainViewModel != null) {
            mainViewModel.stopHttpServer();
            mainViewModel.destroy();
        }
        CameraHelper.getInstance().free();
    }
}