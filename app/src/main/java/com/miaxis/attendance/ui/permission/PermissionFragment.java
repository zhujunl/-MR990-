package com.miaxis.attendance.ui.permission;

import android.os.Bundle;

import com.miaxis.attendance.R;
import com.miaxis.attendance.databinding.FragmentPermissionBinding;
import com.miaxis.attendance.ui.main.MainFragment;
import com.miaxis.common.activity.BaseBindingFragment;
import com.tbruyelle.rxpermissions3.RxPermissions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

public class PermissionFragment extends BaseBindingFragment<FragmentPermissionBinding> {

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
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Throwable {
                        if (aBoolean) {
                            replaceParent(R.id.container, MainFragment.newInstance());
                        } else {
                            new AlertDialog.Builder(getContext()).setMessage("请授权后使用。").setPositiveButton("退出", (dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            }).create().show();
                        }
                    }
                });
    }

}