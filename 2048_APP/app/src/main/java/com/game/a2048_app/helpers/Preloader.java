package com.game.a2048_app.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.game.a2048_app.R;

public class Preloader {

    private static Preloader instance = new Preloader();
    private static Context context;

    /**
     * Default class constructor.
     */
    private Preloader() {
    }

    private Drawable buttonGreeen;
    private Drawable buttonGreeenLight;
    private Drawable buttonBlue;
    private Drawable buttonBlueLight;
    private Drawable mainButtonClicked;
    private Drawable mainButton;
    private Drawable setttingsClicked;
    private Drawable setttings;
    private Drawable undoClicked;
    private Drawable undo;
    private Drawable pausePlayOn;
    private Drawable pausePlayOff;
    private Drawable muteOn;
    private Drawable muteOff;
    private Drawable darkThemeOn;
    private Drawable darkThemeOff;
    private Drawable zero;
    private Drawable two;
    private Drawable four;
    private Drawable eight;
    private Drawable sixteen;
    private Drawable thirtyTwo;
    private Drawable sixtyFour;
    private Drawable oneHundred;
    private Drawable twoHundreds;
    private Drawable fiveHundreds;
    private Drawable oneThousand;
    private Drawable twoThousands;
    private Drawable fourThousands;
    private Drawable eightThousands;
    private Drawable sixteenThousands;
    private Drawable thirtyTwoThousands;
    private Drawable sixtyFiveThousands;
    private Drawable oneHundredThousands;

    /**
     * Initialized context for class.
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
    public void loadAssets() {
        this.buttonGreeen = context.getDrawable(R.drawable.button_green);
        this.buttonGreeenLight = context.getDrawable(R.drawable.button_green_light);
        this.buttonBlue = context.getDrawable(R.drawable.button_blue);
        this.buttonBlueLight = context.getDrawable(R.drawable.button_blue_light);
        this.mainButtonClicked = context.getDrawable(R.drawable.main_button_clicked);
        this.mainButton = context.getDrawable(R.drawable.main_button);
        this.setttingsClicked = context.getDrawable(R.drawable.settings_clicked);
        this.setttings = context.getDrawable(R.drawable.settings);
        this.undoClicked = context.getDrawable(R.drawable.undo_clicked);
        this.undo = context.getDrawable(R.drawable.undo);
        this.pausePlayOn = context.getDrawable(R.drawable.pause_play_on);
        this.pausePlayOff = context.getDrawable(R.drawable.pause_play);
        this.muteOn = context.getDrawable(R.drawable.mute_on);
        this.muteOff = context.getDrawable(R.drawable.mute_off);
        this.darkThemeOn = context.getDrawable(R.drawable.dark_theme_on);
        this.darkThemeOff = context.getDrawable(R.drawable.dark_theme_off);
        this.zero = context.getDrawable(R.drawable.zero);
        this.two = context.getDrawable(R.drawable.two);
        this.four = context.getDrawable(R.drawable.four);
        this.eight = context.getDrawable(R.drawable.eight);
        this.sixteen = context.getDrawable(R.drawable.sixteen);
        this.thirtyTwo = context.getDrawable(R.drawable.thirty_two);
        this.sixtyFour = context.getDrawable(R.drawable.sixty_four);
        this.oneHundred = context.getDrawable(R.drawable.one_hundred);
        this.twoHundreds = context.getDrawable(R.drawable.two_hundreds);
        this.fiveHundreds = context.getDrawable(R.drawable.five_hundreds);
        this.oneThousand = context.getDrawable(R.drawable.one_thousand);
        this.twoThousands = context.getDrawable(R.drawable.two_thousands);
        this.fourThousands = context.getDrawable(R.drawable.four_thousands);
        this.eightThousands = context.getDrawable(R.drawable.eight_thousands);
        this.sixteenThousands = context.getDrawable(R.drawable.sixteen_thousands);
        this.thirtyTwoThousands = context.getDrawable(R.drawable.thirty_two_thousands);
        this.sixtyFiveThousands = context.getDrawable(R.drawable.sixty_five_thousands);
        this.oneHundredThousands = context.getDrawable(R.drawable.one_hundred_thousands);
    }

    /**
     * @return instance of class.
     */
    public static Preloader getInstance() {
        return instance;
    }

    /**
     * @return clicked undo button's image.
     */
    public Drawable getUndoClicked() {
        return undoClicked;
    }

    /**
     * @return undo button's image.
     */
    public Drawable getUndo() {
        return undo;
    }

    /**
     * @return pause and play on button's image.
     */
    public Drawable getPausePlayOn() {
        return pausePlayOn;
    }

    /**
     * @return pause and play off button's image.
     */
    public Drawable getPausePlayOff() {
        return pausePlayOff;
    }

    /**
     * @return mute on button's image.
     */
    public Drawable getMuteOn() {
        return muteOn;
    }

    /**
     * @return mute off button's image.
     */
    public Drawable getMuteOff() {
        return muteOff;
    }

    /**
     * @return dark theme on image.
     */
    public Drawable getDarkThemeOn() {
        return darkThemeOn;
    }

    /**
     * @return dark theme off image.
     */
    public Drawable getDarkThemeOff() {
        return darkThemeOff;
    }

    /**
     * @return settings button's image.
     */
    public Drawable getSetttings() {
        return setttings;
    }

    /**
     * @return settings clicked button's image.
     */
    public Drawable getSetttingsClicked() {
        return setttingsClicked;
    }

    /**
     * @return main button's clicked image.
     */
    public Drawable getMainButtonClicked() {
        return mainButtonClicked;
    }

    /**
     * @return main button's image.
     */
    public Drawable getMainButton() {
        return mainButton;
    }

    /**
     * @return field's image for 0 value.
     */
    public Drawable getZero() {
        return zero;
    }

    /**
     * @return field's image for 2 value.
     */
    public Drawable getTwo() {
        return two;
    }

    /**
     * @return field's image for 4 value.
     */
    public Drawable getFour() {
        return four;
    }

    /**
     * @return field's image for 8 value.
     */
    public Drawable getEight() {
        return eight;
    }

    /**
     * @return field's image for 16 value.
     */
    public Drawable getSixteen() {
        return sixteen;
    }

    /**
     * @return field's image for 32 value.
     */
    public Drawable getThirtyTwo() {
        return thirtyTwo;
    }

    /**
     * @return field's image for 64 value.
     */
    public Drawable getSixtyFour() {
        return sixtyFour;
    }

    /**
     * @return field's image for 128 value.
     */
    public Drawable getOneHundred() {
        return oneHundred;
    }

    /**
     * @return field's image for 256 value.
     */
    public Drawable getTwoHundreds() {
        return twoHundreds;
    }

    /**
     * @return field's image for 512 value.
     */
    public Drawable getFiveHundreds() {
        return fiveHundreds;
    }

    /**
     * @return field's image for 1 024 value.
     */
    public Drawable getOneThousand() {
        return oneThousand;
    }

    /**
     * @return field's image for 2 048 value.
     */
    public Drawable getTwoThousands() {
        return twoThousands;
    }

    /**
     * @return field's image for 4 096 value.
     */
    public Drawable getFourThousands() {
        return fourThousands;
    }

    /**
     * @return field's image for 8 192 value.
     */
    public Drawable getEightThousands() {
        return eightThousands;
    }

    /**
     * @return field's image for 16 384 value.
     */
    public Drawable getSixteenThousands() {
        return sixteenThousands;
    }

    /**
     * @return field's image for 32 768 value.
     */
    public Drawable getThirtyTwoThousands() {
        return thirtyTwoThousands;
    }

    /**
     * @return field's image for 65 536 value.
     */
    public Drawable getSixtyFiveThousands() {
        return sixtyFiveThousands;
    }

    /**
     * @return field's image for 131 072 value.
     */
    public Drawable getOneHundredThousands() {
        return oneHundredThousands;
    }

    /**
     * @return green field's background image.
     */
    public Drawable getButtonGreeen() {
        return buttonGreeen;
    }

    /**
     * @return light green field's background image.
     */
    public Drawable getButtonGreeenLight() {
        return buttonGreeenLight;
    }

    /**
     * @return blue field's background image.
     */
    public Drawable getButtonBlue() {
        return buttonBlue;
    }

    /**
     * @return light blue field's background image.
     */
    public Drawable getButtonBlueLight() {
        return buttonBlueLight;
    }

}
