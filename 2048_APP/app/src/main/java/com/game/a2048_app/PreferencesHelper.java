package com.game.a2048_app;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    private static final PreferencesHelper INSTANCE = new PreferencesHelper();

    private static Context context;

    private static SharedPreferences sharedPreferencesSettings;

    private PreferencesHelper() {
    }

    public static PreferencesHelper getInstance() {
        return INSTANCE;
    }

    public static void initContext(Context context) {
        if (PreferencesHelper.context == null) {
            PreferencesHelper.context = context.getApplicationContext();
            PreferencesHelper.sharedPreferencesSettings
                    = PreferencesHelper.context.getSharedPreferences
                    (PreferencesHelper.context.getResources().getString(R.string.settings), Context.MODE_PRIVATE);
        }
    }

    public boolean getDarkTheme(){
        if (PreferencesHelper.context == null) {
            throw new NullPointerException(" no xd byczq");
        }
        return PreferencesHelper.sharedPreferencesSettings.
                getBoolean(PreferencesHelper.context.getResources().getString(R.string.dark_theme), false);
    }

}
