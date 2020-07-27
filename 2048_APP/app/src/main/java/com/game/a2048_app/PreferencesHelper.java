package com.game.a2048_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

// TODO: 27.07.2020 FINISH ME

public class PreferencesHelper {

    private static final PreferencesHelper INSTANCE = new PreferencesHelper();
    private static Context context;
    private static SharedPreferences sharedPreferencesSettings;
    private static SharedPreferences.Editor sharedPreferencesEditor;

    private PreferencesHelper() {
    }

    public static PreferencesHelper getInstance() {
        return INSTANCE;
    }

    @SuppressLint("CommitPrefEdits")
    public static void initContext(Context context) {
        if (PreferencesHelper.context == null) {
            PreferencesHelper.context = context.getApplicationContext();
            PreferencesHelper.sharedPreferencesSettings
                    = PreferencesHelper.context.getSharedPreferences
                    (PreferencesHelper.context.getResources().getString(R.string.settings), Context.MODE_PRIVATE);
            sharedPreferencesEditor = sharedPreferencesSettings.edit();
        }
    }

    public boolean getDarkTheme() {
        if (PreferencesHelper.context == null) {
            throw new NullPointerException("Null context. ");
        }
        return PreferencesHelper.sharedPreferencesSettings.
                getBoolean(PreferencesHelper.context.getResources().getString(R.string.dark_theme), false);
    }

    public int getVolume() {
        if (PreferencesHelper.context == null) {
            throw new NullPointerException("Null context. ");
        }
        return PreferencesHelper.sharedPreferencesSettings.
                getBoolean(PreferencesHelper.context.getResources().getString(R.string.volume), true) ? 1 : 0;

    }

    public void getChoosenSensors(boolean[] choosenSensors) {
        if (PreferencesHelper.context == null) {
            throw new NullPointerException("Null context. ");
        }
        String[] sensorNames = PreferencesHelper.context.getResources().getStringArray(R.array.sensors);
        for (int i = 0; i < choosenSensors.length; i++) {
            choosenSensors[i] = PreferencesHelper.sharedPreferencesSettings.getBoolean(sensorNames[i], false);
        }
    }

    public void setDarkTheme(boolean isDarkTheme) {
        if (PreferencesHelper.context == null) {
            throw new NullPointerException("Null context. ");
        }
        sharedPreferencesEditor.putBoolean(PreferencesHelper.context.getResources().getString(R.string.dark_theme), isDarkTheme);
        sharedPreferencesEditor.apply();
    }

    public void setVolume(int volume) {
        if (PreferencesHelper.context == null) {
            throw new NullPointerException("Null context. ");
        }
        sharedPreferencesEditor.putBoolean(PreferencesHelper.context.getResources().getString(R.string.volume), (volume == 1));
        sharedPreferencesEditor.apply();
    }

    public void setChoosenSensors(boolean[] choosenSensors) {
        if (PreferencesHelper.context == null) {
            throw new NullPointerException("Null context. ");
        }
        String[] sensorNames = PreferencesHelper.context.getResources().getStringArray(R.array.sensors);
        for (int i = 0; i < choosenSensors.length; i++) {
            sharedPreferencesEditor.putBoolean(sensorNames[i], choosenSensors[i]);
        }
        sharedPreferencesEditor.apply();
    }


}
