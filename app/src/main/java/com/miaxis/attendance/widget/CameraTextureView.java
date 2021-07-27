package com.miaxis.attendance.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * @author Tank
 * @date 2021/7/26 5:29 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class CameraTextureView extends TextureView {

    //宽高比列
    private float mRatio = 1F;

    public CameraTextureView(Context context) {
        super(context);
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        @SuppressLint("CustomViewStyleable")
//        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.preview_view);
//        this.mRatio = array.getFloat(R.styleable.preview_view_ratio, 1F);
//        array.recycle();
    }

    public void setRawPreviewSize(CameraTextureView.Size size) {
        if (size != null && size.isLegal()) {
            this.mRatio = size.width / (size.height * 1.0F);
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        //        setMeasuredDimension(width, width * 4 / 3);
        //        setTranslationY(-(width / 3F / 2F));

        //        setMeasuredDimension(width, width * 16 / 9);
        //        setTranslationY(-(width * 7 / 9F / 2F));

        if (this.mRatio <= 1) {
            setMeasuredDimension(width, (int) (width * (1 / this.mRatio)));
            setTranslationY(-(width * ((1 / this.mRatio) - 1) / 2F));
        } else {
            setMeasuredDimension((int) (height * this.mRatio), height);
            setTranslationX(-(height * (this.mRatio - 1) / 2F));
        }
    }


    public static class Size {
        public final int width;
        public final int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public boolean isLegal() {
            return this.width > 0 && this.height > 0
                    && (Math.max(this.width, this.height) / Math.min(this.width, this.height) < 2);
        }

        @Override
        public String toString() {
            return "Size{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

}
