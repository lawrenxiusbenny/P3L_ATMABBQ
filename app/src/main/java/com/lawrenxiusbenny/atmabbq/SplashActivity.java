package com.lawrenxiusbenny.atmabbq;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private ImageView splash_logo,splash_bg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash_bg = findViewById(R.id.splash_bg);
        splash_logo = findViewById(R.id.splash_logo);

        splash_bg.animate().translationY(-5000).setDuration(1000).setStartDelay(2500);
        splash_logo.animate().translationY(2000).setDuration(1000).setStartDelay(2500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        },3500);
    }
}