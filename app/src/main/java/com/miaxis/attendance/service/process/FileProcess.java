package com.miaxis.attendance.service.process;

import fi.iki.elonen.NanoHTTPD;

/**
 * @author Tank
 * @date 2021/8/23 5:28 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class FileProcess {

    public static class AddFile implements BaseProcess {
        public AddFile() {
        }

        @Override
        public NanoHTTPD.Response onProcess(NanoHTTPD.IHTTPSession session) throws Exception {
            return null;
        }
    }

}



