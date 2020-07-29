
package com.game.a2048_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.fingerprint.FingerprintManager;
import android.media.MediaPlayer;
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

public class MainActivity extends AppCompatActivity implements FingerprintDialogCallback {

    private MainActivity mainActivity = this;
    private Boolean isAuthenticated = false;
    private Button startGameButton;
    private int volume = 1;
    MediaPlayer mediaPlayer;
    private static final PreferencesHelper preferencesHelper = PreferencesHelper.getInstance();


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        PreferencesHelper.initContext(this);
        setContentView(R.layout.activity_main);
        initButtons();
        loadData();
    }

    public static boolean isFingerPrintSensorAvailable(Context context) {
        FingerprintManager manager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        return (manager != null && manager.isHardwareDetected() && manager.hasEnrolledFingerprints());
    }

    /**
     * Call method to set theme.
     * Loads volume and current theme.
     */
    private void loadData() {
        boolean isDarkTheme = preferencesHelper.getDarkTheme();
        setTheme(isDarkTheme);
        this.volume = preferencesHelper.getVolume();
    }

    /**
     * Set dark or light theme.
     * @param isDarkTheme is dark theme on.
     */
    // TODO: 19.07.2020 spojrzeć na te i podobne funkcje
    private void setTheme(boolean isDarkTheme) {
        ImageView darkThemeView = (ImageView) findViewById(R.id.darkThemeView);
        if (isDarkTheme) {
            darkThemeView.setImageResource(R.drawable.dark_theme_on);
        } else {
            darkThemeView.setImageResource(R.drawable.dark_theme_off);
        }
    }

    /**
     * Calls method to initialize buttons.
     */
    private void initButtons() {
        configureStartGameButton();
        configureAuthenticateButton();
    }

    /**
     * Initialize start game button.
     * Sets its listener.
     */
    private void configureStartGameButton() {
        startGameButton = (Button) findViewById(R.id.startGameButton);
        startGameButton.setOnClickListener(initializeBoardActivity);
    }

    /**
     * Initialize authentication button.
     * Sets its listener.
     */
    private void configureAuthenticateButton() {
        Button authenticationButton = (Button) findViewById(R.id.authenticateButton);
        if (isFingerPrintSensorAvailable(this)) {
            authenticationButton.setOnClickListener(authenticationListener);
        } else {
            authenticationButton.setVisibility(View.GONE);
        }
    }

    /**
     * Creates button on click listener to authenticate user.
     */
    private View.OnClickListener authenticationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            initFingerprintDialog();
        }
    };

    /**
     * Creates button on click listener to start game.
     * Play sound after click and change button's image.
     */
    private View.OnClickListener initializeBoardActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startGameButton.setBackgroundResource(R.drawable.main_activity_button_clicked);
            mediaPlayer = MediaPlayer.create(mainActivity, R.raw.decline_call);
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    startGameButton.setBackgroundResource(R.drawable.main_activity_button);
                }

            });
            Intent i = new Intent(MainActivity.this, BoardActivity.class);
            i.putExtra(getResources().getString(R.string.authentication), Boolean.toString(isAuthenticated));
            startActivity(i);
        }
    };

    /**
     * Init fingerprint dialog to authenticate user.
     */
    private void initFingerprintDialog() {
        if (FingerprintDialog.isAvailable(mainActivity)) {
            FingerprintDialog.initialize(mainActivity)
                    .title(R.string.fingerprint_title)
                    .message(R.string.fingerprint_message)
                    .callback(mainActivity)
                    .show();
        }
    }

    /**
     * When user is authenticated successfully, local variable is sets to true.
     */
    @Override
    public void onAuthenticationSucceeded() {
        this.isAuthenticated = true;
    }

    @Override
    public void onAuthenticationCancel() {

    }

    /**
     * Pressing back button will close application.
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * On activity stop media player is released.
     */
    @Override
    protected void onStop() {
        super.onStop();
        this.mediaPlayer.release();
    }

}

