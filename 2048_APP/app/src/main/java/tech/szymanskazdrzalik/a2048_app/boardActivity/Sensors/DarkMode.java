package tech.szymanskazdrzalik.a2048_app.boardActivity.Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import tech.szymanskazdrzalik.a2048_app.R;
import tech.szymanskazdrzalik.a2048_app.boardActivity.BoardActivityListener;
import tech.szymanskazdrzalik.a2048_app.helpers.PreferencesHelper;

import static tech.szymanskazdrzalik.a2048_app.boardActivity.buttons.SettingsButton.chosenSensors;

/**
 * Sets dark or light mode depending on the light level.
 */
public class DarkMode implements Runnable, SensorEventListener {

    private final static int DARK_MODE_ENABLE_DARK_MODE = 30;
    private final static int DARK_MODE_DISABLE_DARK_MODE = 50;
    private Context context;
    private PreferencesHelper preferencesHelper = PreferencesHelper.getInstance();
    private BoardActivityListener boardActivityListener;
    private float mLightData;

    /**
     * Default class constructor. Sets class context.
     *
     * @param context context from activity.
     */
    public DarkMode(Context context) {
        this.context = context;
        this.boardActivityListener = (BoardActivityListener) context;
    }

    /**
     * {@inheritDoc}
     * Calls {@link BoardActivityListener} to change the theme of the parent activity.
     */
    @Override
    public void run() {
        if (mLightData <= DARK_MODE_ENABLE_DARK_MODE && !preferencesHelper.getDarkTheme()) {
            preferencesHelper.setDarkTheme(true);
            boardActivityListener.callback(context.getString(R.string.SetTheme));
        } else if (mLightData >= DARK_MODE_DISABLE_DARK_MODE && preferencesHelper.getDarkTheme()) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}