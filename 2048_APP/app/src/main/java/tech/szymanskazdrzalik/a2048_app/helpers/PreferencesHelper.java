package tech.szymanskazdrzalik.a2048_app.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import tech.szymanskazdrzalik.a2048_app.R;

public class PreferencesHelper {

    private static final PreferencesHelper INSTANCE = new PreferencesHelper();
    private static Context context;
    private static SharedPreferences sharedPreferencesSettings;
    private static SharedPreferences.Editor sharedPreferencesEditor;

    /**
     * Empty default class constructor.
     */
    private PreferencesHelper() {
    }

    /**
     * @return instance of class {@link PreferencesHelper#INSTANCE}.
     */
    public static PreferencesHelper getInstance() {
        return INSTANCE;
    }

    /**
     * Initialized context for class.
     *
     * @param context context passed by activity.
     *                Assigns {@link PreferencesHelper#sharedPreferencesSettings} instance of {@link SharedPreferences} class for accessing and modifying preference data.
     *                Assigns  {@link PreferencesHelper#sharedPreferencesEditor} a new instance of the {@link SharedPreferences.Editor} interface.
     */
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
     *
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
     * Sets setting about dark theme to shared preferences settings.
     *
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
     * Sets volume to shared preferences settings.
     *
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
     * @param chosenSensors chosen sensors by user loaded from shared preferences settings.
     */
    public void getChosenSensors(boolean[] chosenSensors) {
        if (PreferencesHelper.context == null) {
            throw new NullPointerException("Null context. ");
        }
        String[] sensorNames = PreferencesHelper.context.getResources().getStringArray(R.array.sensors);
        for (int i = 0; i < chosenSensors.length; i++) {
            chosenSensors[i] = PreferencesHelper.sharedPreferencesSettings.getBoolean(sensorNames[i], false);
        }
    }

    /**
     * Sets chosen sensors to shared preferences settings.
     *
     * @param chosenSensors user's chosen sensors.
     */
    public void setChosenSensors(boolean[] chosenSensors) {
        if (PreferencesHelper.context == null) {
            throw new NullPointerException("Null context.");
        }
        String[] sensorNames = PreferencesHelper.context.getResources().getStringArray(R.array.sensors);
        for (int i = 0; i < chosenSensors.length; i++) {
            sharedPreferencesEditor.putBoolean(sensorNames[i], chosenSensors[i]);
        }
        sharedPreferencesEditor.apply();
    }


}
