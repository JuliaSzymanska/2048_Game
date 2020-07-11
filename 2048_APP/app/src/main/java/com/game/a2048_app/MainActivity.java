
package com.game.a2048_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;

import com.game.module.Game;

import me.aflak.libraries.callback.FingerprintCallback;
import me.aflak.libraries.callback.FingerprintDialogCallback;
import me.aflak.libraries.dialog.FingerprintDialog;
import me.aflak.libraries.view.Fingerprint;

public class MainActivity extends AppCompatActivity implements FingerprintDialogCallback{

    MainActivity mainActivity = this;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureStartGameButton();
        configureAuthenticateButton();
    }

    private void configureStartGameButton(){
        Button startGame = (Button) findViewById(R.id.startGameButton);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BoardActivity.class));
            }
        });
    }

    private void configureAuthenticateButton() {
        Button startGame = (Button) findViewById(R.id.authenticateButton);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FingerprintDialog.isAvailable(mainActivity)){
                    FingerprintDialog.initialize(mainActivity)
                            .title(R.string.fingerprint_title)
                            .message(R.string.fingerprint_message)
                            .callback(mainActivity)
                            .show();
                }
            }
        });
    }


    @Override
    public void onAuthenticationSucceeded() {
        Game.getInstance().setUserAuthenticated(true);
    }

    @Override
    public void onAuthenticationCancel() {
    // FIXME: 11.07.2020
    }
}

