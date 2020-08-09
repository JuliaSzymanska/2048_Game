package com.game.a2048_app.boardActivity;

import com.game.module.Game;

/**
 * Pauses or unpauses game depending on proximity level.
 */
class StopGameProximity implements Runnable {

    private final static int PROXIMITY_DISTANCE = 5;

    StopGameProximity(float mProximityData, Game game) {
        this.mProximityData = mProximityData;
        this.game = game;
    }
    private float mProximityData;
    private Game game;

    @Override
    public void run() {
        // Proximity sensor - zatrzymuje sie czas po zblizeniu
        if (mProximityData < PROXIMITY_DISTANCE) {
            if (!game.isSuspended()) {
                game.pauseTimer();
            }
        } else if (game.isSuspended()) {
            game.unpauseTimer();
        }
    }
}