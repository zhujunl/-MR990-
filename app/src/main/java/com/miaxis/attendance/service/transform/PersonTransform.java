package com.miaxis.attendance.service.transform;

import android.util.Base64;
import android.util.Log;

import com.miaxis.attendance.data.entity.LocalImage;
import com.miaxis.attendance.data.entity.Person;
import com.miaxis.attendance.data.model.LocalImageModel;
import com.miaxis.attendance.service.HttpServer;
import com.miaxis.attendance.service.MxResponse;
import com.miaxis.attendance.service.MxResponseCode;
import com.miaxis.attendance.service.bean.User;
import com.miaxis.common.utils.FileUtils;

/**
 * @author Tank
 * @date 2021/8/23 7:24 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class PersonTransform {

    private static final String TAG = "PersonTransform";

    public static MxResponse<Person> transform(User user) {
        if (user == null) {
            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, MxResponseCode.MSG_ILLEGAL_PARAMETER);
        }
        //byte[] decode = Base64.getDecoder().decode(user.base_pic);
        //        java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
        //        byte[] decode = decoder.decode(user.base_pic);
        byte[] decode = android.util.Base64.decode(user.base_pic, Base64.URL_SAFE);
        String savePath = HttpServer.FilePath_Face + System.currentTimeMillis() + ".jpeg";
        boolean b = FileUtils.writeFile(savePath, decode);
        Log.e(TAG, "writeFile: " + b);
        if (!b) {
            return MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "save image failed");
        }
        LocalImage localImage = new LocalImage();
        localImage.Type = 1;
        localImage.ImagePath = savePath;
        long insert = LocalImageModel.insert(localImage);
        //        List<LocalImage> byID = LocalImageModel.findByID(user.base_pic);
        //        if (ListUtils.isNullOrEmpty(byID)) {
        //            return MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, MxResponseCode.MSG_ILLEGAL_PARAMETER);
        //        }
        Person person = new Person();
        //        public long id;
        //        public String name;//姓名（唯一，汉字）
        //        public String job_no;//工号（唯一，大小写字母和数字）
        //        public boolean ia_admin;//是否是管理员 0-否 1-是
        //        public String department_id;//部门id
        //        public String password;//密码（大小写字母，数字，特殊字符）
        //        public String base_pic;//底图
        //        public String pass_number;//当日通行次数（每日零点更新）
        //        public String id_number;//身份证号
        //        public String birthday;//生日
        //        public String is_delete;//是否删除 0-否 1-是
        //        public String create_time;//创建时间
        //        public String update_time;//修改时间
        person.UserId = "" + user.id;
        person.IdCardNumber = user.id_number;
        person.Number = user.id_number;
        person.Name = user.name;
        person.FaceImage = insert;
        return MxResponse.CreateSuccess(person);
    }


}
