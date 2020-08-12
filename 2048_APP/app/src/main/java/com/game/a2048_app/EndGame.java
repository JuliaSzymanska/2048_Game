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
    //  - po kliknięciu resetart Button okno z pytaniem czy na pewno chce sie zrestartowac
    //  - przywrocic gre do singletona
    //  - https://developer.android.com/studio/build/shrink-code.html
    //  - nazwy domen :
    //      - tech.szymanskazdrzalik.a2048_app
    //  - logger?
    //  - https://freemusicarchive.org/music/Kevin_MacLeod - jesli uzyjemy trzeba go gdzieś w apce zcreditować, wg jego licencji.
    //  1. Przycisk głośności na czerowono jak jest zbliżenie
    //  2. Zmnienszyc restart button
    //  3. Po kliknieciu restartu pytanie czy na pewno
    //  4. Chciałeś zrobić te głośniki mute i settings w main activity
    //  5. Sprawko 😥
    //  6. Montaż treningowy przy pierwszym uruchomieniu

    private static String score;
    private static String highScore;
    private Button homePageButton;
    private Preloader preloader = Preloader.getInstance();
    private OnSwipeTouchListener onSwipeTouchListener;
    private static boolean isAuthenticated = false;

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
        ((SwipeUpCreditsButton) findViewById(R.id.authors)).setupSwipeTopListener(this, findViewById(R.id.endGame));
    }

    private void getExtrasFromIntent() {
        Intent intent = getIntent();
        String score = intent.getStringExtra(getResources().getString(R.string.score));
        String highScore = intent.getStringExtra(getResources().getString(R.string.high_score));
        // TODO: 11.08.2020 to nadal zresetuje się po przejsciu do credits.
        //  https://stackoverflow.com/a/2098936 - tak bym zrobił zapisywanie czy jesteśmy authenticated i wtedy usunał wszędzie pola isAuthenticated z każdej klasy
        EndGame.isAuthenticated = intent.getBooleanExtra(getResources().getString(R.string.authentication), false);
        // TODO: 11.08.2020 nie jestem pewien czy to jest null czy string null
        if (score != null && !score.equals("null"))
            EndGame.score = score;
        if (highScore != null && !highScore.equals("null"))
            EndGame.highScore = highScore;
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
        if (EndGame.isAuthenticated) {
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
