package com.miaxis.application;

import android.os.Bundle;

import org.nanohttpd.webserver.SimpleWebServer;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Defaults
        int port = 8080;

        try {
            SimpleWebServer aaa = new SimpleWebServer("aaa", 8080, new File("/sdcard/miaxis/"), false);
            aaa.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}