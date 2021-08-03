package com.miaxis.httpserver;

import android.os.Bundle;

import org.nanohttpd.DebugServer;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    DebugServer debugServer = new DebugServer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            debugServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            debugServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}