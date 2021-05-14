package com.lawrenxiusbenny.atmabbq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    private ImageView splash_logo,splash_bg;
//    private LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash_bg = findViewById(R.id.splash_bg);
        splash_logo = findViewById(R.id.splash_logo);
//        lottieAnimationView = (LottieAnimationView)findViewById(R.id.animasiSplash);
//        lottieAnimationView.playAnimation();

        splash_bg.animate().translationY(-5000).setDuration(1000).setStartDelay(2500);
        splash_logo.animate().translationY(2000).setDuration(1000).setStartDelay(2500);
//        lottieAnimationView.animate().translationY(2000).setDuration(1000).setStartDelay(2500);
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