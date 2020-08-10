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
    private static Drawable two;
    private static Drawable four;
    private static Drawable eight;
    private static Drawable sixteen;
    private static Drawable thirtyTwo;
    private static Drawable sixtyFour;
    private static Drawable oneHundred;
    private static Drawable twoHundred;
    private static Drawable fiveHundred;
    private static Drawable oneThousand;
    private static Drawable twoThousand;
    private static Drawable fourThousand;
    private static Drawable eightThousand;
    private static Drawable sixteenThousand;
    private static Drawable thirtyTwoThousand;
    private static Drawable sixtyFiveThousand;
    private static Drawable oneHundredThousand;

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

        loadPausePlayOn();
        loadPausePlayOff();

        loadMuteOn();
        loadMuteOff();

        loadDarkThemeOn();

        loadSettings();
        loadSettingsClicked();

        loadMainButton();
        loadMainButtonClicked();

        loadTwo();
        loadFour();
        loadEight();
        loadSixteen();
        loadThirtyTwo();
        loadSixtyFour();
        loadOneHundred();
        loadTwoHundred();
        loadFiveHundred();
        loadOneThousand();
        loadTwoThousand();
        loadFourThousand();
        loadEightThousand();
        loadSixteenThousand();
        loadThirtyTwoThousand();
        loadSixtyFiveThousand();
        loadOneHundredThousand();

        loadButtonGreen();
        loadButtonGreenLight();
        loadButtonBlue();
        loadButtonBlueLight();
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
     * @return field's image for the value 0 (null).
     */
    public Drawable getZero() {
        return null;
    }


    /**
     * @return field's image for 2 value ({@link Preloader#two}).
     */
    public Drawable getTwo() {
        if (two == null)
            loadTwo();
        return two;
    }

    private void loadTwo() {
        two = context.getDrawable(R.drawable.number_two);
    }

    /**
     * @return field's image for 4 value ({@link Preloader#four}).
     */
    public Drawable getFour() {
        if (four == null)
            loadFour();
        return four;
    }

    private void loadFour() {
        four = context.getDrawable(R.drawable.number_four);
    }

    /**
     * @return field's image for 8 value ({@link Preloader#eight}).
     */
    public Drawable getEight() {
        if (eight == null)
            loadEight();
        return eight;
    }

    private void loadEight() {
        eight = context.getDrawable(R.drawable.number_eight);
    }

    /**
     * @return field's image for 16 value ({@link Preloader#sixteen}).
     */
    public Drawable getSixteen() {
        if (sixteen == null)
            loadSixteen();
        return sixteen;
    }

    private void loadSixteen() {
        sixteen = context.getDrawable(R.drawable.number_sixteen);
    }

    /**
     * @return field's image for 32 value ({@link Preloader#thirtyTwo}).
     */
    public Drawable getThirtyTwo() {
        if (thirtyTwo == null)
            loadThirtyTwo();
        return thirtyTwo;
    }

    private void loadThirtyTwo() {
        thirtyTwo = context.getDrawable(R.drawable.number_thirty_two);
    }

    /**
     * @return field's image for 64 value ({@link Preloader#sixtyFour}).
     */
    public Drawable getSixtyFour() {
        if (sixtyFour == null)
            loadSixtyFour();
        return sixtyFour;
    }

    private void loadSixtyFour() {
        sixtyFour = context.getDrawable(R.drawable.number_sixty_four);
    }

    /**
     * @return field's image for 128 value ({@link Preloader#oneHundred}).
     */
    public Drawable getOneHundred() {
        if (oneHundred == null)
            loadOneHundred();
        return oneHundred;
    }

    private void loadOneHundred() {
        oneHundred = context.getDrawable(R.drawable.number_one_hundred);
    }

    /**
     * @return field's image for 256 value ({@link Preloader#twoHundred}).
     */
    public Drawable getTwoHundred() {
        if (twoHundred == null)
            loadTwoHundred();
        return twoHundred;
    }

    private void loadTwoHundred() {
        twoHundred = context.getDrawable(R.drawable.number_two_hundred);
    }

    /**
     * @return field's image for 512 value ({@link Preloader#fiveHundred}).
     */
    public Drawable getFiveHundred() {
        if (fiveHundred == null)
            loadFiveHundred();
        return fiveHundred;
    }

    private void loadFiveHundred() {
        fiveHundred = context.getDrawable(R.drawable.number_five_hundred);
    }

    /**
     * @return field's image for 1 024 value({@link Preloader#oneThousand}).
     */
    public Drawable getOneThousand() {
        if (oneThousand == null)
            loadOneThousand();
        return oneThousand;
    }

    private void loadOneThousand() {
        oneThousand = context.getDrawable(R.drawable.number_one_thousand);
    }

    /**
     * @return field's image for 2 048 value ({@link Preloader#twoThousand}).
     */
    public Drawable getTwoThousand() {
        if (twoThousand == null)
            loadTwoThousand();
        return twoThousand;
    }

    private void loadTwoThousand() {
        twoThousand = context.getDrawable(R.drawable.number_two_thousand);
    }

    /**
     * @return field's image for 4 096 value ({@link Preloader#fourThousand}).
     */
    public Drawable getFourThousand() {
        if (fourThousand == null)
            loadFourThousand();
        return fourThousand;
    }

    private void loadFourThousand() {
        fourThousand = context.getDrawable(R.drawable.number_four_thousand);
    }

    /**
     * @return field's image for 8 192 value ({@link Preloader#eightThousand}).
     */
    public Drawable getEightThousand() {
        if (eightThousand == null)
            loadEightThousand();
        return eightThousand;
    }

    private void loadEightThousand() {
        eightThousand = context.getDrawable(R.drawable.number_eight_thousand);
    }

    /**
     * @return field's image for 16 384 value ({@link Preloader#sixteenThousand}).
     */
    public Drawable getSixteenThousand() {
        if (sixteenThousand == null)
            loadSixteenThousand();
        return sixteenThousand;
    }

    private void loadSixteenThousand() {
        sixteenThousand = context.getDrawable(R.drawable.number_sixteen_thousand);
    }

    /**
     * @return field's image for 32 768 value ({@link Preloader#thirtyTwoThousand}).
     */
    public Drawable getThirtyTwoThousand() {
        if (thirtyTwoThousand == null)
            loadThirtyTwoThousand();
        return thirtyTwoThousand;
    }

    private void loadThirtyTwoThousand() {
        thirtyTwoThousand = context.getDrawable(R.drawable.number_thirty_two_thousand);
    }

    /**
     * @return field's image for 65 536 value ({@link Preloader#sixtyFiveThousand}).
     */
    public Drawable getSixtyFiveThousand() {
        if (sixtyFiveThousand == null)
            loadSixtyFiveThousand();
        return sixtyFiveThousand;
    }

    private void loadSixtyFiveThousand() {
        sixtyFiveThousand = context.getDrawable(R.drawable.number_sixty_five_thousand);
    }

    /**
     * @return field's image for 131 072 value ({@link Preloader#oneHundredThousand}).
     */
    public Drawable getOneHundredThousand() {
        if (oneHundredThousand == null)
            loadOneHundredThousand();
        return oneHundredThousand;
    }

    private void loadOneHundredThousand() {
        oneHundredThousand = context.getDrawable(R.drawable.number_one_hundred_thousand);
    }

    /**
     * @return green field's background image ({@link Preloader#buttonGreen}).
     */
    public Drawable getButtonGreen() {
        if (buttonGreen == null)
            loadButtonGreen();
        return buttonGreen;
    }

    private void loadButtonGreen() {
        buttonGreen = context.getDrawable(R.drawable.button_green);
    }

    /**
     * @return light green field's background image ({@link Preloader#buttonGreenLight}).
     */
    public Drawable getButtonGreenLight() {
        if (buttonGreenLight == null)
            loadButtonGreenLight();
        return buttonGreenLight;
    }

    private void loadButtonGreenLight() {
        buttonGreenLight = context.getDrawable(R.drawable.button_green_light);
    }

    /**
     * @return blue field's background image ({@link Preloader#buttonBlue}).
     */
    public Drawable getButtonBlue() {
        if (buttonBlue == null)
            loadButtonBlue();
        return buttonBlue;
    }

    private void loadButtonBlue() {
        buttonBlue = context.getDrawable(R.drawable.button_blue);
    }

    /**
     * @return light blue field's background image ({@link Preloader#buttonBlueLight}).
     */
    public Drawable getButtonBlueLight() {
        if (buttonBlueLight == null)
            loadButtonBlueLight();
        return buttonBlueLight;
    }

    private void loadButtonBlueLight() {
        buttonBlueLight = context.getDrawable(R.drawable.button_green_light);
    }
}
