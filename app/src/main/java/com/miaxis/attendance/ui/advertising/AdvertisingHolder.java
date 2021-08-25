package com.miaxis.attendance.ui.advertising;

import android.view.View;

import com.bumptech.glide.Glide;
import com.miaxis.attendance.R;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Tank
 * @date 2021/8/25 2:45 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class AdvertisingHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private AdvertisingClickListener mClickListener;
    private Advertising mAdvertising;

    public AdvertisingHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
    }

    public void bind(AdvertisingClickListener advertisingClickListener) {
        this.mClickListener = advertisingClickListener;
    }

    public void bind(Advertising advertising) {
        this.mAdvertising = advertising;
        AppCompatImageView aiv_image = itemView.findViewById(R.id.aiv_image);
        Glide.with(aiv_image).load(advertising.imageId).error(R.mipmap.ic_launcher).into(aiv_image);
    }

    @Override
    public void onClick(View v) {
        if (this.mClickListener != null) {
            this.mClickListener.onAdvertisingClick(this.mAdvertising);
        }
    }
}
