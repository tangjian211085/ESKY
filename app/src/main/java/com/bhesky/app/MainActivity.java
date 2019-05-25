package com.bhesky.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.puhui.lib.utils.DMLog;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, ProcessService.class));

        DMLog.e(this.getClass().getSimpleName(), "" + stringFromJNI());
    }

    public native String stringFromJNI();
}
