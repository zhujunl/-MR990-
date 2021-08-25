package com.miaxis.attendance.ui.bar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.miaxis.attendance.MainViewModel;
import com.miaxis.attendance.R;
import com.miaxis.attendance.data.bean.AttendanceBean;
import com.miaxis.attendance.databinding.FragmentBarBinding;
import com.miaxis.common.activity.BaseBindingFragment;
import com.miaxis.common.utils.HardWareUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class BarFragment extends BaseBindingFragment<FragmentBarBinding> {

    private final NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();

    public static BarFragment newInstance() {
        return new BarFragment();
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_bar;
    }

    @Override
    protected void initView(@NonNull FragmentBarBinding binding, @Nullable Bundle savedInstanceState) {
        BarViewModel viewModel = new ViewModelProvider(this).get(BarViewModel.class);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getActivity().registerReceiver(networkChangeReceiver, intentFilter);
        binding.tvIp.setText(HardWareUtils.getHostIP());

        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        mainViewModel.startService.setValue(true);
        mainViewModel.httpServerStatus.observe(this, integer -> {
            switch (integer) {
                case 1:
                    binding.ivCloud.setImageResource(R.drawable.ic_baseline_cloud_done);
                    break;
                case -1:
                    binding.ivCloud.setImageResource(R.drawable.ic_baseline_cloud_error);
                    break;
                default:
                    binding.ivCloud.setImageResource(R.drawable.ic_baseline_cloud_off);
            }
        });
        mainViewModel.mAttendance.observe(this, new Observer<AttendanceBean>() {
            @Override
            public void onChanged(AttendanceBean attendance) {
//                Glide.with(binding.ivImage).load(attendance.BaseImage)
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(networkChangeReceiver);
    }

    static class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, "网络状态改变", Toast.LENGTH_SHORT).show();
        }
    }
}