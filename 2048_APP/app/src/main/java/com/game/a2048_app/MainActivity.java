
package com.game.a2048_app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.game.module.Game;

import java.util.Arrays;
import java.util.List;

import me.aflak.libraries.callback.FingerprintDialogCallback;
import me.aflak.libraries.dialog.FingerprintDialog;


public class MainActivity extends AppCompatActivity implements FingerprintDialogCallback {

    MainActivity mainActivity = this;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initButtons();
    }

    private void initButtons() {
        configureStartGameButton();
        configureAuthenticateButton();
        configureSettingsButton();
    }

    private View.OnClickListener authentication = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (FingerprintDialog.isAvailable(mainActivity)) {
                FingerprintDialog.initialize(mainActivity)
                        .title(R.string.fingerprint_title)
                        .message(R.string.fingerprint_message)
                        .callback(mainActivity)
                        .show();
            }
        }
    };

    private View.OnClickListener initializeBoardActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, BoardActivity.class));
        }
    };

    private View.OnClickListener settings = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            String[] sensors = new String[]{
                    "Light sensor",
                    "Proximity sensor",
                    "Magnetometer",
                    "Accelerometer + Gyroscope"
            };
            final List<String> sensorsList = Arrays.asList(sensors);
            final boolean[] chosenSensors = new boolean[]{false, false, false, false};

            builder.setMultiChoiceItems(sensors, chosenSensors, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    chosenSensors[which] = isChecked;
                    String currentItem = sensorsList.get(which);

                }
            });
            builder.setCancelable(false);
            builder.setTitle("Which sensors to enable?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };

    private void configureStartGameButton() {
        Button startGame = (Button) findViewById(R.id.startGameButton);
        startGame.setOnClickListener(initializeBoardActivity);
    }

    private void configureAuthenticateButton() {
        Button authenticationButton = (Button) findViewById(R.id.authenticateButton);
        authenticationButton.setBackgroundResource(R.drawable.fingerprint_without_background);
        authenticationButton.setOnClickListener(authentication);
    }

    private void configureSettingsButton() {
        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setBackgroundResource(R.drawable.settings);
        settingsButton.setOnClickListener(settings);
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

