package com.game.a2048_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.game.a2048_app.helpers.Preloader;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread loadImagesThread = new Thread() {
            @Override
            public void run() {
                Preloader preloader = Preloader.getInstance();
                Preloader.initContext(SplashActivity.this);
                preloader.loadAssets();

            }

        };
        loadImagesThread.start();
        while(loadImagesThread.isAlive()){
        }
        int SPLASH_TIME_OUT = 1000;
        new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                }, SPLASH_TIME_OUT);

    }
}