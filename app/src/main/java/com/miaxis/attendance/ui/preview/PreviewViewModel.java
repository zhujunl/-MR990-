package com.miaxis.attendance.ui.preview;

import android.graphics.RectF;
import android.util.Log;

import com.miaxis.common.camera.CameraConfig;
import com.miaxis.common.camera.MXCamera;
import com.miaxis.common.camera.MXFrame;

import org.zz.api.MXFace;
import org.zz.api.MXFaceIdAPI;
import org.zz.api.MXImageToolsAPI;
import org.zz.api.MXResult;
import org.zz.api.MxImage;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PreviewViewModel extends ViewModel {
    private static final String TAG = "PreviewViewModel";

    MutableLiveData<RectF> faceRect = new MutableLiveData<>();

    public PreviewViewModel() {
    }

    public void Process(MXCamera camera, MXFrame frame) {
        if (!MXFrame.isBufferEmpty(frame) && MXFrame.isSizeLegal(frame)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "-----------------------------------------");
                    MXResult<byte[]> mxResult = MXImageToolsAPI.getInstance().YUV2RGB(frame.buffer, frame.width, frame.height);//MR90 10ms
                    Log.e(TAG, "YUV2RGB: " + mxResult);
                    if (MXResult.isSuccess(mxResult)) {
                        MXResult<MxImage> imageRotate = MXImageToolsAPI.getInstance().ImageRotate(
                                new MxImage(frame.width, frame.height, mxResult.getData()), CameraConfig.Camera_RGB.bufferOrientation);//MR90 15ms
                        Log.e(TAG, "ImageRotate: " + imageRotate);
                        if (MXResult.isSuccess(imageRotate)) {
                            MxImage mxImage = imageRotate.getData();
                            MXResult<List<MXFace>> detectFace = MXFaceIdAPI.getInstance().mxDetectFace(mxImage.buffer, mxImage.width, mxImage.height);//MR90 40--100ms
                            Log.e(TAG, "detectFace: " + detectFace);
                            if (!MXResult.isSuccess(detectFace)) {
                                faceRect.postValue(null);
                                camera.setNextFrameEnable();
                            } else {
                                //String path = "/sdcard/DCIM/" + System.currentTimeMillis() + ".jpeg";
                                //MXResult<?> save = MXImageToolsAPI.getInstance().ImageSave(path, mxImage.buffer, mxImage.width, mxImage.height, 3);//MR90 30ms
                                //Log.e(TAG, "ImageSave: " + save);
                                List<MXFace> list = detectFace.getData();
                                faceRect.postValue(list.get(0).getFaceRectF());

                                //                                MXResult<MxImage> cutRect = MXImageToolsAPI.getInstance().ImageCutRect(mxImage, list.get(0).getFaceRect());//MR90 30ms
                                //                                Log.e(TAG, "cutRect: " + cutRect);
                                //                                if (MXResult.isSuccess(cutRect)){
                                //                                    MxImage cutImage = cutRect.getData();
                                //                                    String path = "/sdcard/DCIM/" + System.currentTimeMillis() + ".jpeg";
                                //                                    MXResult<?> save = MXImageToolsAPI.getInstance().ImageSave(path, cutImage.buffer, cutImage.width, cutImage.height, 3);
                                //                                    Log.e(TAG, "save: " + save);
                                //                                }
                                //
                                //                                SystemClock.sleep(1000);
                                camera.setNextFrameEnable();
                                //MXResult<Integer> result = MXFaceIdAPI.getInstance().mxFaceQuality(mxImage.buffer, mxImage.width, mxImage.height, list.get(0));//MR90 100ms
                                //Log.e(TAG, "result: " + result);
                            }
                        }
                    }

                }
            }).start();
        }
    }

}