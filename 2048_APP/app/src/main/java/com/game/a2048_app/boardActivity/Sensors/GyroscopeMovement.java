package com.game.a2048_app.boardActivity.Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.game.a2048_app.R;
import com.game.a2048_app.boardActivity.OurCustomListenerFIXMERenameME;

public class GyroscopeMovement implements Runnable, SensorEventListener {

    private final static float minGyroValue = 2f;

    private float[] mGyroscopeData = new float[3];
    private Context context;
    private OurCustomListenerFIXMERenameME ourCustomListenerFIXMERenameME;

    public GyroscopeMovement(Context context) {
        this.context = context;
        this.ourCustomListenerFIXMERenameME = (OurCustomListenerFIXMERenameME) context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_GYROSCOPE) {
            this.mGyroscopeData = event.values;
            new Thread(this).start();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if(this.mGyroscopeData[0] > minGyroValue) {
            ourCustomListenerFIXMERenameME.callback(context.getString(R.string.MoveDown));
        } else if(this.mGyroscopeData[0] < -minGyroValue) {
            ourCustomListenerFIXMERenameME.callback(context.getString(R.string.MoveUP));
        } else if(this.mGyroscopeData[1] > minGyroValue) {
            ourCustomListenerFIXMERenameME.callback(context.getString(R.string.MoveRight));
        } else if(this.mGyroscopeData[1] < -minGyroValue) {
            ourCustomListenerFIXMERenameME.callback(context.getString(R.string.MoveLeft));
        }
    }
}
