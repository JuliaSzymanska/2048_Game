
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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.game.a2048_app.helpers.PreferencesHelper;
import com.game.a2048_app.helpers.Preloader;

import me.aflak.libraries.callback.FingerprintDialogCallback;
import me.aflak.libraries.dialog.FingerprintDialog;

// TODO: 19.07.2020  powiązanie musi zawierać załączniki w postaci wszystkich dokumentów powiązanych
//  (not katalogowych, dokumentów RFC, aktów prawnych, itp.,
//  a także wszystkich programów źródłowych (także bibliotek open source'owych), a także programów wykonywalnych
//  do czego zmierzam, pamiętać żeby to dodać jako biblioteka opensourcowa
//  https://github.com/OmarAflak/Fingerprint

public class MainActivity extends AppCompatActivity implements FingerprintDialogCallback {

    private MainActivity mainActivity = this;
    private Boolean isAuthenticated = false;
    private Button startGameButton;
    private int volume = 1;
    MediaPlayer mediaPlayer;
    private Preloader preloader = Preloader.getInstance();
    private static final PreferencesHelper preferencesHelper = PreferencesHelper.getInstance();

    private ConstraintLayout constraintLayout;

    private OnSwipeTouchListener onSwipeTouchListener;


    /**
     * Called when the activity is starting.
     * Calls methods to load data and initialize buttons.
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down
     *                           then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise is null.
     */
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        PreferencesHelper.initContext(this);
        setContentView(R.layout.activity_main);
        initButtons();
        loadData();
        this.constraintLayout = findViewById(R.id.constraintLayoutMainActivity);
        this.setupSwipeListener();
    }

    /**
     * @param context current context.
     * @return if fingerprint sensor is available.
     */
    private boolean isFingerprintSensorAvailable(Context context) {
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
            darkThemeView.setImageDrawable(preloader.getDarkThemeOn());
        } else {
            darkThemeView.setImageDrawable(preloader.getDarkThemeOff());
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
        if (isFingerprintSensorAvailable(this)) {
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
            startGameButton.setBackground(preloader.getMainButtonClicked());
            mediaPlayer = MediaPlayer.create(mainActivity, R.raw.decline_call);
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    startGameButton.setBackground(preloader.getMainButton());
                    mediaPlayer.release();
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

    private void setupSwipeListener() {
        onSwipeTouchListener = new OnSwipeTouchListener(this, constraintLayout);
        final MainActivity mainActivity = this;
        onSwipeTouchListener.onSwipe = new OnSwipeTouchListener.onSwipeListener() {
            @Override
            public void swipeRight() {
            }

            @Override
            public void swipeTop() {
                // TODO: 02.08.2020 dodać odpowiedni transition
                //overridePendingTransition();
                startActivity(new Intent(mainActivity, Credits.class));
            }

            @Override
            public void swipeBottom() {
            }

            @Override
            public void swipeLeft() {
            }
        };
    }

    /**
     * {@inheritDoc}
     * {@link #isAuthenticated} is set to true
     */
    @Override
    public void onAuthenticationSucceeded() {
        this.isAuthenticated = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAuthenticationCancel() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        super.onStop();
    }
}

