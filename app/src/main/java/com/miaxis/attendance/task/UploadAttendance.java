package com.miaxis.attendance.task;

import android.os.SystemClock;

import com.miaxis.attendance.api.HttpApi;
import com.miaxis.attendance.api.HttpResponse;
import com.miaxis.attendance.data.entity.Attendance;
import com.miaxis.attendance.data.entity.LocalImage;
import com.miaxis.attendance.data.model.AttendanceModel;
import com.miaxis.attendance.data.model.LocalImageModel;
import com.miaxis.common.utils.ListUtils;

import java.io.File;
import java.util.List;

import retrofit2.Response;

/**
 * @author Tank
 * @date 2021/8/26 9:44 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class UploadAttendance implements Runnable {

    private boolean isRunning;

    public UploadAttendance() {
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public void run() {
        this.isRunning = true;
        while (true){
            SystemClock.sleep(2000);
            List<Attendance> noUpload = AttendanceModel.findNoUpload();
            if (!ListUtils.isNullOrEmpty(noUpload)){
                Attendance attendance = noUpload.get(0);
                long captureImage = attendance.CaptureImage;
                List<LocalImage> localImages = LocalImageModel.findByID(captureImage);
                File file = null;
                if (!ListUtils.isNullOrEmpty(localImages)){
                    LocalImage localImage = localImages.get(0);
                     file = new File(localImage.ImagePath);
                }
                try {
                    Response<HttpResponse<String>> execute = HttpApi.uploadImage(file).execute();
                    if (execute.body().code.equals("200")){
                        attendance.Upload=1;
                        AttendanceModel.update(attendance);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
