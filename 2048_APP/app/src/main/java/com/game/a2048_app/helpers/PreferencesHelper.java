package com.game.a2048_app.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.game.a2048_app.R;

// TODO: 27.07.2020 FINISH ME

public class PreferencesHelper {

    private static final PreferencesHelper INSTANCE = new PreferencesHelper();
    private static Context context;
    private static SharedPreferences sharedPreferencesSettings;
    private static SharedPreferences.Editor sharedPreferencesEditor;

    /**
     * Default class constructor.
     */
    private PreferencesHelper() {
    }

    /**
     * @return instance of class.
     */
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

    /**
     * Loads if dark theme is on/off and return it.
     * @return if dark theme is on/off.
     */
    public boolean getDarkTheme() {
        if (PreferencesHelper.context == null) {
            throw new NullPointerException("Null context. ");
        }
        return PreferencesHelper.sharedPreferencesSettings.
                getBoolean(PreferencesHelper.context.getResources().getString(R.string.dark_theme), false);
    }

    /**
     * @return volume loaded from shared preferences settings.
     */
    public int getVolume() {
        if (PreferencesHelper.context == null) {
            throw new NullPointerException("Null context. ");
        }
        return PreferencesHelper.sharedPreferencesSettings.
                getBoolean(PreferencesHelper.context.getResources().getString(R.string.volume), true) ? 1 : 0;

    }

    /**
     * @return choosen sensors by user loaded from shared preferences settings.
     */
    public void getChoosenSensors(boolean[] choosenSensors) {
        if (PreferencesHelper.context == null) {
            throw new NullPointerException("Null context. ");
        }
        String[] sensorNames = PreferencesHelper.context.getResources().getStringArray(R.array.sensors);
        for (int i = 0; i < choosenSensors.length; i++) {
            choosenSensors[i] = PreferencesHelper.sharedPreferencesSettings.getBoolean(sensorNames[i], false);
        }
    }

    /**
     * Sets setting about dark theme to shared preferences settings.
     * @param isDarkTheme if dark theme is on/off.
     */
    public void setDarkTheme(boolean isDarkTheme) {
        if (PreferencesHelper.context == null) {
            throw new NullPointerException("Null context. ");
        }
        sharedPreferencesEditor.putBoolean(PreferencesHelper.context.getResources().getString(R.string.dark_theme), isDarkTheme);
        sharedPreferencesEditor.apply();
    }

    /**
     * Sets volume to shared preferences settings.
     * @param volume value of application volume.
     */
    public void setVolume(int volume) {
        if (PreferencesHelper.context == null) {
            throw new NullPointerException("Null context. ");
        }
        sharedPreferencesEditor.putBoolean(PreferencesHelper.context.getResources().getString(R.string.volume), (volume == 1));
        sharedPreferencesEditor.apply();
    }

    /**
     * Sets choosen sensors to shared preferences settings.
     * @param choosenSensors user's choosen sensors.
     */
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
