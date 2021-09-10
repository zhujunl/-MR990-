package com.miaxis.attendance.ui.finger;

import android.graphics.Bitmap;

import com.miaxis.attendance.config.AppConfig;
import com.miaxis.attendance.data.bean.AttendanceBean;
import com.miaxis.attendance.data.entity.Attendance;
import com.miaxis.attendance.data.entity.Finger;
import com.miaxis.attendance.data.entity.LocalImage;
import com.miaxis.attendance.data.entity.Person;
import com.miaxis.attendance.data.model.AttendanceModel;
import com.miaxis.attendance.data.model.LocalImageModel;
import com.miaxis.attendance.data.model.PersonModel;
import com.miaxis.common.response.ZZResponse;
import com.miaxis.common.utils.ListUtils;
import com.mx.finger.common.MxImage;
import com.mx.finger.utils.RawBitmapUtils;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class FingerViewModel extends ViewModel implements MR990FingerStrategy.ReadFingerCallBack {

    MutableLiveData<Boolean> StartCountdown = new MutableLiveData<>(true);
    MutableLiveData<ZZResponse<AttendanceBean>> mAttendance = new MutableLiveData<>();

    public FingerViewModel() {
    }

    public void readFinger() {
        MR990FingerStrategy.getInstance().readFinger(this);
    }

    public void stopRead() {
        MR990FingerStrategy.getInstance().stopRead();
    }

    public void resume() {
        MR990FingerStrategy.getInstance().resume();
        this.StartCountdown.setValue(true);
    }

    public void pause() {
        MR990FingerStrategy.getInstance().pause();
        this.StartCountdown.setValue(false);
    }

    @Override
    public void onReadFinger(MxImage finger) {
        this.StartCountdown.postValue(true);
    }

    @Override
    public void onExtractFeature(MxImage image, byte[] feature) {
    }

    @Override
    public void onFeatureMatch(MxImage image, byte[] feature, Finger finger, Bitmap bitmap) {
        String capturePath = null;
        String UserId = null;
        Person person = null;
        if (finger != null) {
            List<Person> byUserID = PersonModel.findByUserID(UserId = finger.UserId);
            if (!ListUtils.isNullOrEmpty(byUserID)) {
                person = byUserID.get(0);
                capturePath = AppConfig.Path_CaptureImage + "finger" + "_" + person.UserId + "_" + System.currentTimeMillis() + ".bmp";
            }
        }
        if (capturePath == null) {
            capturePath = AppConfig.Path_CaptureImage + "finger" + "_temp_" + System.currentTimeMillis() + ".bmp";
        }
        int saveBMP = RawBitmapUtils.saveBMP(capturePath, image.data, image.width, image.height);
        Timber.e("saveBMP:%s", saveBMP);
        if (saveBMP != 0) {
            return;
        }

        LocalImage captureLocalImage = new LocalImage();
        captureLocalImage.Type = 2;
        captureLocalImage.LocalPath = capturePath;
        captureLocalImage.id = LocalImageModel.insert(captureLocalImage);
        if (captureLocalImage.id <= 0) {
            return;
        }

        Attendance attendance = new Attendance();
        attendance.UserId = UserId;
        attendance.CaptureImage = captureLocalImage.id;
        attendance.Mode = 2;
        attendance.Status = finger == null ? 2 : 1;
        attendance.id = AttendanceModel.insert(attendance);
        if (attendance.id <= 0) {
            return;
        }

        AttendanceBean attendanceBean = new AttendanceBean();
        attendanceBean.Status = finger == null ? 2 : 1;
        attendanceBean.Mode = 2;
        attendanceBean.UserId = UserId;
        attendanceBean.CaptureImage = capturePath;
        attendanceBean.CutImage = capturePath;
        attendanceBean.UserName = person == null ? null : person.Name;
        if (finger == null) {
            this.mAttendance.postValue(ZZResponse.CreateFail(-203, "人员未找到"));
        } else {
            this.mAttendance.postValue(ZZResponse.CreateSuccess(attendanceBean));
        }
    }

}