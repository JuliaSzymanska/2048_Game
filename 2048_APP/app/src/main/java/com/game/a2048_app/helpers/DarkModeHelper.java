package com.game.a2048_app.helpers;

import android.widget.ImageView;



public class DarkModeHelper {

    /**
     * Set dark or light theme.
     */
    public static void setTheme(ImageView darkThemeView) {
        if (PreferencesHelper.getInstance().getDarkTheme()) {
            darkThemeView.setImageDrawable(Preloader.getInstance().getDarkThemeOn());
        } else {
            darkThemeView.setImageDrawable(null);
        }
    }
}
