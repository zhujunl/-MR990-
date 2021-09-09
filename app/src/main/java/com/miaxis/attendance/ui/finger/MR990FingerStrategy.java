package com.miaxis.attendance.ui.finger;

import android.graphics.Bitmap;
import android.os.SystemClock;

import com.miaxis.attendance.App;
import com.miaxis.attendance.data.entity.Finger;
import com.miaxis.attendance.data.model.FingerModel;
import com.miaxis.common.utils.ArrayUtils;
import com.mx.finger.alg.MxFingerAlg;
import com.mx.finger.api.msc.MxMscBigFingerApi;
import com.mx.finger.api.msc.MxMscBigFingerApiFactory;
import com.mx.finger.common.MxImage;
import com.mx.finger.common.Result;
import com.mx.finger.utils.RawBitmapUtils;

import org.zz.api.MXResult;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;


public class MR990FingerStrategy {
    private static final String TAG = "MR990FingerStrategy";
    private MxMscBigFingerApi mxMscBigFingerApi;
    private MxFingerAlg mxFingerAlg;

    private MR990FingerStrategy() {
    }

    private static class Single {
        public static MR990FingerStrategy mr990FingerStrategy = new MR990FingerStrategy();
    }

    public static MR990FingerStrategy getInstance() {
        return Single.mr990FingerStrategy;
    }

    public void init() {
        MxMscBigFingerApiFactory fingerFactory = new MxMscBigFingerApiFactory(App.getInstance());
        mxMscBigFingerApi = fingerFactory.getApi();
        mxFingerAlg = fingerFactory.getAlg();
    }

    public String deviceInfo() {
        if (mxMscBigFingerApi != null) {
            Result<String> deviceInfo = mxMscBigFingerApi.getDeviceInfo();
            if (deviceInfo.isSuccess()) {
                return deviceInfo.data;
            }
        }
        return "";
    }

    public MXResult<byte[]> extractFeature(byte[] image, int width, int height) {
        if (this.mxFingerAlg == null) {
            return MXResult.CreateFail(-201, "please init first");
        }
        byte[] extractFeature = this.mxFingerAlg.extractFeature(image, width, height);
        if (ArrayUtils.isNullOrEmpty(extractFeature)) {
            return MXResult.CreateFail(-202, "extract feature failed");
        } else {
            return MXResult.CreateSuccess(extractFeature);
        }
    }

    private boolean isCancel = false;
    private boolean isWaite = false;

    /**
     * 待优化
     */
    public void readFinger(ReadFingerCallBack readFingerCallBack) {
        this.isCancel = false;
        App.getInstance().threadExecutor.execute(() -> {
            while (!this.isCancel && this.mxMscBigFingerApi != null) {
                SystemClock.sleep(200);
                if (this.isWaite) {
                    SystemClock.sleep(500);
                    continue;
                }
                if (!this.mxMscBigFingerApi.getDeviceInfo().isSuccess()) {
                    continue;
                }
                Result<MxImage> result = this.mxMscBigFingerApi.getFingerImageBig(1000);
                Timber.e("getFingerImageBig:%s", result);
                if (!this.isCancel && result.isSuccess()) {
                    MxImage image = result.data;
                    readFingerCallBack.onReadFinger(image);
                    if (!this.isCancel && image != null) {
                        byte[] feature = mxFingerAlg.extractFeature(image.data, image.width, image.height);
                        Timber.e("extractFeature:%s", (feature == null ? null : feature.length));
                        readFingerCallBack.onExtractFeature(image, feature);
                        HashMap<String, Finger> all = FingerModel.findAll();
                        for (Map.Entry<String, Finger> entry : all.entrySet()) {
                            Finger finger = entry.getValue();
                            if (!this.isCancel && !ArrayUtils.isNullOrEmpty(finger.FingerFeature) &&
                                    !ArrayUtils.isNullOrEmpty(feature)) {
                                int match = mxFingerAlg.match(finger.FingerFeature, feature, 3);
                                if (!this.isCancel && match == 0) {
                                    Timber.e("onFeatureMatch: %s", finger);
                                    readFingerCallBack.onFeatureMatch(image, feature, finger,
                                            RawBitmapUtils.raw2Bimap(image.data, image.width, image.height));
                                    break;
                                }
                            }
                        }
                        Timber.e("onFeatureMatch: end");
                    }
                }
            }
        });
    }

    public void pause() {
        this.isWaite = true;
    }

    public void resume() {
        this.isWaite = false;
    }

    public void stopRead() {
        this.isCancel = true;
    }

    public void release() {
        this.isCancel = true;
        this.mxMscBigFingerApi = null;
        this.mxFingerAlg = null;
    }

    /**
     * 读取指纹回调
     */
    public interface ReadFingerCallBack {
        /**
         * 读到指纹
         */
        void onReadFinger(MxImage finger);

        /**
         * 指纹特征提取成功
         */
        void onExtractFeature(MxImage finger, byte[] feature);

        /**
         * 指纹特征比对成功
         */
        void onFeatureMatch(MxImage image, byte[] feature, Finger finger, Bitmap bitmap);

    }

}
