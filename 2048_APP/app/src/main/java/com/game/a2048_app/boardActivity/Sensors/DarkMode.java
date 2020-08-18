package com.game.a2048_app.boardActivity.Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.game.a2048_app.R;
import com.game.a2048_app.boardActivity.BoardActivityListener;
import com.game.a2048_app.helpers.PreferencesHelper;

import static com.game.a2048_app.boardActivity.buttons.SettingsButton.chosenSensors;

/**
 * Sets dark or light mode depending on the light level.
 */
public class DarkMode implements Runnable, SensorEventListener {

    public DarkMode(Context context) {
        this.context = context;
        this.boardActivityListener = (BoardActivityListener) context;
    }

    private Context context;

    private PreferencesHelper preferencesHelper = PreferencesHelper.getInstance();
    private BoardActivityListener boardActivityListener;
    private float mLightData;

    private final static int DARK_MODE_ENABLE_LIGHT = 30;
    private final static int DARK_MODE_DISABLE_LIGHT = 50;


    /**
     * {@inheritDoc}
     * Calls {@link BoardActivityListener} to change the theme of the parent activity.
     */
    @Override
    public void run() {
        // Light Sensor - gdy jest ciemno włącza się dark mode
        if (mLightData <= DARK_MODE_ENABLE_LIGHT && !preferencesHelper.getDarkTheme()) {
            preferencesHelper.setDarkTheme(true);
            boardActivityListener.callback(context.getString(R.string.SetTheme));
        } else if (mLightData >= DARK_MODE_DISABLE_LIGHT && preferencesHelper.getDarkTheme()) {
            preferencesHelper.setDarkTheme(false);
            boardActivityListener.callback(context.getString(R.string.SetTheme));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        this.mLightData = event.values[0];
        if (chosenSensors[2]) {
            new Thread(this).start();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}