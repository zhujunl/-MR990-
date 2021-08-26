package com.miaxis.attendance.service.process.base;


import android.util.Log;

import com.miaxis.attendance.service.HttpServer;
import com.miaxis.attendance.service.MxResponse;

import org.nanohttpd.NanoHTTPD;

/**
 * @author Tank
 * @date 2021/8/23 5:28 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public abstract class BaseGetProcess implements BaseProcess {

    protected final String TAG = "BaseGetProcess";

    public abstract MxResponse<?> onProcess(NanoHTTPD.IHTTPSession session) throws Exception;

    public NanoHTTPD.Response process(NanoHTTPD.IHTTPSession session) throws Exception {
        Log.e(TAG, "Method:" + session.getMethod());
        if (NanoHTTPD.Method.GET != session.getMethod()) {
            return NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.METHOD_NOT_ALLOWED, NanoHTTPD.MIME_PLAINTEXT, "Error method");
        }
        MxResponse<?> mxResponse = onProcess(session);
        Log.e(TAG, "Response: " + mxResponse);
        return NanoHTTPD.newFixedLengthResponse(
                NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_JSON, HttpServer.Gson.toJson(mxResponse));
    }

}



