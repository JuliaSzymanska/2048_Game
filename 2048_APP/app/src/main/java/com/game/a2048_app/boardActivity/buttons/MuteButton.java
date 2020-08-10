package com.game.a2048_app.boardActivity.buttons;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.game.a2048_app.helpers.PreferencesHelper;
import com.game.a2048_app.helpers.Preloader;
import com.game.a2048_app.helpers.SoundPlayer;


// TODO: 09.08.2020 dodać xml do przycisku
public class MuteButton extends androidx.appcompat.widget.AppCompatButton {

    // TODO: 09.08.2020 Wyrzuciłem tutaj ten mute button, ale jako że nie jestem znawcą androida to kurna nwm czy tak powinno być xDD

    private final PreferencesHelper preferencesHelper = PreferencesHelper.getInstance();
    private Preloader preloader = Preloader.getInstance();

    public MuteButton(Context context) {
        super(context);
        this.prepareButton();
    }

    private OnClickListener onClickListener = new OnClickListener() {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            muteButtonOnClick(v);
        }
    };

    private void prepareButton() {
        this.setMuteButtonImage();
        this.setOnClickListener(this.onClickListener);
    }

    public MuteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.prepareButton();
    }

    public MuteButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.prepareButton();
    }

    /**
     * Sets the appropriate mute button's image
     */
    private void setMuteButtonImage() {
        if (preferencesHelper.getVolume() == 1) {
            this.setBackground(preloader.getMuteOff());
        } else if (preferencesHelper.getVolume() == 0) {
            this.setBackground(preloader.getMuteOn());
        } else {
            throw new IllegalArgumentException("Argument value should be 0 or 1");
        }
    }

    /**
     * Sets volume level - mute or unmute.
     * Changes button's image.
     */
    private void setMuteSettings() {
        setMuteButtonImage();
        SoundPlayer.getInstance().setVolume(preferencesHelper.getVolume());
    }

    /**
     * Creates button on click listener to mute or unmute application.
     * Play sound after click and change button's image.
     */
    // TODO: 09.08.2020 chce to usstawić w xml
    public void muteButtonOnClick(View v) {
        if (preferencesHelper.getVolume() == 0) {
            preferencesHelper.setVolume(1);
        } else if (preferencesHelper.getVolume() == 1) {
            preferencesHelper.setVolume(0);
        } else {
            throw new IllegalArgumentException("Argument value should be 0 or 1");
        }
        try {
            setMuteSettings();
        } catch (IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
