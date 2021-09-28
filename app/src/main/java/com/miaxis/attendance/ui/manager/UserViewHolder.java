package com.miaxis.attendance.ui.manager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.miaxis.attendance.R;
import com.miaxis.common.utils.ListUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Tank
 * @date 2021/9/27 7:24 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class UserViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "UserViewHolder";

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(MxUser mxUser) {
        Log.e(TAG, "bind: " + mxUser);
        if (mxUser == null) {
            return;
        }
        TextView tv_name = itemView.findViewById(R.id.tv_name);
        tv_name.setText(String.valueOf(mxUser.name));
        TextView tv_work = itemView.findViewById(R.id.tv_work);
        tv_work.setText(String.valueOf(mxUser.number));

        ImageView iv_face = itemView.findViewById(R.id.iv_face);
        Glide.with(iv_face).load(mxUser.face).into(iv_face);

        List<String> fingers = mxUser.fingers;
        LinearLayout ll_fingers = itemView.findViewById(R.id.ll_fingers);

        if (!ListUtils.isNullOrEmpty(fingers)) {
            for (String path : fingers) {
                ImageView finger = (ImageView) LayoutInflater.from(ll_fingers.getContext()).inflate(R.layout.view_finger, ll_fingers, false);
                Glide.with(finger).load(path).into(finger);
                ll_fingers.addView(finger);
            }
        }else {
            ImageView finger = (ImageView) LayoutInflater.from(ll_fingers.getContext()).inflate(R.layout.view_finger, ll_fingers, false);
            ll_fingers.addView(finger);
            finger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }


}
