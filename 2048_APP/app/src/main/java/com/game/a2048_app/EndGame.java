package com.game.a2048_app;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.game.a2048_app.helpers.DarkModeHelper;
import com.game.a2048_app.helpers.PreferencesHelper;
import com.game.a2048_app.helpers.Preloader;
import com.game.a2048_app.helpers.SoundPlayer;

public class EndGame extends AppCompatActivity {

    // TODO: 23.07.2020  Nasze work list:
    //  - java doc
    //  - zrobic xml do buttons
    //  - kiedy pausujemy gre sensorem to ma sie zmienic przycik
    //  - przywrocic gre do singletona
    //  - https://developer.android.com/studio/build/shrink-code.html

    private String score;
    private String highScore;
    private Boolean authentication;
    private Button homePageButton;
    private Preloader preloader = Preloader.getInstance();
//    private final PreferencesHelper preferencesHelper = PreferencesHelper.getInstance();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesHelper.initContext(this);
        getExtrasFromIntent();
        setContentView(R.layout.activity_end_game);
        homePageButton = findViewById(R.id.homePage);
        loadData();
    }

    private void getExtrasFromIntent() {
        Intent intent = getIntent();
        this.score = intent.getStringExtra(getResources().getString(R.string.score));
        this.highScore = intent.getStringExtra(getResources().getString(R.string.high_score));
        this.authentication = Boolean.parseBoolean(intent.getStringExtra(getResources().getString(R.string.authentication)));
    }

    /**
     * Calls method to set TextView texts.
     */
    private void loadData() {
        DarkModeHelper.setTheme((ImageView) findViewById(R.id.darkThemeView));
        setTextScoreText();
        setTextHighScoreText();
    }

    /**
     * Sets text to display score.
     */
    void setTextScoreText() {
        TextView textScore = (TextView) findViewById(R.id.textScore);
        textScore.setText(String.format("%s:\n%s", getResources().getString(R.string.score), score));
    }

    /**
     * Sets text to display high score.
     */
    void setTextHighScoreText() {
        if (authentication) {
            TextView textScore = (TextView) findViewById(R.id.textHighScore);
            textScore.setText(String.format("%s:\n%s", getResources().getString(R.string.high_score), highScore));
        }
    }

    private MediaPlayer.OnCompletionListener setHomePageButtonBackgroundListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            homePageButton.setBackground(preloader.getMainButton());
        }
    };

    /**
     * Creates button on click listener to change to main activity.
     * Play sound after click and change button's image.
     */
    public void homePageButtonOnClick(View v) {
        homePageButton.setBackground(preloader.getMainButtonClicked());
        SoundPlayer soundPlayer = SoundPlayer.getInstance();
        soundPlayer.playSound(soundPlayer.getAsset(getApplicationContext(), R.raw.slide_activities), setHomePageButtonBackgroundListener);
        startActivity(new Intent(EndGame.this, MainActivity.class));
    }

    /**
     * Pressing back button will not perform any action.
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

}
