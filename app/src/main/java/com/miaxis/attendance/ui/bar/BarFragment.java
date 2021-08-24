package com.miaxis.attendance.ui.bar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import com.miaxis.attendance.R;
import com.miaxis.attendance.databinding.FragmentBarBinding;
import com.miaxis.common.activity.BaseBindingFragment;
import com.miaxis.common.utils.HardWareUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class BarFragment extends BaseBindingFragment<FragmentBarBinding> {
    private BarViewModel mViewModel;

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
        mViewModel = new ViewModelProvider(this).get(BarViewModel.class);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getActivity().registerReceiver(networkChangeReceiver, intentFilter);
        binding.tvIp.setText(HardWareUtils.getHostIP());

        mViewModel.startHttpServer(8090);
        mViewModel.httpServerStatus.observe(this, integer -> {
            switch (integer) {
                case 1:
                    binding.ivCloud.setImageResource(R.drawable.ic_baseline_cloud_done);
                    Toast.makeText(getContext(), "服务已启动", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    binding.ivCloud.setImageResource(R.drawable.ic_baseline_cloud_error);
                    Toast.makeText(getContext(), "服务启动失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    binding.ivCloud.setImageResource(R.drawable.ic_baseline_cloud_off);
                    Toast.makeText(getContext(), "服务已关闭", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.stopHttpServer();
        getActivity().unregisterReceiver(networkChangeReceiver);
    }

    static class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, "网络状态改变", Toast.LENGTH_SHORT).show();
        }
    }
}