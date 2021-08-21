package com.miaxis.attendance.data;

import androidx.lifecycle.MutableLiveData;

/**
 * @author Tank
 * @date 2021/8/19 5:43 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class AppData {

    MutableLiveData<String> IP = new MutableLiveData<>();

    MutableLiveData<String> date = new MutableLiveData<>();

    public AppData() {
    }


}
