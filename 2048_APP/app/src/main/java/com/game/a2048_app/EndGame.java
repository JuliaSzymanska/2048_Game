package com.game.a2048_app;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndGame extends AppCompatActivity {

    // TODO: 23.07.2020  Nasze work list:
    //  - javaDoc
    //  - testy testy testy (jeden jest prawdkopodobnie zepsuty, dodać testy do tego co dzisiaj zrobiłem)
    //  - wyczyścić to co zrobiłem w board i boardActivity, zoptymalizować, usunac nie potrzebny kod, podzielić na funkcje, uładnić kod
    //  - wywalić moje zmiany w animacji i poprawic to jak działa animacja
    //  - jak wyjde z apki i wejde jeszcze raz nie zamykając jej i będąc w activity gry, resetuje się głośność na 1,
    //  podczas gdy przycisk wyciszenia wskazuje wyciszenie - trzeba by załadować poprzednią głośność

    private String score;
    private String highScore;
    private Boolean authentication;
    private EndGame endgame = this;
    private Button homePageButton;
    MediaPlayer mediaPlayer;
    private static final PreferencesHelper preferencesHelper = PreferencesHelper.getInstance();

    /**
     * Loads theme, score and high score passed by previous activity.
     * Sets buttons id.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        PreferencesHelper.initContext(this);
        score = intent.getStringExtra(getResources().getString(R.string.score));
        highScore = intent.getStringExtra(getResources().getString(R.string.high_score));
        authentication = Boolean.parseBoolean(intent.getStringExtra(getResources().getString(R.string.authentication)));
        setContentView(R.layout.activity_end_game);
        homePageButton = findViewById(R.id.homePage);
        homePageButton.setOnClickListener(initializeMainActivity);
        loadData();
    }

    /**
     * Calls method to set TextVIew texts.
     */
    private void loadData() {
        boolean isDarkTheme = preferencesHelper.getDarkTheme();
        setTheme(isDarkTheme);
        setTextScoreText();
        setTextHighScoreText();
    }

    /**
     * Set dark or light theme.
     * @param isDarkTheme is dark theme on.
     */
    private void setTheme(boolean isDarkTheme) {
        ImageView darkThemeView = (ImageView) findViewById(R.id.darkThemeView);
        if (isDarkTheme) {
            darkThemeView.setImageResource(R.drawable.dark_theme_on);
        } else {
            darkThemeView.setImageResource(R.drawable.dark_theme_off);
        }
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

    /**
     * Creates button on click listener to change to main activity.
     * Play sound after click and change button's image.
     */
    private View.OnClickListener initializeMainActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            homePageButton.setBackgroundResource(R.drawable.main_activity_button_clicked);
            mediaPlayer = MediaPlayer.create(endgame, R.raw.slide_activities);
            int volume = preferencesHelper.getVolume();
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    homePageButton.setBackgroundResource(R.drawable.main_activity_button);
                }

            });
            startActivity(new Intent(EndGame.this, MainActivity.class));
        }
    };

    /**
     * Pressing back button will not perform any action.
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * On activity stop media player is released.
     */
    @Override
    protected void onStop() {
        super.onStop();
        this.mediaPlayer.release();
    }

}
