package com.game.a2048_app.boardActivity.Sensors;

import android.content.Context;

import com.game.a2048_app.R;
import com.game.a2048_app.boardActivity.OurCustomListenerFIXMERenameME;
import com.game.module.Game;

/**
 * Pauses or unpauses game depending on proximity level.
 */
public class StopGameProximity implements Runnable {

    private final static int PROXIMITY_DISTANCE = 5;

    public StopGameProximity(Context context, float mProximityData, Game game) {
        this.mProximityData = mProximityData;
        this.context = context;
        this.game = game;
        this.ourCustomListenerFIXMERenameME = (OurCustomListenerFIXMERenameME) context;
    }

    private float mProximityData;
    private Game game;
    private Context context;
    private OurCustomListenerFIXMERenameME ourCustomListenerFIXMERenameME;

    @Override
    public void run() {
        // Proximity sensor - zatrzymuje sie czas po zblizeniu
        if (mProximityData < PROXIMITY_DISTANCE) {
            if (!game.isSuspended()) {
                ourCustomListenerFIXMERenameME.callback(context.getString(R.string.SetPauseOn));
            }
        } else if (game.isSuspended()) {
            ourCustomListenerFIXMERenameME.callback(context.getString(R.string.SetPauseOff));
        }
    }
}