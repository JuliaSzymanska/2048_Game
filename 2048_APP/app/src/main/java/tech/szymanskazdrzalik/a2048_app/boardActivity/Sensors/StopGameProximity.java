package tech.szymanskazdrzalik.a2048_app.boardActivity.Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import tech.szymanskazdrzalik.a2048_app.R;
import tech.szymanskazdrzalik.a2048_app.boardActivity.BoardActivityListener;
import tech.szymanskazdrzalik.module.Game;

import static tech.szymanskazdrzalik.a2048_app.boardActivity.buttons.SettingsButton.chosenSensors;

/**
 * Pauses or unpauses game depending on proximity level.
 */
public class StopGameProximity implements Runnable, SensorEventListener {

    private final static int PROXIMITY_DISTANCE = 5;

    private float mProximityData;
    private Game game;
    private Context context;
    private BoardActivityListener boardActivityListener;

    /**
     * Class constructor.
     * @param context context from activity.
     * @param game current game.
     */
    public StopGameProximity(Context context, Game game) {
        this.context = context;
        this.game = game;
        this.boardActivityListener = (BoardActivityListener) context;
    }

    /**
     * {@inheritDoc}
     * Pauses or unpauses game according to {@link StopGameProximity#mProximityData}.
     */
    @Override
    public void run() {
        if (mProximityData < PROXIMITY_DISTANCE) {
            if (!game.isSuspended()) {
                boardActivityListener.callback(context.getString(R.string.SetPauseOn));
            }
        } else if (game.isSuspended()) {
            boardActivityListener.callback(context.getString(R.string.SetPauseOff));
        }
    }

    /**
     * {@inheritDoc}
     * Starts new thread to pause or unpause game.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        mProximityData = event.values[0];
        if (chosenSensors[3]) {
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