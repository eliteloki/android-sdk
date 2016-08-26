package com.elite.todo_sdk_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(PreferenceManager.getDefaultSharedPreferences(SplashScreenActivity.this).getBoolean("com.elite.todo_sdk_test.LoginCheck",false)){
                    startActivity(new Intent(SplashScreenActivity.this,TodoActivity.class));
                }else {
                    startActivity(new Intent(SplashScreenActivity.this,LoginActivity.class));
                }
                finish();
            }
        },1500);
    }
}
