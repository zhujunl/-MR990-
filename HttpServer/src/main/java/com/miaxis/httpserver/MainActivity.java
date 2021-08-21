package com.miaxis.httpserver;

import android.os.Bundle;
import android.util.Log;

import org.nanohttpd.DebugServer;

import java.io.IOException;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
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

        int[] ints = new int[]{1, 2, 1, 2, 1, 4, 5, 54, 656, 676, 7, 5};

        int[] in = new int[ints.length];

        System.arraycopy(ints, 0, in, 0, ints.length);
        Log.e(TAG, "onCreate: " + Arrays.toString(in));
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