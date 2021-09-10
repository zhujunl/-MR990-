package com.miaxis.attendance.ui.bar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.miaxis.attendance.MainViewModel;
import com.miaxis.attendance.R;
import com.miaxis.attendance.config.AppConfig;
import com.miaxis.attendance.data.bean.AttendanceBean;
import com.miaxis.attendance.databinding.FragmentBarBinding;
import com.miaxis.attendance.tts.TTSSpeechManager;
import com.miaxis.common.activity.BaseBindingFragment;
import com.miaxis.common.response.ZZResponse;
import com.miaxis.common.utils.HardWareUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class BarFragment extends BaseBindingFragment<FragmentBarBinding> {

    private static final String TAG = "BarFragment";
    private MainViewModel mMainViewModel;
    private Handler mHandler = new Handler();
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
        binding.tvIp.setText("本机IP：" + HardWareUtils.getHostIP());
        //viewModel.UserCounts.observe(this, integer -> binding.tvUserCounts.setText("人数：" + (integer == null ? "0 " : ("" + integer))));
        //viewModel.UserCounts.setValue(PersonModel.allCounts());
        mMainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        mMainViewModel.startService.setValue(true);
        mMainViewModel.httpServerStatus.observe(this, integer -> {
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

        mMainViewModel.mAttendance.observe(this, attendance -> {
            if (attendance == null) {
                return;
            }
            if (ZZResponse.isSuccess(attendance)) {
                mMainViewModel.openDoor();
                AttendanceBean attendanceData = attendance.getData();
                if (viewModel.isNewUser(attendance.getData())) {
                    Glide.with(binding.ivImage).load(attendanceData.CutImage).into(binding.ivImage);
                    binding.tvName.setText(String.valueOf(attendanceData.UserName));
                    TTSSpeechManager.getInstance().speak(AppConfig.WelcomeWords);
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.postDelayed(() -> {
                        Glide.with(binding.ivImage).load(R.drawable.logo).centerCrop().into(binding.ivImage);
                        binding.tvName.setText(R.string.text_name);
                        viewModel.setNewUserReset();
                    }, AppConfig.CloseDoorDelay);
                }
            } else {
                Toast.makeText(getContext(), "比对失败：" + attendance.getMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
        mMainViewModel.httpServerStatus.removeObservers(this);
        mMainViewModel.mAttendance.removeObservers(this);
        mMainViewModel.mAttendance.setValue(null);
        getActivity().unregisterReceiver(networkChangeReceiver);
    }

    static class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, "网络状态改变", Toast.LENGTH_SHORT).show();
        }
    }
}