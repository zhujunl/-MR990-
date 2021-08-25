package com.miaxis.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author Tank
 * @date 2021/5/11 13:29
 * @des
 * @updateAuthor
 * @updateDes
 */
public class FileUtils {


    public static boolean initFile(String path) {
        if (StringUtils.isNullOrEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                boolean mkdirs = parentFile.mkdirs();
            }
        }
        return true;
    }


    /**
     * 把数据流写入文件
     *
     * @param path
     * @param bytes
     */
    public static boolean writeFile(String path, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return false;
        }
        FileOutputStream out = null;
        try {
            initFile(path);
            out = new FileOutputStream(path);//指定写到哪个路径中
            out.write(bytes);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static byte[] File2Bytes(File file) {
        int byte_size = 1024;
        byte[] b = new byte[byte_size];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(byte_size);
            for (int length; (length = fileInputStream.read(b)) != -1; ) {
                outputStream.write(b, 0, length);
            }
            fileInputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void delete(String path) {
        try {
            boolean delete = new File(path).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
