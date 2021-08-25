package com.miaxis.attendance.service.process;

import android.util.Log;

import com.miaxis.attendance.data.model.PersonModel;
import com.miaxis.attendance.service.HttpServer;
import com.miaxis.attendance.service.MxResponse;
import com.miaxis.attendance.service.MxResponseCode;
import com.miaxis.attendance.service.bean.User;
import com.miaxis.attendance.service.transform.PersonTransform;
import com.miaxis.common.utils.MapUtils;

import org.nanohttpd.NanoHTTPD;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Tank
 * @date 2021/8/23 5:28 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class UserProcess {

    private final ConcurrentHashMap<String, Integer> mAddUserProcess = new ConcurrentHashMap<>();


    public static class QueryAllUser extends PostProcess {
        public QueryAllUser() {
        }

        @Override
        public NanoHTTPD.Response onProcess(NanoHTTPD.IHTTPSession session) throws Exception {
            Map<String, String> parms = session.getParms();
            if (!MapUtils.isNullOrEmpty(parms)) {
                return NanoHTTPD.newFixedLengthResponse(
                        NanoHTTPD.Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, null);
            }
            return NanoHTTPD.newFixedLengthResponse(HttpServer.Gson.toJson(MxResponse.CreateSuccess(PersonModel.findAll())));
        }
    }

    public static class AddUser extends PostProcess {
        public AddUser() {
        }

        @Override
        public NanoHTTPD.Response onProcess(NanoHTTPD.IHTTPSession session) throws Exception {
            Map<String, String> parms = session.getParms();
            Log.e("AddUser", "onProcess: " + HttpServer.Gson.toJson(parms));
            if (MapUtils.isNullOrEmpty(parms)) {
                return NanoHTTPD.newFixedLengthResponse(
                        NanoHTTPD.Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Error parameter");
            }
            User user = HttpServer.Gson.fromJson(HttpServer.Gson.toJson(parms), User.class);
            if (user == null) {
                return NanoHTTPD.newFixedLengthResponse(
                        NanoHTTPD.Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Error format");
            }
            if (user.isIllegal()) {
                return NanoHTTPD.newFixedLengthResponse(HttpServer.Gson.toJson(MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER, user.toString())));
            }
            MxResponse<?> transform = PersonTransform.transform(user);
            if (!MxResponse.isSuccess(transform)) {
                return NanoHTTPD.newFixedLengthResponse(HttpServer.Gson.toJson(MxResponse.CreateFail(transform)));
            }
            return NanoHTTPD.newFixedLengthResponse(HttpServer.Gson.toJson(transform));
        }
    }

    public static class UpdateUser extends PostProcess {

        public UpdateUser() {
        }

        @Override
        public NanoHTTPD.Response onProcess(NanoHTTPD.IHTTPSession session) throws Exception {
            return null;
        }
    }
}



