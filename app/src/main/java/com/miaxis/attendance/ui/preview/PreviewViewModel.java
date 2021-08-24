package com.miaxis.attendance.ui.preview;

import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.miaxis.attendance.App;
import com.miaxis.attendance.data.entity.Face;
import com.miaxis.attendance.data.entity.Person;
import com.miaxis.attendance.data.model.FaceModel;
import com.miaxis.attendance.data.model.PersonModel;
import com.miaxis.common.camera.CameraConfig;
import com.miaxis.common.camera.CameraHelper;
import com.miaxis.common.camera.CameraPreviewCallback;
import com.miaxis.common.camera.MXCamera;
import com.miaxis.common.camera.MXFrame;
import com.miaxis.common.response.ZZResponse;
import com.miaxis.common.response.ZZResponseCode;
import com.miaxis.common.utils.ListUtils;

import org.zz.api.MXFace;
import org.zz.api.MXFaceIdAPI;
import org.zz.api.MXImageToolsAPI;
import org.zz.api.MXResult;
import org.zz.api.MxImage;
import org.zz.mr990Driver;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PreviewViewModel extends ViewModel implements CameraPreviewCallback {

    private static final String TAG = "PreviewViewModel";
    private Handler mHandler = new Handler();
    private long timeOut = 1000 * 5L;

    /**
     * 人脸框
     */
    MutableLiveData<RectF> faceRect = new MutableLiveData<>();
    /**
     * 摄像头是否可用
     */
    MutableLiveData<ZZResponse<?>> IsCameraEnable_Rgb = new MutableLiveData<>();
    MutableLiveData<ZZResponse<?>> IsCameraEnable_Nir = new MutableLiveData<>();

    /**
     * 近红外预览区域
     */
    AtomicReference<SurfaceHolder> SurfaceHolder_Nir = new AtomicReference<>();
    /**
     * Nir视频帧是否正在处理
     */
    AtomicBoolean IsNirFrameProcessing = new AtomicBoolean(false);
    /**
     * 是否启用近红外帧
     */
    AtomicBoolean IsNirEnable = new AtomicBoolean(false);


    /**
     * 人脸帧数据缓存
     */
    AtomicReference<Map.Entry<MxImage, MXFace>> CurrentMxImage_Rgb = new AtomicReference<>();
    AtomicReference<Map.Entry<MxImage, MXFace>> CurrentMxImage_Nir = new AtomicReference<>();


    public PreviewViewModel() {
    }

    /**
     * 处理可见光视频帧数据
     */
    private synchronized void Process_Rgb(MXFrame frame) {
        Disposable subscribe = Observable.create((ObservableOnSubscribe<List<RectF>>) emitter -> {
            if (!MXFrame.isBufferEmpty(frame) && MXFrame.isSizeLegal(frame)) {
                MXResult<byte[]> mxResult = MXImageToolsAPI.getInstance().YUV2RGB(frame.buffer, frame.width, frame.height);//MR90 10ms
                if (MXResult.isSuccess(mxResult)) {
                    MXResult<MxImage> imageRotate = MXImageToolsAPI.getInstance().ImageRotate(
                            new MxImage(frame.width, frame.height, mxResult.getData()),
                            CameraConfig.Camera_RGB.bufferOrientation);//MR90 15ms
                    if (MXResult.isSuccess(imageRotate)) {
                        MxImage mxImage = imageRotate.getData();
                        MXResult<List<MXFace>> detectFace = MXFaceIdAPI.getInstance().mxDetectFace(
                                mxImage.buffer, mxImage.width, mxImage.height);//MR90 40--100ms
                        if (MXResult.isSuccess(detectFace)) {
                            List<RectF> list = new ArrayList<>();
                            List<MXFace> data = detectFace.getData();
                            for (MXFace mxFace : data) {
                                list.add(mxFace.getFaceRectF());
                            }
                            this.CurrentMxImage_Rgb.compareAndSet(null, new AbstractMap.SimpleEntry<>(mxImage, MXFaceIdAPI.getInstance().getMaxFace(data)));
                            emitter.onNext(list);
                            return;
                        }
                    }
                }
            }
            this.CurrentMxImage_Rgb.compareAndSet(null, null);
            emitter.onNext(new ArrayList<>());
        }).subscribeOn(Schedulers.from(App.getInstance().threadExecutor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    if (ListUtils.isNullOrEmpty(list)) {
                        this.faceRect.postValue(null);
                    } else {
                        if (this.IsNirEnable.get()) {
                            startNirFrame();
                        }
                        this.faceRect.postValue(list.get(0));
                    }
                    startRgbFrame();
                }, throwable -> {
                    this.CurrentMxImage_Rgb.compareAndSet(null, null);
                    this.faceRect.postValue(null);
                    startRgbFrame();
                });
    }

    /**
     * 开启可见光视频帧
     */
    private void startRgbFrame() {
        ZZResponse<MXCamera> mxCamera = CameraHelper.getInstance().createOrFindMXCamera(CameraConfig.Camera_RGB);
        if (ZZResponse.isSuccess(mxCamera)) {
            int enable = mxCamera.getData().setNextFrameEnable();
            this.mHandler.removeCallbacksAndMessages(null);
            this.mHandler.postDelayed(() -> IsCameraEnable_Rgb.setValue(ZZResponse.CreateFail(-98, "Camera is error")), this.timeOut);
            this.IsCameraEnable_Rgb.setValue(ZZResponse.CreateSuccess());
        } else {
            this.IsCameraEnable_Rgb.setValue(ZZResponse.CreateFail(mxCamera.getCode(), "Can not found rgb camera"));
        }
    }

    /**
     * 开启近红外视频帧
     * 主线程
     */
    private void startNirFrame() {
        if (!this.IsNirFrameProcessing.get()) {
            ZZResponse<MXCamera> mxCamera = CameraHelper.getInstance().createOrFindMXCamera(CameraConfig.Camera_NIR);
            if (ZZResponse.isSuccess(mxCamera)) {
                int enable = mxCamera.getData().setNextFrameEnable();
                this.IsNirFrameProcessing.set(true);
                this.IsCameraEnable_Nir.setValue(ZZResponse.CreateSuccess());
            } else {
                this.IsCameraEnable_Nir.setValue(ZZResponse.CreateFail(mxCamera.getCode(), "Can not found nir camera"));
            }
        }
    }

    /**
     * 处理近红外视频帧数据
     */
    private void Process_Nir(MXFrame frame) {
        Disposable subscribe = Observable.create((ObservableOnSubscribe<List<RectF>>) emitter -> {
            if (!MXFrame.isBufferEmpty(frame) && MXFrame.isSizeLegal(frame)) {
                MXResult<byte[]> mxResult = MXImageToolsAPI.getInstance().YUV2RGB(frame.buffer, frame.width, frame.height);//MR90 10ms
                if (MXResult.isSuccess(mxResult)) {
                    MXResult<MxImage> imageRotate = MXImageToolsAPI.getInstance().ImageRotate(
                            new MxImage(frame.width, frame.height, mxResult.getData()),
                            CameraConfig.Camera_NIR.bufferOrientation);//MR90 15ms
                    if (MXResult.isSuccess(imageRotate)) {
                        MxImage mxImage = imageRotate.getData();
                        MXResult<List<MXFace>> detectFace = MXFaceIdAPI.getInstance().mxDetectFaceNir(
                                mxImage.buffer, mxImage.width, mxImage.height);//MR90 40--100ms
                        if (MXResult.isSuccess(detectFace)) {
                            //                            String path = "/sdcard/1/" + System.currentTimeMillis() + ".jpeg";
                            //                            MXResult<?> imageSave = MXImageToolsAPI.getInstance().ImageSave(path, mxImage.buffer, mxImage.width, mxImage.height, 3);
                            //                            Log.e(TAG, "imageSave: " + imageSave);
                            List<RectF> list = new ArrayList<>();
                            List<MXFace> data = detectFace.getData();
                            for (MXFace mxFace : data) {
                                list.add(mxFace.getFaceRectF());
                            }
                            this.CurrentMxImage_Rgb.compareAndSet(null, new AbstractMap.SimpleEntry<>(mxImage, MXFaceIdAPI.getInstance().getMaxFace(data)));
                            emitter.onNext(list);
                            return;
                        }
                    }
                }
            }
            emitter.onNext(new ArrayList<>());
        }).subscribeOn(Schedulers.from(App.getInstance().threadExecutor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    if (ListUtils.isNullOrEmpty(list)) {
                        this.IsNirFrameProcessing.set(false);
                    } else {
                        processLiveAndMatch();
                    }
                }, throwable -> {
                    this.CurrentMxImage_Nir.compareAndSet(null, null);
                    this.IsNirFrameProcessing.set(false);
                });
    }

    public void showRgbCameraPreview(SurfaceTexture surface) {
        Disposable subscribe = Observable.create((ObservableOnSubscribe<ZZResponse<MXCamera>>) emitter -> {
            ZZResponse<?> init = CameraHelper.getInstance().init(2);
            Log.e(TAG, "init:" + init);
            if (ZZResponse.isSuccess(init)) {
                ZZResponse<MXCamera> mxCamera = CameraHelper.getInstance().createOrFindMXCamera(CameraConfig.Camera_RGB);
                Log.e(TAG, "createMXCamera:" + mxCamera);
                if (ZZResponse.isSuccess(mxCamera)) {
                    MXCamera camera = mxCamera.getData();
                    int startTexture = camera.startTexture(surface);
                    Log.e(TAG, "startTexture:" + startTexture);
                    if (startTexture == 0) {
                        emitter.onNext(mxCamera);
                    } else {
                        emitter.onNext(ZZResponse.CreateFail(startTexture, "Preview failed"));
                    }
                } else {
                    emitter.onNext(mxCamera);
                }
            } else {
                emitter.onNext(ZZResponse.CreateFail(init));
            }
        }).subscribeOn(Schedulers.from(App.getInstance().threadExecutor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(camera -> {
                    if (ZZResponse.isSuccess(camera)) {
                        camera.getData().setPreviewCallback(PreviewViewModel.this);
                        startNirPreview();
                    }
                    startRgbFrame();
                }, throwable -> {
                    this.IsCameraEnable_Rgb.setValue(ZZResponse.CreateFail(-99, throwable.getMessage()));
                });
    }

    /**
     * 开启近红外预览
     */
    private void startNirPreview() {
        Disposable subscribe = Observable.create((ObservableOnSubscribe<ZZResponse<MXCamera>>) emitter -> {
            ZZResponse<MXCamera> mxCamera = CameraHelper.getInstance().createOrFindMXCamera(CameraConfig.Camera_NIR);
            Log.e(TAG, "createMXCamera:" + mxCamera);
            if (ZZResponse.isSuccess(mxCamera)) {
                MXCamera camera = mxCamera.getData();
                int start = camera.start(this.SurfaceHolder_Nir.get());
                Log.e(TAG, "start:" + start);
                if (start == 0) {
                    emitter.onNext(mxCamera);
                } else {
                    emitter.onNext(ZZResponse.CreateFail(start, "Preview(Nir) failed"));
                }
            } else {
                emitter.onNext(mxCamera);
            }
        }).subscribeOn(Schedulers.from(App.getInstance().threadExecutor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(camera -> {
                    if (ZZResponse.isSuccess(camera)) {
                        camera.getData().setPreviewCallback(PreviewViewModel.this);
                    }
                    this.IsCameraEnable_Nir.setValue(camera);
                }, throwable -> {
                    this.IsCameraEnable_Nir.setValue(ZZResponse.CreateFail(-99, throwable.getMessage()));
                });
    }

    /**
     * 处理活体和比对
     */
    private void processLiveAndMatch() {
        Disposable subscribe = Observable.create((ObservableOnSubscribe<ZZResponse<Person>>) emitter -> {
            Map.Entry<MxImage, MXFace> rgbEntry = this.CurrentMxImage_Rgb.get();
            Map.Entry<MxImage, MXFace> nirEntry = this.CurrentMxImage_Nir.get();

            if (rgbEntry == null || nirEntry == null
                    || rgbEntry.getKey() == null || nirEntry.getKey() == null
                    || rgbEntry.getKey().isBufferEmpty() || rgbEntry.getKey().isSizeLegal()
                    || rgbEntry.getValue() == null || nirEntry.getValue() == null
                    || rgbEntry.getKey().isBufferEmpty() || rgbEntry.getKey().isSizeLegal()) {
                emitter.onNext(ZZResponse.CreateFail(ZZResponseCode.CODE_ILLEGAL_PARAMETER, ZZResponseCode.MSG_ILLEGAL_PARAMETER));
            } else {
                MxImage rgbImage = rgbEntry.getKey();
                MXFace rgbFace = rgbEntry.getValue();
                //可见光活体判断
                MXResult<?> rgbResult = MXFaceIdAPI.getInstance().mxRGBLiveDetect(rgbImage.buffer, rgbImage.width, rgbImage.height, rgbFace);
                if (!MXResult.isSuccess(rgbResult)) {
                    emitter.onNext(ZZResponse.CreateFail(rgbResult.getCode(), rgbResult.getMsg()));
                    return;
                }
                //3. 近红外人脸检测
                //4. 近红外活体检测
                MxImage nirImage = nirEntry.getKey();
                MXFace nirFace = nirEntry.getValue();
                MXResult<Integer> nirResult = MXFaceIdAPI.getInstance().mxNIRLiveDetect(nirImage.buffer, nirImage.width, nirImage.height, nirFace);
                if (!MXResult.isSuccess(nirResult)) {
                    emitter.onNext(ZZResponse.CreateFail(nirResult.getCode(), nirResult.getMsg()));
                    return;
                }
                if (nirResult.getData() < MXFaceIdAPI.getInstance().FaceLive) {
                    emitter.onNext(ZZResponse.CreateFail(-76, "非活体，value:" + nirResult.getData()));
                    return;
                }

                //5.比对
                //5.1提取特征
                MXResult<byte[]> mxResult = MXFaceIdAPI.getInstance().mxFeatureExtract(nirImage.buffer, nirImage.width, nirImage.height, nirFace);
                if (!MXResult.isSuccess(mxResult)) {
                    emitter.onNext(ZZResponse.CreateFail(mxResult.getCode(), mxResult.getMsg()));
                    return;
                }
                Face tempFace = null;
                float tempFloat = 0F;
                List<Face> all = FaceModel.findAll();
                for (Face face : all) {
                    MXResult<Float> result = MXFaceIdAPI.getInstance().mxFeatureMatch(mxResult.getData(), face.FaceFeature);
                    if (MXResult.isSuccess(result)) {
                        if (result.getData() >= tempFloat) {
                            tempFace = face;
                            tempFloat = result.getData();
                        }
                    }
                }
                if (tempFloat < MXFaceIdAPI.getInstance().FaceMatch) {
                    emitter.onNext(ZZResponse.CreateFail(-80, "未找到，最大匹配值：" + tempFloat));
                    return;
                }
                List<Person> byUserID = PersonModel.findByUserID(tempFace.UserId);
                if (ListUtils.isNullOrEmpty(byUserID)) {
                    emitter.onNext(ZZResponse.CreateFail(-80, "该人员不存在，UserId：" + tempFace.UserId));
                    return;
                }
                mr990Driver.relayControl(1);
                emitter.onNext(ZZResponse.CreateSuccess(byUserID.get(0)));
            }
        }).subscribeOn(Schedulers.from(App.getInstance().threadExecutor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    Log.e(TAG, "processLiveAndMatch: " + response);
                    if (ZZResponse.isSuccess(response)) {

                    } else {

                    }
                    this.IsNirFrameProcessing.set(false);
                    this.CurrentMxImage_Rgb.compareAndSet(null, null);
                    this.CurrentMxImage_Nir.compareAndSet(null, null);
                }, throwable -> {
                    this.IsNirFrameProcessing.set(false);
                    this.CurrentMxImage_Rgb.compareAndSet(null, null);
                    this.CurrentMxImage_Nir.compareAndSet(null, null);
                });
    }

    @Override
    public void onPreview(MXCamera camera, MXFrame frame) {
        if (camera.getCameraId() == CameraConfig.Camera_RGB.CameraId) {
            this.mHandler.removeCallbacksAndMessages(null);
            this.Process_Rgb(frame);
        } else {
            this.Process_Nir(frame);
        }
    }

    public void resume() {
        CameraHelper.getInstance().resume();
    }

    public void pause() {
        this.mHandler.removeCallbacksAndMessages(null);
        CameraHelper.getInstance().pause();
    }

    public void destroy() {
        CameraHelper.getInstance().stop();
        CameraHelper.getInstance().free();
    }

}