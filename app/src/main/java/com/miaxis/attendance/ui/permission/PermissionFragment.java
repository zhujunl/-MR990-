package com.miaxis.attendance.ui.permission;

import android.os.Bundle;
import android.util.Log;
import com.miaxis.attendance.App;
import com.miaxis.attendance.R;
import com.miaxis.attendance.databinding.FragmentPermissionBinding;
import com.miaxis.attendance.ui.home.HomeFragment;
import com.miaxis.common.activity.BaseBindingFragment;
import com.tbruyelle.rxpermissions3.RxPermissions;
import org.zz.api.MXResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import io.reactivex.rxjava3.disposables.Disposable;

public class PermissionFragment extends BaseBindingFragment<FragmentPermissionBinding> {

    private static final String TAG = "PermissionFragment";

    public static PermissionFragment newInstance(String[] permissions) {
        return new PermissionFragment(permissions);
    }

    private final String[] permissions;

    private PermissionFragment(String[] permissions) {
        this.permissions = permissions;
        if (this.permissions == null || this.permissions.length == 0) {
            throw new IllegalArgumentException("permissions can not be null or empty");
        }
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_permission;
    }

    @Override
    protected void initView(@NonNull FragmentPermissionBinding binding, @Nullable Bundle savedInstanceState) {
        Disposable subscribe = new RxPermissions(this).request(this.permissions)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        MXResult<?> initAlg = App.getInstance().init();
                        Log.e(TAG, "initAlg:" + initAlg);
                        if (MXResult.isSuccess(initAlg)) {
                            replaceParent(R.id.container, HomeFragment.newInstance());
                        } else {
                            new AlertDialog.Builder(getContext()).setMessage("初始化失败。").setPositiveButton("退出", (dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            }).create().show();
                        }
                    } else {
                        new AlertDialog.Builder(getContext()).setMessage("请授权后使用。").setPositiveButton("退出", (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        }).create().show();
                    }
                });
    }

}