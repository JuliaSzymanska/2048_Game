
package com.game.a2048_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import me.aflak.libraries.callback.FingerprintDialogCallback;
import me.aflak.libraries.dialog.FingerprintDialog;

// TODO: 19.07.2020  powiązanie musi zawierać załączniki w postaci wszystkich dokumentów powiązanych
//  (not katalogowych, dokumentów RFC, aktów prawnych, itp.,
//  a także wszystkich programów źródłowych (także bibliotek open source'owych), a także programów wykonywalnych
//  do czego zmierzam, pamiętać żeby to dodać jako biblioteka opensourcowa
//  https://github.com/OmarAflak/Fingerprint


// TODO: 19.07.2020 spojrzeć na bindingi

// TODO: 20.07.2020 https://www.youtube.com/watch?v=0s6x3Sn4eYo - animacja przejść między activites

public class MainActivity extends AppCompatActivity implements FingerprintDialogCallback {

    MainActivity mainActivity = this;
    Boolean authentication = false;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initButtons();
        loadData();
        if (isFingerPrintSensorAvailable(this)) {
            initFingerprintDialog();
        }
    }

    public static boolean isFingerPrintSensorAvailable(Context context) {
        FingerprintManager manager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        return (manager != null && manager.isHardwareDetected() && manager.hasEnrolledFingerprints());
    }

    private void loadData() {
        SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.settings), MODE_PRIVATE);
        boolean isDarkTheme = preferences.getBoolean(getResources().getString(R.string.dark_theme), false);
        setTheme(isDarkTheme);
    }

    // TODO: 19.07.2020 spojrzeć na te i podobne funkcje
    private void setTheme(boolean isDarkTheme) {
        ImageView darkThemeView = (ImageView) findViewById(R.id.darkThemeView);
        if (isDarkTheme) {
            darkThemeView.setImageResource(R.drawable.dark_theme_on);
        } else {
            darkThemeView.setImageResource(R.drawable.dark_theme_off);
        }
    }

    private void initButtons() {
        configureStartGameButton();
        configureAuthenticateButton();
    }

    private void configureStartGameButton() {
        Button startGame = (Button) findViewById(R.id.startGameButton);
        startGame.setBackgroundResource(R.drawable.main_activity_button);
        startGame.setOnClickListener(initializeBoardActivity);
    }

    private void configureAuthenticateButton() {
        Button authenticationButton = (Button) findViewById(R.id.authenticateButton);
        if (isFingerPrintSensorAvailable(this)) {
            authenticationButton.setBackgroundResource(R.drawable.fingerprint);
            authenticationButton.setOnClickListener(authenticationListener);
        } else {
            authenticationButton.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener authenticationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            initFingerprintDialog();
        }
    };

    private View.OnClickListener initializeBoardActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, BoardActivity.class);
            i.putExtra(String.valueOf(R.string.authentication), Boolean.toString(authentication));
            startActivity(i);
        }
    };

    private void initFingerprintDialog() {
        if (FingerprintDialog.isAvailable(mainActivity)) {
            FingerprintDialog.initialize(mainActivity)
                    .title(R.string.fingerprint_title)
                    .message(R.string.fingerprint_message)
                    .callback(mainActivity)
                    .show();
        }
    }

    @Override
    public void onAuthenticationSucceeded() {
        this.authentication = true;
    }

    @Override
    public void onAuthenticationCancel() {

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}

