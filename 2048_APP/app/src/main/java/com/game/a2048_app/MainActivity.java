
package com.game.a2048_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.game.module.Game;

import me.aflak.libraries.callback.FingerprintDialogCallback;
import me.aflak.libraries.dialog.FingerprintDialog;


public class MainActivity extends AppCompatActivity implements FingerprintDialogCallback{

    MainActivity mainActivity = this;
    Button loadGame;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initButtons();
        if (FingerprintDialog.isAvailable(mainActivity)) {
            FingerprintDialog.initialize(mainActivity)
                    .title(R.string.fingerprint_title)
                    .message(R.string.fingerprint_message)
                    .callback(mainActivity)
                    .show();
        }
    }

    private void initButtons() {
        configureStartGameButton();
        configureLoadGameButton();
    }

    private View.OnClickListener initializeBoardActivityForNewGame = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, BoardActivity.class));
        }
    };

    private View.OnClickListener initializeBoardActivityForLoadedGame = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Game.getInstance().setCreateNewGame(false);
            startActivity(new Intent(MainActivity.this, BoardActivity.class));
        }
    };

    private void configureStartGameButton(){
        Button startNewGame = (Button) findViewById(R.id.startNewGameButton);
        startNewGame.setOnClickListener(initializeBoardActivityForNewGame);
    }

    private void configureLoadGameButton(){
        loadGame = (Button) findViewById(R.id.loadGameButton);
        loadGame.setOnClickListener(initializeBoardActivityForLoadedGame);
        loadGame.setEnabled(false);
    }

    @Override
    public void onAuthenticationSucceeded() {
        Game.getInstance().setUserAuthenticated(true);
        loadGame.setEnabled(true);
    }

    @Override
    public void onAuthenticationCancel() {
    // FIXME: 11.07.2020
    }
}

