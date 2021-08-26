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

    private static final String FilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Miaxis" + File.separator;
    public static final String Path_FaceImage = FilePath + "Image_Face" + File.separator;
    public static final String Path_File = FilePath + "File" + File.separator;
    public static final String Path_CaptureImage = FilePath + "Image_Capture" + File.separator;
    public static final String Path_CutImage = FilePath + "Image_Cut" + File.separator;
    public static final String Path_DataBase = FilePath + "DataBase" + File.separator + "attendance.db";


    public static final long CloseDoorDelay = 8 * 1000L;

}
