package com.game.a2048_app;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.game.a2048_app.helpers.PreferencesHelper;
import com.game.a2048_app.helpers.Preloader;
import com.game.a2048_app.helpers.SoundPlayer;

public class EndGame extends AppCompatActivity {

    // TODO: 23.07.2020  Nasze work list:
    //  - javaDoc
    //  - testy testy testy (jeden jest prawdkopodobnie zepsuty, dodać testy do tego co dzisiaj zrobiłem)
    //  - wyczyścić to co zrobiłem w board i boardActivity, zoptymalizować, usunac nie potrzebny kod, podzielić na funkcje, uładnić kod
    //  - próbowałem zrobić żeby animacje się przerywały - co prawda udało się, ale następna animacja zostawała pominięta, l
    //      lub odtwarzała się, ale dokonany ruch już się wyświetlał, a przesuwały się puste klocki, nie umiem tego naprawić, zostawiam blokadę
    //  - https://stackoverflow.com/questions/5465204/how-can-i-set-up-multiple-listeners-for-one-event
    //      użyć żeby zrobić klasę dla dzwieku i dwa konstruktory - domyślny ze sciezka dzwiekowa, i drugi z sciezka i z listenerem.
    //      automatycznie ma byc listener który usuwa media player, ale można dolaczyć swój żeby coś się stało
    //      może uda się zejść ponizej 1000 linii wtedy

    private String score;
    private String highScore;
    private Boolean authentication;
    private Button homePageButton;
    private Preloader preloader = Preloader.getInstance();
    private static final PreferencesHelper preferencesHelper = PreferencesHelper.getInstance();

    /**
     * {@inheritDoc}
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
     * Calls method to set TextView texts.
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
            darkThemeView.setImageDrawable(preloader.getDarkThemeOn());
        } else {
            darkThemeView.setImageDrawable(preloader.getDarkThemeOff());
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

    MediaPlayer.OnCompletionListener setHomePageButtonBackgroundListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            homePageButton.setBackground(preloader.getMainButton());
        }
    };

    /**
     * Creates button on click listener to change to main activity.
     * Play sound after click and change button's image.
     */
    private View.OnClickListener initializeMainActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            homePageButton.setBackground(preloader.getMainButtonClicked());
            SoundPlayer soundPlayer = SoundPlayer.getInstance();
            soundPlayer.playSound(soundPlayer.getAsset(getApplicationContext(), R.raw.slide_activities), setHomePageButtonBackgroundListener);
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
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

}
