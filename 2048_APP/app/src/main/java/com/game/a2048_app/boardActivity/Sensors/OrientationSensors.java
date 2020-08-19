package com.game.a2048_app.boardActivity.Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.game.a2048_app.R;
import com.game.a2048_app.boardActivity.BoardActivityListener;

import static com.game.a2048_app.boardActivity.buttons.SettingsButton.chosenSensors;

public class OrientationSensors implements SensorEventListener {

    private final static double MIN_PITCH = 0.05;
    private final static double MIN_ROLL = 0.05;

    private final static float MIN_GYRO_VALUE_HORIZONTAL = 3f;
    private final static float MIN_GYRO_VALUE_VERTICAL = 2f;

    private final static double HORIZONTAL_PITCH_MAX = 0.5;
    private final static double HORIZONTAL_PITCH_MIN = -0.5;

    private final static double changeColourAzimuthBreakpoint1 = 0.25;
    private final static double changeColourAzimuthBreakpoint2 = 1.25;
    private final static double changeColourAzimuthBreakpoint3 = 1.75;
    private final static double changeColourAzimuthBreakpoint4 = 2.75;

    private float[] mAccelerometerData = new float[3];
    private float[] mGyroscopeData = new float[3];
    private float[] mMagnetometerData = new float[3];
    private Context context;
    private BoardActivityListener boardActivityListener;

    /**
     * Class constructor.
     *
     * @param context context from activity.
     */
    public OrientationSensors(Context context) {
        this.context = context;
        this.boardActivityListener = (BoardActivityListener) context;
    }

    /**
     * @return {@link SensorManager#getOrientation(float[], float[])} with a rotation matrix optained from
     * {@link SensorManager#getRotationMatrix(float[], float[], float[], float[])}
     */
    private float[] getOrientationValues() {
        float[] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix,
                null, mAccelerometerData, mMagnetometerData);
        float[] orientationValues = new float[3];
        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrix, orientationValues);
        }
        return orientationValues;
    }

    /**
     * Method to check the current Gyroscope, Magnetometer and Accelerometer values and use {@link BoardActivityListener#callback(String)}
     * on parent {@link com.game.a2048_app.boardActivity.BoardActivity} class to cause a move in {@link com.game.module.Game}.
     */
    private class MakeMove implements Runnable {

        @Override
        public void run() {
            try {
                float[] orientationValues = getOrientationValues();

                float azimuth = orientationValues[0];
                float pitch = orientationValues[1];
                float roll = orientationValues[2];

                if (mGyroscopeData[0] > MIN_GYRO_VALUE_VERTICAL && pitch < -MIN_PITCH) {
                    boardActivityListener.callback(context.getString(R.string.MoveDown));
                } else if (mGyroscopeData[0] < -MIN_GYRO_VALUE_VERTICAL && pitch > MIN_PITCH) {
                    boardActivityListener.callback(context.getString(R.string.MoveUP));
                } else if (mGyroscopeData[1] > MIN_GYRO_VALUE_HORIZONTAL && roll > MIN_ROLL) {
                    boardActivityListener.callback(context.getString(R.string.MoveRight));
                } else if (mGyroscopeData[1] < -MIN_GYRO_VALUE_HORIZONTAL && roll < -MIN_ROLL) {
                    boardActivityListener.callback(context.getString(R.string.MoveLeft));
                }
            } finally {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Method to check the current Gyroscope, Magnetometer and Accelerometer values and use {@link BoardActivityListener#callback(String)}
     * on parent {@link com.game.a2048_app.boardActivity.BoardActivity} class to change the background colours of the displayed game board
     * depending on the direction of the world the phone is currently pointing towards.
     */
    private class ChangeColour implements Runnable {

        @Override
        public void run() {
            float[] orientationValues = getOrientationValues();
            final float azimuth = orientationValues[0];
            final float pitch = orientationValues[1];
            if (pitch > HORIZONTAL_PITCH_MIN && pitch < HORIZONTAL_PITCH_MAX) {
                if (azimuth >= changeColourAzimuthBreakpoint1 && azimuth < changeColourAzimuthBreakpoint2) {
                    boardActivityListener.callback(context.getString(R.string.Button_Green));
                } else if (azimuth >= changeColourAzimuthBreakpoint3 && azimuth < changeColourAzimuthBreakpoint4) {
                    boardActivityListener.callback(context.getString(R.string.Button_Green_Light));
                } else if (azimuth >= -changeColourAzimuthBreakpoint4 && azimuth < -changeColourAzimuthBreakpoint3) {
                    boardActivityListener.callback(context.getString(R.string.Button_Blue));
                } else if (azimuth > -changeColourAzimuthBreakpoint2 && azimuth < -changeColourAzimuthBreakpoint1) {
                    boardActivityListener.callback(context.getString(R.string.Button_Blue_Light));
                } else {
                    return;
                }
                boardActivityListener.callback(context.getString(R.string.Notify_Adapter));
            }
        }
    }

    /**
     * {@inheritDoc}
     * Calls {@link ChangeColour} or {@link MakeMove} depending on the {@link SensorEvent} and updates the
     * {@link OrientationSensors#mGyroscopeData}, {@link OrientationSensors#mAccelerometerData},
     * {@link OrientationSensors#mMagnetometerData} variables.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_GYROSCOPE:
                this.mGyroscopeData = event.values;
                if (chosenSensors[0]) {
                    new Thread(new MakeMove()).start();
                }
                break;
            case Sensor.TYPE_ACCELEROMETER:
                this.mAccelerometerData = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = event.values.clone();
                if (chosenSensors[1]) {
                    new Thread(new ChangeColour()).start();
                }
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
