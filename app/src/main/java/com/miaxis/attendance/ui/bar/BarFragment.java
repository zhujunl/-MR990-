package com.miaxis.attendance.ui.bar;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.bumptech.glide.Glide;
import com.miaxis.attendance.MainViewModel;
import com.miaxis.attendance.R;
import com.miaxis.attendance.config.AppConfig;
import com.miaxis.attendance.data.bean.AttendanceBean;
import com.miaxis.attendance.databinding.FragmentBarBinding;
import com.miaxis.attendance.tts.TTSSpeechManager;
import com.miaxis.attendance.ui.bar.widget.ClickableLayout;
import com.miaxis.attendance.ui.manager.ManagerFragment;
import com.miaxis.common.activity.BaseBindingFragment;
import com.miaxis.common.response.ZZResponse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class BarFragment extends BaseBindingFragment<FragmentBarBinding> {

    private static final String TAG = "BarFragment";
    private MainViewModel mMainViewModel;
    private Handler mHandler = new Handler();

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
        //        replaceChild(R.id.fl_ip, NetFragment.newInstance());
        //viewModel.UserCounts.observe(this, integer ->
        // binding.tvUserCounts.setText("人数：" + (integer == null ? "0 " : ("" + integer))));
        //viewModel.UserCounts.setValue(PersonModel.allCounts());
        mMainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        mMainViewModel.startService.setValue(true);
        //        mMainViewModel.httpServerStatus.observe(this, integer -> {
        //            switch (integer) {
        //                case 1:
        //                    binding.ivCloud.setImageResource(R.drawable.ic_baseline_cloud_done);
        //                    break;
        //                case -1:
        //                    binding.ivCloud.setImageResource(R.drawable.ic_baseline_cloud_error);
        //                    break;
        //                default:
        //                    binding.ivCloud.setImageResource(R.drawable.ic_baseline_cloud_off);
        //            }
        //        });

        binding.ivCloud.setImageResource(R.drawable.ic_baseline_cloud_done);
        binding.ivCloud1.setImageResource(R.drawable.ic_baseline_cloud_error);
        binding.ivCloud2.setImageResource(R.drawable.ic_baseline_cloud_off);
        binding.ivCloud3.setImageResource(R.drawable.ic_baseline_cloud_done);


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
                    //getView().setBackgroundColor(0xFF32CD32);
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.postDelayed(() -> {
                        Glide.with(binding.ivImage).load(R.drawable.logo).centerCrop().into(binding.ivImage);
                        binding.tvName.setText("");
                        //getView().setBackgroundColor(getResources().getColor(R.color.blue));
                        //getView().setBackgroundResource(R.drawable.bg_bar);
                        viewModel.setNewUserReset();
                    }, AppConfig.CloseDoorDelay);
                }
            } else {
                //Toast.makeText(getContext(), "" + attendance.getMsg(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.clClick.setOnClickListener(new ClickableLayout.OnComboClickListener() {
            @Override
            protected void onComboClick(View v) {
                replaceParent(R.id.container, ManagerFragment.newInstance());
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
    }

    //    static class NetworkChangeReceiver extends BroadcastReceiver {
    //        @Override
    //        public void onReceive(Context context, Intent intent) {
    //            //Toast.makeText(context, "网络状态改变", Toast.LENGTH_SHORT).show();
    //        }
    //    }
}