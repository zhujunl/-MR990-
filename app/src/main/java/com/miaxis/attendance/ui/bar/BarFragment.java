package com.miaxis.attendance.ui.bar;

import android.os.Bundle;

import com.miaxis.attendance.R;
import com.miaxis.attendance.databinding.FragmentBarBinding;
import com.miaxis.common.activity.BaseBindingFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BarFragment extends BaseBindingFragment<FragmentBarBinding> {

    public static BarFragment newInstance() {
        return new BarFragment();
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_bar;
    }

    @Override
    protected void initView(@NonNull FragmentBarBinding binding, @Nullable Bundle savedInstanceState) {
        

    }

}