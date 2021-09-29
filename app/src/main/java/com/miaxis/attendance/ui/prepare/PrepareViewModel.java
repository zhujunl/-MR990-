package com.miaxis.attendance.ui.prepare;

import com.google.gson.Gson;
import com.miaxis.attendance.App;
import com.miaxis.attendance.api.HttpApi;
import com.miaxis.attendance.api.HttpResponse;
import com.miaxis.attendance.api.bean.UserBean;
import com.miaxis.attendance.service.MxResponse;
import com.miaxis.attendance.service.bean.User;
import com.miaxis.attendance.service.transform.PersonTransform;
import com.miaxis.common.utils.ListUtils;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import timber.log.Timber;

public class PrepareViewModel extends ViewModel {

    MutableLiveData<String> progressMsg = new MutableLiveData<>();
    MutableLiveData<Boolean> result = new MutableLiveData<>();
    MutableLiveData<String> msg = new MutableLiveData<>();

    public PrepareViewModel() {
    }

    public void init() {
        progressMsg.setValue("正在请求数据中");
        msg.setValue("");
        Disposable subscribe = Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            Response<HttpResponse<List<UserBean>>> execute = HttpApi.getUserList().execute();
            HttpResponse<List<UserBean>> body = execute.body();
            Timber.e("getUserList:" + body);
            if (!body.isSuccess()) {
                throw new IllegalArgumentException("获取用户信息失败，" + body.message);
            } else {
                List<UserBean> result = body.result;
                if (ListUtils.isNullOrEmpty(result)) {
                    throw new IllegalArgumentException("获取用户信息成功");
                }
                progressMsg.postValue("处理数据中");
                int success = 0;
                int failed = 0;
                int total = result.size();
                Gson gson = new Gson();
                for (UserBean userBean : result) {
                    User user = new User();
                    user.id = userBean.id;
                    user.id_number = userBean.idNumber;
                    user.name = userBean.name;
                    user.job_no = userBean.jobNo;
                    user.department_id = "" + userBean.departmentId;
                    user.url_face = userBean.basePic;
                    user.url_fingers = gson.toJson(userBean.fingerList);
                    MxResponse<?> mxResponse = PersonTransform.insertOrUpdate(user);
                    Timber.e("insertOrUpdate:" + mxResponse);
                    if (MxResponse.isSuccess(mxResponse)) {
                        success++;
                    } else {
                        failed++;
                        msg.postValue((msg.getValue() == null ? "" :
                                msg.getValue()) + "添加失败，姓名：" + user.name + "，错误：" + mxResponse.getMessage() + "\n");
                        HttpResponse<?> response = HttpApi.exceptionReport(user.id, mxResponse.getMessage());
                        Timber.e("exceptionReport:%s", response);
                        //msg.postValue((msg.getValue() == null ? "" :
                        //        "上传错误信息" + (response.isSuccess() ? "成功" : "," + response.message) + "\n"));
                    }
                    progressMsg.postValue("总共：" + total + "   成功：" + success + "   失败：" + failed);
                    break;
                }
                emitter.onNext(success >= total);
            }
        }).subscribeOn(Schedulers.from(App.getInstance().threadExecutor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    result.setValue(success);
                }, throwable -> {
                    Timber.e("getUserList  throwable:" + throwable);
                    msg.setValue("" + throwable.getMessage());
                    result.setValue(false);
                });
    }

}