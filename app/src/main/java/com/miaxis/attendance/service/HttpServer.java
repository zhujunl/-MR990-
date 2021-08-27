package com.miaxis.attendance.service;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.miaxis.attendance.service.process.AttendanceProcess;
import com.miaxis.attendance.service.process.FaceProcess;
import com.miaxis.attendance.service.process.FileProcess;
import com.miaxis.attendance.service.process.UserProcess;
import com.miaxis.attendance.service.process.base.BaseProcess;

import org.nanohttpd.NanoHTTPD;


/**
 * @author Tank
 * @date 2021/8/3 8:37 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class HttpServer extends NanoHTTPD {

    private final String TAG = "HttpServer";
    public static final Gson Gson = new Gson();
    //private final ConcurrentHashMap<String, BaseProcess> mProcess = new ConcurrentHashMap<>();

    public HttpServer(int port) {
        super(port);
    }


    @Override
    public Response serve(IHTTPSession session) {
        if (session != null) {
            BaseProcess process = getProcess(session);
            if (process != null) {
                try {
                    return process.process(session);
                } catch (Exception e) {
                    e.printStackTrace();
                    return NanoHTTPD.newFixedLengthResponse(
                            Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "" + e.getMessage());
                }
            }
            return NanoHTTPD.newFixedLengthResponse(
                    Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, null);
        }
        return NanoHTTPD.newFixedLengthResponse(
                Response.Status.NO_CONTENT, NanoHTTPD.MIME_PLAINTEXT, null);
    }


    private BaseProcess getProcess(IHTTPSession session) {
        if (session == null) {
            return null;
        }
        String uri = session.getUri();
        Log.e(TAG, "serve: uri:" + uri);
        if (TextUtils.isEmpty(uri)) {
            return null;
        }
        switch (uri) {
            case "/api/user/list":
                return new UserProcess.QueryAllUser();
            case "/api/user/add":
                return new UserProcess.AddUser();
            case "/api/user/update":
                return new UserProcess.UpdateUser();
            case "/api/user/delete":
                return new UserProcess.DeleteUser();
            case "/api/face/list":
                return new FaceProcess.QueryAllFace();
            case "/api/attendance/list":
                return new AttendanceProcess.QueryAll();
            case "/api/file/list":
                return new FileProcess.Query();
            case "/api/file/add":
                return new FileProcess.AddFile();
            default:
                return null;
        }
    }

}
