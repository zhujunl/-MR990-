package com.miaxis.application;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //byte[] bytes = File2Bytes(new File("/sdcard/1/11.jpeg"));
        //byte[] encode = Base64.encode(bytes, Base64.NO_WRAP);


        Log.e(TAG, "onCreate: " + Build.SERIAL);
        Log.e(TAG, "onCreate: " + Build.MANUFACTURER);


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

}