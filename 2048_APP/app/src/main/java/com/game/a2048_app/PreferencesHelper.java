package com.game.a2048_app;

import android.content.SharedPreferences;

public class PreferencesHelper {

    private static final PreferencesHelper INSTANCE = new PreferencesHelper();

    private PreferencesHelper() {
    }

    public static PreferencesHelper getInstance() {
        return INSTANCE;
    }

    public boolean getDarkTheme(SharedPreferences preferences){
//        return preferences.getBoolean(getResources().getString(R.string.dark_theme), false);
        return true;
    }

}
