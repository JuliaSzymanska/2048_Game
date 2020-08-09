package com.game.a2048_app.boardActivity.Sensors;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.game.a2048_app.R;
import com.game.a2048_app.boardActivity.OurCustomListenerFIXMERenameME;

/**
 * Changes fields background colour depending on magnetometer value.
 */
public class ChangeColourMagnetometer implements Runnable {

    public ChangeColourMagnetometer(Context context, float[]  mAccelerometerData, float[] mMagnetometerData) {
        this.context = context;
        this.ourCustomListenerFIXMERenameME = (OurCustomListenerFIXMERenameME) context;
        this.mAccelerometerData = mAccelerometerData;
        this.mMagnetometerData = mMagnetometerData;
    }

    private OurCustomListenerFIXMERenameME ourCustomListenerFIXMERenameME;
    private Context context;

    private float[] mAccelerometerData;
    private float[] mMagnetometerData;

    private final static double HORIZONTAL_PITCH_MAX = 0.5;
    private final static double HORIZONTAL_PITCH_MIN = -0.5;

    private final static double changeColourAzimuthBreakpoint1 = 0.25;
    private final static double changeColourAzimuthBreakpoint2 = 1.25;
    private final static double changeColourAzimuthBreakpoint3 = 1.75;
    private final static double changeColourAzimuthBreakpoint4 = 2.75;


    private float[] magnetometerSetup() {
        float[] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix,
                null, mAccelerometerData, mMagnetometerData);
        float[] orientationValues = new float[3];
        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrix, orientationValues);
        }
        return orientationValues;
    }

    @Override
    public void run() {
        float[] orientationValues = magnetometerSetup();
        final float azimuth = orientationValues[0];
        final float pitch = orientationValues[1];
        if (pitch > HORIZONTAL_PITCH_MIN && pitch < HORIZONTAL_PITCH_MAX) {
            if (azimuth >= changeColourAzimuthBreakpoint1 && azimuth < changeColourAzimuthBreakpoint2) {
                ourCustomListenerFIXMERenameME.callback(context.getString(R.string.Button_Green));
            } else if (azimuth >= changeColourAzimuthBreakpoint3 && azimuth < changeColourAzimuthBreakpoint4) {
                ourCustomListenerFIXMERenameME.callback(context.getString(R.string.Button_Green_Light));
            } else if (azimuth >= -changeColourAzimuthBreakpoint4 && azimuth < -changeColourAzimuthBreakpoint3) {
                ourCustomListenerFIXMERenameME.callback(context.getString(R.string.Button_Blue));
            } else if (azimuth > -changeColourAzimuthBreakpoint2 && azimuth < -changeColourAzimuthBreakpoint1) {
                ourCustomListenerFIXMERenameME.callback(context.getString(R.string.Button_Blue_Light));
            } else {
                return;
            }
            ourCustomListenerFIXMERenameME.callback(context.getString(R.string.Notify_Adapter));
        }
    }
}
