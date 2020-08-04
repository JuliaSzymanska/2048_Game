package com.game.a2048_app.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.game.a2048_app.R;

public class Preloader {

    private static Preloader instance = new Preloader();
    private static Context context;

    /**
     * Empty default class constructor.
     */
    private Preloader() {
    }

    private static Drawable buttonGreen;
    private static Drawable buttonGreenLight;
    private static Drawable buttonBlue;
    private static Drawable buttonBlueLight;
    private static Drawable mainButtonClicked;
    private static Drawable mainButton;
    private static Drawable settingsClicked;
    private static Drawable settings;
    private static Drawable undoClicked;
    private static Drawable undo;
    private static Drawable pausePlayOn;
    private static Drawable pausePlayOff;
    private static Drawable muteOn;
    private static Drawable muteOff;
    private static Drawable darkThemeOn;
    private static Drawable darkThemeOff;
    private static Drawable zero;
    private static Drawable two;
    private static Drawable four;
    private static Drawable eight;
    private static Drawable sixteen;
    private static Drawable thirtyTwo;
    private static Drawable sixtyFour;
    private static Drawable oneHundred;
    private static Drawable twoHundreds;
    private static Drawable fiveHundreds;
    private static Drawable oneThousand;
    private static Drawable twoThousands;
    private static Drawable fourThousands;
    private static Drawable eightThousands;
    private static Drawable sixteenThousands;
    private static Drawable thirtyTwoThousands;
    private static Drawable sixtyFiveThousands;
    private static Drawable oneHundredThousands;

    /**
     * Initialized context for class.
     *
     * @param context context passed by activity.
     */
    @SuppressLint("CommitPrefEdits")
    public static void initContext(Context context) {
        if (Preloader.context == null) {
            Preloader.context = context.getApplicationContext();
        }
    }

    /**
     * Loads Drawable images from R.drawable.
     */
    // TODO: 04.08.2020 ustawic alfabetycznie
    public void loadAssets() {
        loadUndoClicked();
        loadUndo();
        loadButtonGreen();
        loadPausePlayOn();
        loadPausePlayOff();
        loadMuteOn();
        loadMuteOff();
        loadDarkThemeOn();
        loadDarkThemeOff();
        loadSettings();
        loadSettingsClicked();
        loadMainButton();
        loadMainButtonClicked();
        buttonGreenLight = context.getDrawable(R.drawable.button_green_light);
        buttonBlue = context.getDrawable(R.drawable.button_blue);
        buttonBlueLight = context.getDrawable(R.drawable.button_blue_light);
        zero = context.getDrawable(R.drawable.zero);
        two = context.getDrawable(R.drawable.two);
        four = context.getDrawable(R.drawable.four);
        eight = context.getDrawable(R.drawable.eight);
        sixteen = context.getDrawable(R.drawable.sixteen);
        thirtyTwo = context.getDrawable(R.drawable.thirty_two);
        sixtyFour = context.getDrawable(R.drawable.sixty_four);
        oneHundred = context.getDrawable(R.drawable.one_hundred);
        twoHundreds = context.getDrawable(R.drawable.two_hundreds);
        fiveHundreds = context.getDrawable(R.drawable.five_hundreds);
        oneThousand = context.getDrawable(R.drawable.one_thousand);
        twoThousands = context.getDrawable(R.drawable.two_thousands);
        fourThousands = context.getDrawable(R.drawable.four_thousands);
        eightThousands = context.getDrawable(R.drawable.eight_thousands);
        sixteenThousands = context.getDrawable(R.drawable.sixteen_thousands);
        thirtyTwoThousands = context.getDrawable(R.drawable.thirty_two_thousands);
        sixtyFiveThousands = context.getDrawable(R.drawable.sixty_five_thousands);
        oneHundredThousands = context.getDrawable(R.drawable.one_hundred_thousands);
    }

    /**
     * @return instance of class ({@link Preloader#instance}).
     */
    public static Preloader getInstance() {
        return instance;
    }

    // TODO: 04.08.2020 ustawic alfabetycznie
    /**
     * @return clicked undo button's image ({@link Preloader#undoClicked}).
     */
    public Drawable getUndoClicked() {
        if (undoClicked == null)
            loadUndoClicked();
        return undoClicked;
    }

    private void loadUndoClicked() {
        undoClicked = context.getDrawable(R.drawable.undo_clicked);
    }

    /**
     * @return undo button's image ({@link Preloader#undo}).
     */
    public Drawable getUndo() {
        if (undo == null)
            loadUndo();
        return undo;
    }

    private void loadUndo() {
        undo = context.getDrawable(R.drawable.undo);
    }


    /**
     * @return pause and play on button's image ({@link Preloader#pausePlayOn}).
     */
    public Drawable getPausePlayOn() {
        if (pausePlayOn == null)
            loadPausePlayOn();
        return pausePlayOn;
    }

    private void loadPausePlayOn() {
        pausePlayOn = context.getDrawable(R.drawable.pause_play_on);
    }

    /**
     * @return pause and play off button's image ({@link Preloader#pausePlayOff}).
     */
    public Drawable getPausePlayOff() {
        if (pausePlayOff == null)
            loadPausePlayOff();
        return pausePlayOff;
    }

    private void loadPausePlayOff() {
        pausePlayOff = context.getDrawable(R.drawable.pause_play);
    }

    /**
     * @return mute on button's image ({@link Preloader#muteOn}).
     */
    public Drawable getMuteOn() {
        if (muteOn == null)
            loadMuteOn();
        return muteOn;
    }

    private void loadMuteOn() {
        muteOn = context.getDrawable(R.drawable.mute_on);
    }

    /**
     * @return mute off button's image ({@link Preloader#muteOff}).
     */
    public Drawable getMuteOff() {
        if (muteOff == null)
            loadMuteOff();
        return muteOff;
    }

    private void loadMuteOff() {
        muteOff = context.getDrawable(R.drawable.mute_off);
    }

    /**
     * @return dark theme on image ({@link Preloader#darkThemeOn}).
     */
    public Drawable getDarkThemeOn() {
        if (darkThemeOn == null)
            loadDarkThemeOn();
        return darkThemeOn;
    }

    private void loadDarkThemeOn() {
        darkThemeOn = context.getDrawable(R.drawable.dark_theme_on);
    }

    /**
     * @return dark theme off image ({@link Preloader#darkThemeOff}).
     */
    public Drawable getDarkThemeOff() {
        if (darkThemeOff == null)
            loadDarkThemeOff();
        return darkThemeOff;
    }

    private void loadDarkThemeOff() {
        darkThemeOff = context.getDrawable(R.drawable.dark_theme_off);
    }

    /**
     * @return settings button's image ({@link Preloader#settings}).
     */
    public Drawable getSettings() {
        if (settings == null)
            loadSettings();
        return settings;
    }

    private void loadSettings() {
        settings = context.getDrawable(R.drawable.settings);
    }

    /**
     * @return settings clicked button's image ({@link Preloader#settingsClicked}).
     */
    public Drawable getSettingsClicked() {
        if (settingsClicked == null)
            loadSettingsClicked();
        return settingsClicked;
    }

    private void loadSettingsClicked() {
        settingsClicked = context.getDrawable(R.drawable.settings_clicked);
    }

    /**
     * @return main button's clicked image ({@link Preloader#mainButtonClicked}).
     */
    public Drawable getMainButtonClicked() {
        if (mainButtonClicked == null)
            loadMainButtonClicked();
        return mainButtonClicked;
    }

    private void loadMainButtonClicked() {
        mainButtonClicked = context.getDrawable(R.drawable.main_button_clicked);
    }

    /**
     * @return main button's image ({@link Preloader#mainButton}).
     */
    public Drawable getMainButton() {
        if (mainButton == null)
            loadMainButton();
        return mainButton;
    }

    private void loadMainButton() {
        mainButton = context.getDrawable(R.drawable.main_button);
    }

    /**
     * @return field's image for 0 value ({@link Preloader#zero}).
     */
    public Drawable getZero() {
        return zero;
    }

    /**
     * @return field's image for 2 value ({@link Preloader#two}).
     */
    public Drawable getTwo() {
        return two;
    }

    /**
     * @return field's image for 4 value ({@link Preloader#four}).
     */
    public Drawable getFour() {
        return four;
    }

    /**
     * @return field's image for 8 value ({@link Preloader#eight}).
     */
    public Drawable getEight() {
        return eight;
    }

    /**
     * @return field's image for 16 value ({@link Preloader#sixteen}).
     */
    public Drawable getSixteen() {
        return sixteen;
    }

    /**
     * @return field's image for 32 value ({@link Preloader#thirtyTwo}).
     */
    public Drawable getThirtyTwo() {
        return thirtyTwo;
    }

    /**
     * @return field's image for 64 value ({@link Preloader#sixtyFour}).
     */
    public Drawable getSixtyFour() {
        return sixtyFour;
    }

    /**
     * @return field's image for 128 value ({@link Preloader#oneHundred}).
     */
    public Drawable getOneHundred() {
        return oneHundred;
    }

    /**
     * @return field's image for 256 value ({@link Preloader#twoHundreds}).
     */
    public Drawable getTwoHundreds() {
        return twoHundreds;
    }

    /**
     * @return field's image for 512 value ({@link Preloader#fiveHundreds}).
     */
    public Drawable getFiveHundreds() {
        return fiveHundreds;
    }

    /**
     * @return field's image for 1 024 value({@link Preloader#oneThousand}).
     */
    public Drawable getOneThousand() {
        return oneThousand;
    }

    /**
     * @return field's image for 2 048 value ({@link Preloader#twoThousands}).
     */
    public Drawable getTwoThousands() {
        return twoThousands;
    }

    /**
     * @return field's image for 4 096 value ({@link Preloader#fourThousands}).
     */
    public Drawable getFourThousands() {
        return fourThousands;
    }

    /**
     * @return field's image for 8 192 value ({@link Preloader#eightThousands}).
     */
    public Drawable getEightThousands() {
        return eightThousands;
    }

    /**
     * @return field's image for 16 384 value ({@link Preloader#sixteenThousands}).
     */
    public Drawable getSixteenThousands() {
        return sixteenThousands;
    }

    /**
     * @return field's image for 32 768 value ({@link Preloader#thirtyTwoThousands}).
     */
    public Drawable getThirtyTwoThousands() {
        return thirtyTwoThousands;
    }

    /**
     * @return field's image for 65 536 value ({@link Preloader#sixtyFiveThousands}).
     */
    public Drawable getSixtyFiveThousands() {
        return sixtyFiveThousands;
    }

    /**
     * @return field's image for 131 072 value ({@link Preloader#oneHundredThousands}).
     */
    public Drawable getOneHundredThousands() {
        return oneHundredThousands;
    }

    /**
     * @return green field's background image ({@link Preloader#buttonGreen}).
     */
    public Drawable getButtonGreen() {
        if (buttonGreen != null)
            return buttonGreen;
        return loadButtonGreen();
    }

    private Drawable loadButtonGreen() {
        return context.getDrawable(R.drawable.button_green);
    }


    /**
     * @return light green field's background image ({@link Preloader#buttonGreenLight}).
     */
    public Drawable getButtonGreenLight() {
        return buttonGreenLight;
    }

    /**
     * @return blue field's background image ({@link Preloader#buttonBlue}).
     */
    public Drawable getButtonBlue() {
        return buttonBlue;
    }

    /**
     * @return light blue field's background image ({@link Preloader#buttonBlueLight}).
     */
    public Drawable getButtonBlueLight() {
        return buttonBlueLight;
    }

}
