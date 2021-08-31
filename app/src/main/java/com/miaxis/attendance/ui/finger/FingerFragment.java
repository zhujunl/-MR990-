package com.miaxis.attendance.ui.finger;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.miaxis.attendance.R;
import com.miaxis.attendance.data.entity.Finger;
import com.miaxis.attendance.databinding.FragmentFingerBinding;
import com.miaxis.common.activity.BaseBindingFragment;
import com.mx.finger.common.MxImage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class FingerFragment extends BaseBindingFragment<FragmentFingerBinding> implements MR990FingerStrategy.ReadFingerCallBack {

    private FingerViewModel mViewModel;

    public static FingerFragment newInstance() {
        return new FingerFragment();
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_finger;
    }

    @Override
    protected void initView(@NonNull FragmentFingerBinding binding, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(FingerViewModel.class);
        mViewModel.readFinger(this);
    }

    @Override
    public void onReadFinger(MxImage finger) {

    }

    @Override
    public void onExtractFeature(MxImage finger, byte[] feature) {

    }

    @Override
    public void onFeatureMatch(MxImage image, byte[] feature, Finger finger, Bitmap bitmap) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.stopRead();
    }
}