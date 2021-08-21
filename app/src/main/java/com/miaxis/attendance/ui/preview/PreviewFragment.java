package com.miaxis.attendance.ui.preview;

import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;

import com.miaxis.attendance.R;
import com.miaxis.attendance.databinding.FragmentPreviewBinding;
import com.miaxis.attendance.widget.CameraTextureView;
import com.miaxis.common.activity.BaseBindingFragment;
import com.miaxis.common.camera.CameraConfig;
import com.miaxis.common.camera.CameraHelper;
import com.miaxis.common.camera.CameraPreviewCallback;
import com.miaxis.common.camera.MXCamera;
import com.miaxis.common.camera.MXFrame;
import com.miaxis.common.response.ZZResponse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class PreviewFragment extends BaseBindingFragment<FragmentPreviewBinding> implements TextureView.SurfaceTextureListener, CameraPreviewCallback {

    private static final String TAG = "PreviewFragment";
    private PreviewViewModel mViewModel;

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
        binding.ttvPreview.setSurfaceTextureListener(this);
        binding.ttvPreview.setRotationY(CameraConfig.Camera_RGB.mirror ? 180 : 0); // 镜面对称
        binding.ttvPreview.setRawPreviewSize(new CameraTextureView.Size(CameraConfig.Camera_RGB.height, CameraConfig.Camera_RGB.width));
        mViewModel.faceRect.observe(this, new Observer<RectF>() {
            @Override
            public void onChanged(RectF rectF) {
                binding.frvRect.setRect(rectF, false);
            }
        });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureAvailable:   " + width + "X" + height);
        ZZResponse<?> init = CameraHelper.getInstance().init(2);
        Log.e(TAG, "init:" + init);
        ZZResponse<MXCamera> mxCamera = CameraHelper.getInstance().createMXCamera(CameraConfig.Camera_RGB);
        Log.e(TAG, "createMXCamera:" + mxCamera);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ZZResponse<MXCamera> mxCameraZZResponse = CameraHelper.getInstance().find(CameraConfig.Camera_RGB);
                Log.e(TAG, "find:" + mxCameraZZResponse);
                if (ZZResponse.isSuccess(mxCameraZZResponse)) {
                    int startTexture = mxCameraZZResponse.getData().startTexture(surface);
                    Log.e(TAG, "startTexture:" + startTexture);
                    mxCameraZZResponse.getData().setPreviewCallback(PreviewFragment.this);
                    mxCameraZZResponse.getData().setNextFrameEnable();
                }
            }
        }).start();
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
    public void onPreview(MXCamera camera, MXFrame frame) {
        mViewModel.Process(camera, frame);
    }


}

