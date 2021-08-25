package com.miaxis.attendance.service.process;


import android.util.Base64;
import android.util.Log;

import com.miaxis.attendance.data.entity.LocalImage;
import com.miaxis.attendance.data.model.LocalImageModel;
import com.miaxis.attendance.service.HttpServer;
import com.miaxis.attendance.service.MxResponse;
import com.miaxis.attendance.service.MxResponseCode;
import com.miaxis.common.utils.FileUtils;
import com.miaxis.common.utils.MapUtils;
import com.miaxis.common.utils.StringUtils;

import org.nanohttpd.NanoHTTPD;

import java.util.Map;

/**
 * @author Tank
 * @date 2021/8/23 5:28 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class FileProcess {

    public static class AddFile extends PostProcess {

        private static final String TAG = "AddFile";

        public AddFile() {
        }

        @Override
        public NanoHTTPD.Response onProcess(NanoHTTPD.IHTTPSession session) throws Exception {
            Map<String, String> parms = session.getParms();
            if (MapUtils.isNullOrEmpty(parms)) {
                return NanoHTTPD.newFixedLengthResponse(
                        NanoHTTPD.Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, null);
            }
            String file = parms.get("file");
            if (StringUtils.isNullOrEmpty(file)) {
                return NanoHTTPD.newFixedLengthResponse(HttpServer.Gson.toJson(MxResponse.CreateFail(MxResponseCode.CODE_ILLEGAL_PARAMETER,MxResponseCode.MSG_ILLEGAL_PARAMETER)));
            }
            byte[] decode = Base64.decode(file, Base64.NO_WRAP);
            String savePath = HttpServer.FilePath_Face + System.currentTimeMillis() + ".jpeg";
            boolean b = FileUtils.writeFile(savePath, decode);
            Log.e(TAG, "writeFile: " + b);
            if (!b) {
                return NanoHTTPD.newFixedLengthResponse(HttpServer.Gson.toJson(MxResponse.CreateFail(MxResponseCode.CODE_OPERATION_ERROR, "save image failed")));
            }
            LocalImage localImage = new LocalImage();
            localImage.Type = 1;
            localImage.ImagePath = savePath;
            long insert = LocalImageModel.insert(localImage);
            return NanoHTTPD.newFixedLengthResponse(HttpServer.Gson.toJson(MxResponse.CreateSuccess(insert)));
        }
    }

}



