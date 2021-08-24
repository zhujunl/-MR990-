package com.miaxis.attendance.service.process;


import org.nanohttpd.NanoHTTPD;

/**
 * @author Tank
 * @date 2021/8/23 5:28 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public interface BaseProcess {

    NanoHTTPD.Response onProcess(NanoHTTPD.IHTTPSession session) throws Exception;

}



