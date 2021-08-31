package com.miaxis.attendance.config;

import android.os.Environment;

import java.io.File;

/**
 * @author Tank
 * @date 2021/8/26 1:13 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class AppConfig {

    private static final String MainPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Miaxis" + File.separator;
    public static final String Path_FaceImage = MainPath + "Image_Face" + File.separator;
    public static final String Path_FingerImage = MainPath + "Finger_Face" + File.separator;
    public static final String Path_File = MainPath + "File" + File.separator;
    public static final String Path_CaptureImage = MainPath + "Image_Capture" + File.separator;
    public static final String Path_CutImage = MainPath + "Image_Cut" + File.separator;
    public static final String Path_DataBase = MainPath + "DataBase" + File.separator + "attendance.db";


    public static final long CloseDoorDelay = 8 * 1000L;

    public static final int Server_Port = 8090;

}
