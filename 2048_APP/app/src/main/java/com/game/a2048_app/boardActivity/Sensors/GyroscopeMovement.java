package com.game.a2048_app.boardActivity.Sensors;

import android.content.Context;
import android.hardware.SensorManager;


import com.game.a2048_app.R;
import com.game.a2048_app.boardActivity.OurCustomListenerFIXMERenameME;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GyroscopeMovement implements Runnable {

    private static Lock lock = new ReentrantLock();

    private final static float MIN_GYRO_VALUE_HORIZONTAL = 3f;
    private final static float MIN_GYRO_VALUE_VERTICAL = 2f;

    private final static float RESET_GYRO_VALUE = 0.3f;

    private final static float DETECT_MOVE_PITCH = 0.3f;
    private final static float DETECT_MOVE_ROLL = 0.3f;

    private float[] mGyroscopeData;
    private float[] mAccelerometerData;
    private float[] mMagnetometerData;
    private Context context;
    private OurCustomListenerFIXMERenameME ourCustomListenerFIXMERenameME;
    private static boolean hasMoved = true;

    private final static float RESET_PITCH = 0.2f;
    private final static float RESET_ROLL = 0.2f;

    public GyroscopeMovement(Context context, float[] mGyroscopeData, float[] mAccelerometerData, float[] mMagnetometerData) {
        this.context = context;
        this.ourCustomListenerFIXMERenameME = (OurCustomListenerFIXMERenameME) context;
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

                if (!hasMoved) {
                    if (this.mGyroscopeData[0] > MIN_GYRO_VALUE_VERTICAL && pitch < 0) {
                        ourCustomListenerFIXMERenameME.callback(context.getString(R.string.MoveDown));
                        hasMoved = true;
                    } else if (this.mGyroscopeData[0] < -MIN_GYRO_VALUE_VERTICAL && pitch > 0) {
                        ourCustomListenerFIXMERenameME.callback(context.getString(R.string.MoveUP));
                        hasMoved = true;
                    } else if (this.mGyroscopeData[1] > MIN_GYRO_VALUE_HORIZONTAL && roll > 0) {
                        ourCustomListenerFIXMERenameME.callback(context.getString(R.string.MoveRight));
                        hasMoved = true;
                    } else if (this.mGyroscopeData[1] < -MIN_GYRO_VALUE_HORIZONTAL && roll < 0) {
                        ourCustomListenerFIXMERenameME.callback(context.getString(R.string.MoveLeft));
                        hasMoved = true;
                    }
                }
                if (Math.abs(this.mGyroscopeData[0]) < RESET_GYRO_VALUE && Math.abs(this.mGyroscopeData[1]) < RESET_GYRO_VALUE) {
                    hasMoved = false;
                }
            }
            finally {
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
