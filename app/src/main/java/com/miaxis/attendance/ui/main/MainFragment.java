package com.miaxis.attendance.ui.main;

import android.os.Bundle;

import com.miaxis.attendance.R;
import com.miaxis.attendance.databinding.FragmentMainBinding;
import com.miaxis.attendance.ui.bar.BarFragment;
import com.miaxis.attendance.ui.preview.PreviewFragment;
import com.miaxis.common.activity.BaseBindingFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class MainFragment extends BaseBindingFragment<FragmentMainBinding> {

    private MainViewModel mViewModel;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView(@NonNull FragmentMainBinding binding, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        replaceChild(R.id.fl_preview, PreviewFragment.newInstance());

        replaceChild(R.id.fl_content, BarFragment.newInstance());



    }

}