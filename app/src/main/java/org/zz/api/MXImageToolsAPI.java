
package org.zz.api;


import android.graphics.Rect;
import android.text.TextUtils;

import org.zz.jni.mxImageTool;

public class MXImageToolsAPI {

    final mxImageTool mMxImageTool = new mxImageTool();

    public static MXImageToolsAPI getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final MXImageToolsAPI instance = new MXImageToolsAPI();
    }

    /*
      ================================ 静态内部类单例 ================================
     */

    /**
     * @param pYUVImage - 输入	YUV图像数据
     *                  iImgWidth	- 输入	图像宽度
     *                  iImgHeight	- 输入	图像高度
     *                  pRGBImage	- 输出	RGB图像数据
     * @return 1-成功，其他-失败
     * @author chen.gs
     * @category YUV数据转换为RGB数据(Android摄像头获取的数据为YUV格式)
     */
    public MXResult<byte[]> YUV2RGB(byte[] pYUVImage, int iImgWidth, int iImgHeight) {
        if (pYUVImage == null || pYUVImage.length == 0 || iImgWidth <= 0 || iImgHeight <= 0) {
            return MXResult.CreateFail(-1, "参数不合法");
        }
        byte[] pRGBImage = new byte[iImgWidth * iImgHeight * 3];
        this.mMxImageTool.YUV2RGB(pYUVImage, iImgWidth, iImgHeight, pRGBImage);
        return MXResult.CreateSuccess(pRGBImage);
    }

    /**
     * @param szSaveFilePath - 输入	保存图像路径
     *                       pImgBuf			- 输入	图像缓冲区
     *                       iImgWidth			- 输入	图像宽度
     *                       iImgHeight			- 输入	图像高度
     *                       iChannels          - 输入  图像通道
     * @return 1-成功，其他-失败
     * @author chen.gs
     * @category 保存图像数据
     */
    public MXResult<?> ImageSave(String szSaveFilePath,
                                 byte[] pImgBuf, int iImgWidth, int iImgHeight, int iChannels) {
        if (TextUtils.isEmpty(szSaveFilePath) || pImgBuf == null ||
                pImgBuf.length == 0 || iImgWidth <= 0 || iImgHeight <= 0 ||
                iChannels <= 0 || iChannels > 4) {
            return MXResult.CreateFail(-1, "参数不合法");
        }

        int imageSave = this.mMxImageTool.ImageSave(szSaveFilePath, pImgBuf, iImgWidth, iImgHeight, iChannels);
        if (imageSave != 1) {
            return MXResult.CreateFail(-2, "操作失败");
        }
        return MXResult.CreateSuccess();
    }

    /**
     * @param input - 输入	RGB图像缓冲区
     *              iImgWidth			- 输入	图像宽度
     *              iImgHeight			- 输入	图像高度
     *              iAngle             - 输入，角度90/180/270
     *              pDstImgBuf  		- 输出	RGB图像缓冲区
     *              iDstWidth			- 输出	图像宽度
     *              iDstHeight			- 输出	图像高度
     * @return 1-成功，其他-失败
     * @author chen.gs
     * @category 输入的RGB图像，进行顺时针90/180/270角度旋转
     */
    public MXResult<MxImage> ImageRotate(MxImage input, int iAngle) {
        if (input == null || input.isBufferEmpty() ||
                !input.isSizeLegal() ||
                !(iAngle == 90 || iAngle == 180 || iAngle == 270)) {
            return MXResult.CreateFail(-1, "参数不合法");
        }
        byte[] out = new byte[input.width * input.height * 3];
        int[] width = new int[1];
        int[] height = new int[1];
        int imageRotate = this.mMxImageTool.ImageRotate(input.buffer, input.width, input.height,
                iAngle, out, width, height);
        if (imageRotate != 1) {
            return MXResult.CreateFail(-2, "操作失败");
        }
        return MXResult.CreateSuccess(new MxImage(width[0], height[0], out));
    }


    /**
     * @param input - 输入	RGB图像缓冲区
     *                   iImgWidth		- 输入	图像宽度
     *                   iImgHeight		- 输入	图像高度
     *                   iRect			- 输入	Rect[0]	=x;
     *                   Rect[1]	=y;
     *                   Rect[2]	=width;
     *                   Rect[3]	=height;
     *                   pDstImgBuf  	- 输出	RGB图像缓冲区
     *                   iDstWidth		- 输出	图像宽度
     *                   iDstHeight		- 输出	图像高度
     * @return 1-成功，其他-失败
     * @author chen.gs
     * @category 在输入的RGB图像上根据输入的Rect, 进行裁剪
     */
    public MXResult<MxImage> ImageCutRect(MxImage input, Rect rect) {
        if (input == null || input.isBufferEmpty() ||
                !input.isSizeLegal() || rect == null || rect.isEmpty() ||
                rect.top <= 0 || rect.left <= 0 || rect.right <= 0 || rect.bottom <= 0) {
            return MXResult.CreateFail(-1, "参数不合法");
        }
        int[] rects = new int[]{rect.left, rect.top, rect.width(), rect.height()};
        byte[] out = new byte[input.width * input.height * 3];
        int[] width = new int[1];
        int[] height = new int[1];
        int imageCutRect = this.mMxImageTool.ImageCutRect(input.buffer, input.width, input.height,
                rects, out, width, height);
        if (imageCutRect != 1) {
            return MXResult.CreateFail(-2, "操作失败");
        }
        return MXResult.CreateSuccess(new MxImage(width[0], height[0], out));
    }
}
