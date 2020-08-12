package com.game.a2048_app.boardActivity.Sensors;

import android.app.Activity;
import android.content.Context;
import android.widget.Button;

import com.game.a2048_app.R;
import com.game.a2048_app.helpers.Preloader;
import com.game.module.Game;

/**
 * Pauses or unpauses game depending on proximity level.
 */
public class StopGameProximity implements Runnable {

    private final static int PROXIMITY_DISTANCE = 5;

    public StopGameProximity(Context context, float mProximityData, Game game) {
        this.mProximityData = mProximityData;
        this.game = game;
        this.context = context;
        this.pausePlay = (Button) ((Activity) context).findViewById(R.id.pausePlayButton);
    }

    private float mProximityData;
    private Game game;
    private Button pausePlay;
    private Context context;
    private Preloader preloader = Preloader.getInstance();

    @Override
    public void run() {
        // Proximity sensor - zatrzymuje sie czas po zblizeniu
        if (mProximityData < PROXIMITY_DISTANCE) {
            if (!game.isSuspended()) {
                game.pauseTimer();
                ((Activity) this.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pausePlay.setBackground(preloader.getPausePlayOn());
                    }
                });
            }
        } else if (game.isSuspended()) {
            game.unPauseTimer();
            ((Activity) this.context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pausePlay.setBackground(preloader.getPausePlayOff());
                }
            });
        }
    }
}