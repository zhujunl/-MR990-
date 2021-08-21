
package org.zz.api;

import android.content.Context;

import org.zz.jni.JustouchFaceApi;

import java.util.ArrayList;
import java.util.List;


public class MXFaceIdAPI {

    private static final String TAG = "MXFaceIdAPI";
    private boolean m_bInit = false;
    private final JustouchFaceApi mJustouchFaceApi = new JustouchFaceApi();
    private int[] FaceData;
    private MXFaceInfoEx[] FaceInfo;

    private MXFaceIdAPI() {
    }

    public static MXFaceIdAPI getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final MXFaceIdAPI instance = new MXFaceIdAPI();
    }

    /*
      ================================ 静态内部类单例 ================================
     */

    /**
     * @param
     * @return algorithm version
     * @author chen.gs
     * @category algorithm version
     */
    public MXResult<String> mxAlgVersion() {
        return MXResult.CreateSuccess(mJustouchFaceApi.getAlgVersion());
    }

    /**
     * @param context -  input，context handle
     *                szModelPath    -  input，model path
     *                szLicense      -  input，authorization code
     * @return 0-success, others-failure
     * @author chen.gs
     * @category initialization algorithm
     */
    public MXResult<?> mxInitAlg(Context context, String szModelPath, String szLicense) {
        int nRet = this.mJustouchFaceApi.initAlg(context, szModelPath, szLicense);
        if (nRet != 0) {
            return MXResult.CreateFail(nRet, null);
        }
        this.FaceData = new int[MXFaceInfoEx.SIZE * MXFaceInfoEx.iMaxFaceNum];
        this.FaceInfo = new MXFaceInfoEx[MXFaceInfoEx.iMaxFaceNum];
        for (int i = 0; i < MXFaceInfoEx.iMaxFaceNum; i++) {
            this.FaceInfo[i] = new MXFaceInfoEx();
        }
        this.m_bInit = true;
        return MXResult.CreateSuccess();
    }

    /**
     * @param
     * @return 0-success, others-failure
     * @author chen.gs
     * @category release algorithm
     */
    public void mxFreeAlg() {
        if (this.m_bInit) {
            this.mJustouchFaceApi.freeAlg();
        }
        this.FaceData = null;
        this.FaceInfo = null;
        this.m_bInit = false;
    }

    /**
     * @param pImage - input, BGR image data
     *               nWidth    - input, image width
     *               nHeight   - input, image height
     *               pFaceNum  - input/output，number of faces
     *               pFaceInfo - output, face information, see MXFaceInfoEx structure
     * @return 0-success, others-failure
     * @author chen.gs
     * @category face detection for still image detection
     */
    public synchronized MXResult<List<MXFace>> mxDetectFace(byte[] pImage, int nWidth, int nHeight) {
        if (!this.m_bInit) {
            return MXResult.CreateFail(MXErrorCode.ERR_NO_INIT, "未初始化");
        }
        int[] pFaceNum = new int[1];
        int nRet = this.mJustouchFaceApi.detectFace(pImage, nWidth, nHeight, pFaceNum, this.FaceData);
        if (nRet != 0) {
            pFaceNum[0] = 0;
            return MXResult.CreateFail(nRet, "人脸检测失败");
        }
        MXFaceInfoEx.Ints2MXFaceInfoExs(pFaceNum[0], this.FaceData, this.FaceInfo);
        List<MXFace> infoList = new ArrayList<>();
        for (int i = 0; i < pFaceNum[0]; i++) {
            int[] info = new int[MXFaceInfoEx.SIZE];
            System.arraycopy(this.FaceData, MXFaceInfoEx.SIZE * i, info, 0, MXFaceInfoEx.SIZE);
            infoList.add(new MXFace(info, this.FaceInfo[i]));
        }
        return MXResult.CreateSuccess(infoList);
    }

    /**
     * @param pImage - input, RGB image data
     *               nWidth       - input, image width
     *               nHeight      - input, image height
     *               nFaceNum     - input, number of faces
     *               pFaceInfo    - input, face information, see MXFaceInfoEx structure
     *               pFaceFeature - output, face features, feature length * number of faces
     * @return 0-success, others-failure
     * @author chen.gs
     * @category face feature extraction
     */
    public MXResult<byte[]> mxFeatureExtract(byte[] pImage, int nWidth, int nHeight, MXFace mxFace) {
        if (!this.m_bInit) {
            return MXResult.CreateFail(MXErrorCode.ERR_NO_INIT, "未初始化");
        }
        byte[] feature = new byte[mxGetFeatureSize()];
        int nRet = mJustouchFaceApi.featureExtract(pImage, nWidth, nHeight, 1, mxFace.getFaceData(), feature);
        if (nRet != 0) {
            return MXResult.CreateFail(MXErrorCode.ERR_FACE_EXTRACT, "人脸特征提取失败");
        }
        return MXResult.CreateSuccess(feature);
    }

    /**
     * @param pFaceFeatureA - input, face feature A
     *                      pFaceFeatureB - input, face feature B
     *                      fScore  - Output, similarity measure(0 ~ 1.0), the bigger the more similar, recommended threshold: 0.76
     * @return 0-success, others-failure
     * @author chen.gs
     * @category face feature match
     */
    public MXResult<Float> mxFeatureMatch(byte[] pFaceFeatureA, byte[] pFaceFeatureB) {
        if (!this.m_bInit) {
            return MXResult.CreateFail(MXErrorCode.ERR_NO_INIT, "未初始化");
        }
        float[] fScore = new float[1];
        int match = mJustouchFaceApi.featureMatch(pFaceFeatureA, pFaceFeatureB, fScore);
        if (match != 0) {
            return MXResult.CreateFail(MXErrorCode.ERR_FACE_MATCH, "人脸比对失败");
        }
        return MXResult.CreateSuccess(fScore[0]);
    }

    /**
     * @param pImage - input, RGB image width
     *               nWidth        - input, image width
     *               nHeight       - input, image height
     *               nFaceNum    	- input, number of faces
     *               pFaceInfo     - input/output, obtained through quality attribute of MXFaceInfoEx structure, recommended threshold: 50
     * @return 0-success, others-failure
     * @author chen.gs
     * @category face image quality evaluation based on face detection results
     */
    public MXResult<Integer> mxFaceQuality(byte[] pImage, int nWidth, int nHeight, MXFace mxFace) {
        if (!this.m_bInit) {
            return MXResult.CreateFail(MXErrorCode.ERR_NO_INIT, "未初始化");
        }
        int nRet = mJustouchFaceApi.faceQuality(pImage, nWidth, nHeight, 1, mxFace.getFaceData());
        if (nRet != 0) {
            return MXResult.CreateFail(MXErrorCode.ERR_QUALITY, "图像质量检测失败");
        }
        MXFaceInfoEx.Int2MXFaceInfoEx(mxFace.getFaceData(), mxFace.getFaceInfo());
        return MXResult.CreateSuccess(mxFace.getFaceInfo().quality);
    }

    /**
     * @param pImage - input, near infrared face image
     *               nWidth  	    - input, image width
     *               nHeight    	- input, image height
     *               nFaceNum    	- input, number of faces
     *               pFaceInfo 	- input/output, obtained through liveness attribute of MXFaceInfoEx structure, recommended threshold: 80
     * @return 0-success, others-failure
     * @author chen.gs
     * @category live detection of near infrared face image (the best effect of the camera of the specified model)
     */
    public MXResult<Integer> mxNIRLiveDetect(byte[] pImage, int nWidth, int nHeight, MXFace mxFace) {
        if (!this.m_bInit) {
            return MXResult.CreateFail(MXErrorCode.ERR_NO_INIT, "未初始化");
        }
        int nRet = mJustouchFaceApi.nirLivenessDetect(pImage, nWidth, nHeight, 1, mxFace.getFaceData());
        if (nRet != 0) {
            return MXResult.CreateFail(MXErrorCode.ERR_FACE_LIV, "活体检测失败");
        }
        MXFaceInfoEx.Int2MXFaceInfoEx(mxFace.getFaceData(), mxFace.getFaceInfo());
        return MXResult.CreateSuccess(mxFace.getFaceInfo().liveness);
    }

    /**
     * @param pImage - input, RGB image width
     *               nWidth        - input, image width
     *               nHeight       - input, image height
     *               nFaceNum    	- input, number of faces
     *               pFaceInfo 	- input/output, obtained through mask attribute of MXFaceInfoEx structure, recommended threshold: 40
     * @return 0-success, others-failure
     * @author chen.gs
     * @category Detect whether a face is wearing a mask
     */
    public MXResult<Integer> mxMaskDetect(byte[] pImage, int nWidth, int nHeight, MXFace mxFace) {
        if (!this.m_bInit) {
            return MXResult.CreateFail(MXErrorCode.ERR_NO_INIT, "未初始化");
        }
        int nRet = mJustouchFaceApi.maskDetect(pImage, nWidth, nHeight, 1, mxFace.getFaceData());
        if (nRet != 0) {
            return MXResult.CreateFail(MXErrorCode.ERR_FACE_MASK, "口罩检测失败");
        }
        MXFaceInfoEx.Int2MXFaceInfoEx(mxFace.getFaceData(), mxFace.getFaceInfo());
        return MXResult.CreateSuccess(mxFace.getFaceInfo().mask);
    }

    /**
     * @param pImage - input, RGB image data
     *               nWidth       - input, image width
     *               nHeight      - input, image height
     *               nFaceNum     - input, number of faces
     *               pFaceInfo    - input, face information, see MXFaceInfoEx structure
     *               pFaceFeature - output, face features, feature length * number of faces
     * @return 0-success, others-failure
     * @author chen.gs
     * @category face feature extraction (mask algorithm)
     */
    public MXResult<byte[]> mxMaskFeatureExtract(byte[] pImage, int nWidth, int nHeight, MXFace mxFace) {
        if (!this.m_bInit) {
            return MXResult.CreateFail(MXErrorCode.ERR_NO_INIT, "未初始化");
        }
        byte[] feature = new byte[mxGetFeatureSize()];
        int nRet = mJustouchFaceApi.maskFeatureExtract(pImage, nWidth, nHeight, 1, mxFace.getFaceData(), feature);
        if (nRet != 0) {
            return MXResult.CreateFail(MXErrorCode.ERR_FACE_MASK_EXTRACT, "人脸特征提取失败(带口罩)");
        }
        return MXResult.CreateSuccess(feature);
    }

    /**
     * @param pImage - input, RGB image data
     *               nWidth       - input, image width
     *               nHeight      - input, image height
     *               nFaceNum     - input, number of faces
     *               pFaceInfo    - input, face information, see MXFaceInfoEx structure
     *               pFaceFeature - output, face features, feature length * number of faces
     * @return 0-success, others-failure
     * @author chen.gs
     * @category face feature extraction for registration(mask algorithm)
     */
    public MXResult<byte[]> mxMaskFeatureExtract4Reg(byte[] pImage, int nWidth, int nHeight, MXFace mxFace) {
        if (!this.m_bInit) {
            return MXResult.CreateFail(MXErrorCode.ERR_NO_INIT, "未初始化");
        }
        byte[] feature = new byte[mxGetFeatureSize()];
        int nRet = mJustouchFaceApi.maskFeatureExtract4Reg(pImage, nWidth, nHeight, 1, mxFace.getFaceData(), feature);
        if (nRet != 0) {
            return MXResult.CreateFail(MXErrorCode.ERR_FACE_MASK_REG, "提取人脸特征失败(带口罩)");
        }
        return MXResult.CreateSuccess(feature);
    }

    /**
     * @param pFaceFeatureA - input, face feature A
     *                      pFaceFeatureB - input, face feature B
     *                      fScore  - Output, similarity measure(0 ~ 1.0), the bigger the more similar, recommended threshold: 0.73
     * @return 0-success, others-failure
     * @author chen.gs
     * @category face feature match (mask algorithm)
     */
    public MXResult<Float> mxMaskFeatureMatch(byte[] pFaceFeatureA, byte[] pFaceFeatureB) {
        if (!this.m_bInit) {
            return MXResult.CreateFail(MXErrorCode.ERR_NO_INIT, "未初始化");
        }
        float[] fScore = new float[1];
        int match = mJustouchFaceApi.maskFeatureMatch(pFaceFeatureA, pFaceFeatureB, fScore);
        if (match != 0) {
            return MXResult.CreateFail(MXErrorCode.ERR_FACE_MASK_MATCH, "特征(带口罩)匹配失败");
        }
        return MXResult.CreateSuccess(fScore[0]);
    }

    /**
     * @param
     * @return face feature length
     * @author chen.gs
     * @category get face feature length
     */
    public int mxGetFeatureSize() {
        int iFeaLen = 0;
        if (m_bInit) {
            iFeaLen = mJustouchFaceApi.getFeatureSize();
        }
        return iFeaLen;
    }

}
