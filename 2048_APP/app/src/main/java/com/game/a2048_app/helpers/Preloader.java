package com.game.a2048_app.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.game.a2048_app.R;

public class Preloader {

    private static Preloader instance = new Preloader();
    private static Context context;

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


    @SuppressLint("CommitPrefEdits")
    public static void initContext(Context context) {
        if (Preloader.context == null) {
            Preloader.context = context.getApplicationContext();
        }
    }

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

    public static Preloader getInstance() {
        return instance;
    }

    public Drawable getUndoClicked() {
        return undoClicked;
    }

    public Drawable getPausePlayOn() {
        return pausePlayOn;
    }

    public Drawable getPausePlayOff() {
        return pausePlayOff;
    }

    public Drawable getMuteOn() {
        return muteOn;
    }

    public Drawable getMuteOff() {
        return muteOff;
    }

    public Drawable getUndo() {
        return undo;
    }

    public Drawable getDarkThemeOn() {
        return darkThemeOn;
    }

    public Drawable getDarkThemeOff() {
        return darkThemeOff;
    }

    public Drawable getSetttings() {
        return setttings;
    }

    public Drawable getSetttingsClicked() {
        return setttingsClicked;
    }

    public Drawable getZero() {
        return zero;
    }

    public Drawable getButtonGreeen() {
        return buttonGreeen;
    }

    public Drawable getMainButtonClicked() {
        return mainButtonClicked;
    }

    public Drawable getMainButton() {
        return mainButton;
    }

    public Drawable getTwo() {
        return two;
    }

    public Drawable getFour() {
        return four;
    }

    public Drawable getEight() {
        return eight;
    }

    public Drawable getSixteen() {
        return sixteen;
    }

    public Drawable getThirtyTwo() {
        return thirtyTwo;
    }

    public Drawable getSixtyFour() {
        return sixtyFour;
    }

    public Drawable getOneHundred() {
        return oneHundred;
    }

    public Drawable getTwoHundreds() {
        return twoHundreds;
    }

    public Drawable getFiveHundreds() {
        return fiveHundreds;
    }

    public Drawable getOneThousand() {
        return oneThousand;
    }

    public Drawable getTwoThousands() {
        return twoThousands;
    }

    public Drawable getFourThousands() {
        return fourThousands;
    }

    public Drawable getEightThousands() {
        return eightThousands;
    }

    public Drawable getSixteenThousands() {
        return sixteenThousands;
    }

    public Drawable getThirtyTwoThousands() {
        return thirtyTwoThousands;
    }

    public Drawable getSixtyFiveThousands() {
        return sixtyFiveThousands;
    }

    public Drawable getOneHundredThousands() {
        return oneHundredThousands;
    }

    public Drawable getButtonGreeenLight() {
        return buttonGreeenLight;
    }

    public Drawable getButtonBlue() {
        return buttonBlue;
    }

    public Drawable getButtonBlueLight() {
        return buttonBlueLight;
    }

}
