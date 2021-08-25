package com.miaxis.attendance.ui.preview;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.TextureView;

import com.miaxis.attendance.MainViewModel;
import com.miaxis.attendance.R;
import com.miaxis.attendance.databinding.FragmentPreviewBinding;
import com.miaxis.attendance.widget.CameraTextureView;
import com.miaxis.common.activity.BaseBindingFragment;
import com.miaxis.common.camera.CameraConfig;
import com.miaxis.common.camera.CameraHelper;
import com.miaxis.common.response.ZZResponse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class PreviewFragment extends BaseBindingFragment<FragmentPreviewBinding> implements TextureView.SurfaceTextureListener {

    private static final String TAG = "PreviewFragment";
    private PreviewViewModel mViewModel;
    private long timeOut = 10 * 1000L;
    private Handler mHandler = new Handler();

    public static PreviewFragment newInstance() {
        return new PreviewFragment();
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_preview;
    }

    @Override
    protected void initView(@NonNull FragmentPreviewBinding binding, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(PreviewViewModel.class);

        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        mViewModel.StartCountdown.observe(this, start -> {
            mHandler.removeCallbacksAndMessages(null);
            if (start) {
                mHandler.postDelayed(() -> mainViewModel.showAdvertising.setValue(true), timeOut);
            }
        });

        binding.ttvPreview.setSurfaceTextureListener(this);
        binding.ttvPreview.setRotationY(CameraConfig.Camera_RGB.mirror ? 180 : 0); // 镜面对称
        binding.ttvPreview.setRawPreviewSize(new CameraTextureView.Size(CameraConfig.Camera_RGB.height, CameraConfig.Camera_RGB.width));
        Observer<ZZResponse<?>> observer = response -> {
            if (!ZZResponse.isSuccess(response)) {
                new AlertDialog.Builder(getContext()).setTitle("错误")
                        .setMessage("摄像头打开失败，是否重试？\n" + response.getCode() + "," + response.getMsg())
                        .setPositiveButton("重试", (dialog, which) -> {
                            dialog.dismiss();
                            showCameraPreview(binding.ttvPreview.getSurfaceTexture());
                        }).setNegativeButton("退出", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                }).create().show();
            }
        };
        mViewModel.IsCameraEnable_Rgb.observe(this, observer);
        mViewModel.IsCameraEnable_Nir.observe(this, observer);

        mViewModel.faceRect.observe(this, rectF -> binding.frvRect.setRect(rectF, false));
        binding.svPreviewNir.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                mViewModel.SurfaceHolder_Nir.set(holder);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        showCameraPreview(surface);
    }

    private void showCameraPreview(SurfaceTexture surface) {
        mViewModel.showRgbCameraPreview(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        CameraHelper.getInstance().stop();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.resume();
        mViewModel.StartCountdown.setValue(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mViewModel.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.destroy();
    }
}

