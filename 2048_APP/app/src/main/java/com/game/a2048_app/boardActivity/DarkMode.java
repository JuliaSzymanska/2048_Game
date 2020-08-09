package com.game.a2048_app.boardActivity;

import android.content.Context;

import com.game.a2048_app.R;
import com.game.a2048_app.helpers.PreferencesHelper;

/**
 * Sets dark or light mode depending on the light level.
 */
class DarkMode implements Runnable {

    DarkMode(float mLightData, Context context) {
        this.mLightData = mLightData;
        this.context = context;
        this.ourCustomListenerFIXMERenameME = (OurCustomListenerFIXMERenameME) context;
    }
    private Context context;

    private PreferencesHelper preferencesHelper = PreferencesHelper.getInstance();
    OurCustomListenerFIXMERenameME ourCustomListenerFIXMERenameME;
    private float mLightData;

    private final static int DARKMODE_ENABLE_LIGHT = 30;
    private final static int DARKMODE_DISABLE_LIGHT = 50;

    @Override
    public void run() {
        // Light Sensor - gdy jest ciemno włącza się dark mode
        if (mLightData <= DARKMODE_ENABLE_LIGHT && !preferencesHelper.getDarkTheme()) {
            preferencesHelper.setDarkTheme(true);
            ourCustomListenerFIXMERenameME.callback(context.getString(R.string.SetTheme));

        } else if (mLightData >= DARKMODE_DISABLE_LIGHT && preferencesHelper.getDarkTheme()) {
            preferencesHelper.setDarkTheme(false);
            ourCustomListenerFIXMERenameME.callback(context.getString(R.string.SetTheme));
        }
    }
}