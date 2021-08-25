package com.miaxis.attendance.service.process;


import com.miaxis.attendance.service.process.base.BaseProcess;

import org.nanohttpd.NanoHTTPD;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tank
 * @date 2021/8/23 5:28 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public abstract class PostProcess extends BaseProcess {

    @Override
    public NanoHTTPD.Response process(NanoHTTPD.IHTTPSession session) throws Exception {
        if (NanoHTTPD.Method.POST != session.getMethod()) {
            return NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.METHOD_NOT_ALLOWED, NanoHTTPD.MIME_PLAINTEXT, "Error method");
        }
        Map<String, String> param = new HashMap<>();
        session.parseBody(param);
        return onProcess(session);
    }

}



