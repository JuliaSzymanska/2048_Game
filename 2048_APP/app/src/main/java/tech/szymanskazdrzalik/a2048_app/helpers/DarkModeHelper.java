package tech.szymanskazdrzalik.a2048_app.helpers;

import android.widget.ImageView;


public class DarkModeHelper {

    /**
     * Set dark or light theme. Intended for use in Activities that implement darkMode using the DarkThemeView.
     *
     * @param darkThemeView View to be set to either to null or to {@link Preloader#getDarkThemeOn()}.
     */
    public static void setTheme(ImageView darkThemeView) {
        if (PreferencesHelper.getInstance().getDarkTheme()) {
            darkThemeView.setImageDrawable(Preloader.getInstance().getDarkThemeOn());
        } else {
            darkThemeView.setImageDrawable(null);
        }
    }
}
