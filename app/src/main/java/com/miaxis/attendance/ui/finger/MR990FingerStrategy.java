package com.miaxis.attendance.ui.finger;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

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

import java.util.List;


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

    public void readFinger(ReadFingerCallBack readFingerCallBack) {
        this.isCancel = false;
        App.getInstance().threadExecutor.execute(() -> {
            while (!this.isCancel && mxMscBigFingerApi != null) {
                SystemClock.sleep(200);
                if (!mxMscBigFingerApi.getDeviceInfo().isSuccess()) {
                    continue;
                }
                Result<MxImage> result = mxMscBigFingerApi.getFingerImageBig(1000);
                Log.e(TAG, "getFingerImageBig:" + result);
                if (!this.isCancel && result.isSuccess()) {
                    MxImage image = result.data;
                    readFingerCallBack.onReadFinger(image);
                    if (!this.isCancel && image != null) {
                        byte[] feature = mxFingerAlg.extractFeature(image.data, image.width, image.height);
                        Log.e(TAG, "extractFeature:" + (feature == null ? null : feature.length));
                        readFingerCallBack.onExtractFeature(image, feature);
                        List<Finger> fingerList = FingerModel.findAll();
                        for (Finger finger : fingerList) {
                            if (!this.isCancel && !ArrayUtils.isNullOrEmpty(finger.FingerFeature) && !ArrayUtils.isNullOrEmpty(feature)) {
                                int match = mxFingerAlg.match(finger.FingerFeature, feature, 3);
                                if (!this.isCancel && match == 0) {
                                    Log.e(TAG, "onFeatureMatch: " + finger);
                                    readFingerCallBack.onFeatureMatch(image, feature, finger, RawBitmapUtils.raw2Bimap(image.data, image.width, image.height));
                                    break;
                                }
                            }
                        }
                        Log.e(TAG, "onFeatureMatch: end");
                    }
                }
            }

        });
    }

    public void stopRead() {
        isCancel = true;
    }

    public void release() {
        isCancel = true;
        mxMscBigFingerApi = null;
        mxFingerAlg = null;
    }

    /**
     * 读取指纹回调
     */
    public interface ReadFingerCallBack {
        void onReadFinger(MxImage finger);

        void onExtractFeature(MxImage finger, byte[] feature);

        void onFeatureMatch(MxImage image, byte[] feature, Finger finger, Bitmap bitmap);

    }

}
