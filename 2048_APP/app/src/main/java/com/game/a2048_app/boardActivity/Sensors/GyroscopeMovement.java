package com.game.a2048_app.boardActivity.Sensors;

import android.content.Context;
import android.hardware.SensorManager;


import com.game.a2048_app.R;
import com.game.a2048_app.boardActivity.BoardActivityListener;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GyroscopeMovement implements Runnable {

    private static Lock lock = new ReentrantLock();

    private final static float MIN_GYRO_VALUE_HORIZONTAL = 3f;
    private final static float MIN_GYRO_VALUE_VERTICAL = 2f;

    private final static double MIN_PITCH = 0.05;
    private final static double MIN_ROLL = 0.05;

    private float[] mGyroscopeData;
    private float[] mAccelerometerData;
    private float[] mMagnetometerData;
    private Context context;
    private BoardActivityListener boardActivityListener;

    public GyroscopeMovement(Context context, float[] mGyroscopeData, float[] mAccelerometerData, float[] mMagnetometerData) {
        this.context = context;
        this.boardActivityListener = (BoardActivityListener) context;
        this.mGyroscopeData = mGyroscopeData;
        this.mAccelerometerData = mAccelerometerData;
        this.mMagnetometerData = mMagnetometerData;
    }

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
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (GyroscopeMovement.lock.tryLock()) {
            try {
                float[] orientationValues = getOrientationValues();

                float azimuth = orientationValues[0];
                float pitch = orientationValues[1];
                float roll = orientationValues[2];

                if (this.mGyroscopeData[0] > MIN_GYRO_VALUE_VERTICAL && pitch < MIN_PITCH) {
                    boardActivityListener.callback(context.getString(R.string.MoveDown));
                } else if (this.mGyroscopeData[0] < -MIN_GYRO_VALUE_VERTICAL && pitch > MIN_PITCH) {
                    boardActivityListener.callback(context.getString(R.string.MoveUP));
                } else if (this.mGyroscopeData[1] > MIN_GYRO_VALUE_HORIZONTAL && roll > MIN_ROLL) {
                    boardActivityListener.callback(context.getString(R.string.MoveRight));
                } else if (this.mGyroscopeData[1] < -MIN_GYRO_VALUE_HORIZONTAL && roll < MIN_ROLL) {
                    boardActivityListener.callback(context.getString(R.string.MoveLeft));
                }
            } finally {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    GyroscopeMovement.lock.unlock();
                }
            }
        }
    }
}
