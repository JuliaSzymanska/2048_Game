package com.game.a2048_app.boardActivity.Sensors;

import android.content.Context;
import android.hardware.SensorManager;

import com.game.a2048_app.R;
import com.game.a2048_app.boardActivity.OurCustomListenerFIXMERenameME;

public class PositionGyroscope implements Runnable {

    public PositionGyroscope(Context context, float[] mAccelerometerData, float[] mMagnetometerData, boolean hasMoved) {
        this.context = context;
        this.ourCustomListenerFIXMERenameME = (OurCustomListenerFIXMERenameME) context;
        this.mAccelerometerData = mAccelerometerData;
        this.mMagnetometerData = mMagnetometerData;
        this.hasMoved = hasMoved;
    }

    private final static float DETECT_MOVE_PITCH = 0.7f;
    private final static float DETECT_MOVE_ROLL = 0.7f;

    private final static float RESET_PITCH = 0.2f;
    private final static float RESET_ROLL = 0.2f;

    private static final float VALUE_DRIFT = 0.05f;

    private OurCustomListenerFIXMERenameME ourCustomListenerFIXMERenameME;
    private Context context;

    private float[] mAccelerometerData;
    private float[] mMagnetometerData;
    private boolean hasMoved;

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
        // Azimuth: The direction (north/south/east/west) the device is pointing. 0 is magnetic north.
        // Pitch: The top-to-bottom tilt of the device. 0 is flat.
        // Roll: The left-to-right tilt of the device. 0 is flat.
        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        // malutkie odchylenie -> zmiana na 0
        // nam to raczej nie potrzebne
        if (Math.abs(pitch) < VALUE_DRIFT) {
            pitch = 0;
        }
        if (Math.abs(roll) < VALUE_DRIFT) {
            roll = 0;
        }


        if (!hasMoved && (Math.abs(pitch) >= DETECT_MOVE_PITCH || Math.abs(roll) >= DETECT_MOVE_ROLL)) {
            if (pitch >= DETECT_MOVE_PITCH) {
                ourCustomListenerFIXMERenameME.callback(context.getString(R.string.MoveUP));
            } else if (pitch <= -DETECT_MOVE_PITCH) {
                ourCustomListenerFIXMERenameME.callback(context.getString(R.string.MoveDown));
            } else if (roll >= DETECT_MOVE_ROLL) {
                ourCustomListenerFIXMERenameME.callback(context.getString(R.string.MoveRight));
            } else if (roll <= -DETECT_MOVE_ROLL) {
                ourCustomListenerFIXMERenameME.callback(context.getString(R.string.MoveLeft));
            }
            ourCustomListenerFIXMERenameME.callback(context.getString(R.string.HasMovedTrue));
        }
        if (Math.abs(pitch) < RESET_PITCH && Math.abs(roll) < RESET_ROLL) {
            ourCustomListenerFIXMERenameME.callback(context.getString(R.string.HasMovedFalse));
        }
    }
}