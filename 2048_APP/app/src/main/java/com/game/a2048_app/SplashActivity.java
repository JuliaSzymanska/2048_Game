package com.game.a2048_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.game.a2048_app.helpers.Preloader;

public class SplashActivity extends AppCompatActivity {

    private final static int SPLASH_TIME_OUT = 1000;

    private Runnable loadImagesRunnable = new Runnable() {
        @Override
        public void run() {
            Preloader preloader = Preloader.getInstance();
            Preloader.initContext(SplashActivity.this);
            preloader.loadAssets();
        }
    };

    private Thread loadImagesThread;

    private Runnable waitForLoadImagesThreadRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                loadImagesThread.join();
            } catch (InterruptedException ignored) {

            } finally {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                });
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.loadImagesThread = new Thread(this.loadImagesRunnable);
        this.loadImagesThread.start();
        // król nazewnictwa
        Thread waitForLoadImagesThreadThread = new Thread(this.waitForLoadImagesThreadRunnable);
        waitForLoadImagesThreadThread.start();
    }
}