package tech.szymanskazdrzalik.a2048_app;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import tech.szymanskazdrzalik.a2048_app.helpers.DarkModeHelper;
import tech.szymanskazdrzalik.a2048_app.helpers.PreferencesHelper;
import tech.szymanskazdrzalik.a2048_app.helpers.Preloader;
import tech.szymanskazdrzalik.a2048_app.helpers.SoundPlayer;

public class EndGame extends AppCompatActivity {
    private static String score;
    private static String highScore;
    private Button homePageButton;
    private Preloader preloader = Preloader.getInstance();
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
        ((SwipeTopCreditsButton) findViewById(R.id.authors)).setupSwipeTopListener(this, findViewById(R.id.endGameId));
    }

    /**
     * Get data passed from {@link tech.szymanskazdrzalik.a2048_app.boardActivity.BoardActivity}
     */
    private void getExtrasFromIntent() {
        Intent intent = getIntent();
        String score = intent.getStringExtra(getResources().getString(R.string.score));
        String highScore = intent.getStringExtra(getResources().getString(R.string.high_score));
        EndGame.isAuthenticated = intent.getBooleanExtra(getResources().getString(R.string.authentication), false);
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
        TextView textScore = findViewById(R.id.textScore);
        textScore.setText(String.format("%s:\n%s", getResources().getString(R.string.score), score));
    }

    /**
     * Sets text to display high score.
     */
    void setTextHighScoreText() {
        if (EndGame.isAuthenticated) {
            TextView textScore = findViewById(R.id.textHighScore);
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
