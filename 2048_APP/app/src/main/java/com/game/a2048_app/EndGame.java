package com.game.a2048_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndGame extends AppCompatActivity {

    String score;
    String highScore;
    Boolean authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        score = intent.getStringExtra(getResources().getString(R.string.score));
        highScore = intent.getStringExtra(getResources().getString(R.string.high_score));
        authentication = Boolean.parseBoolean(intent.getStringExtra(String.valueOf(R.string.authentication)));
        authentication = Boolean.parseBoolean(intent.getStringExtra(getResources().getString(R.string.authentication)));
        setContentView(R.layout.activity_end_game);
        Button homePage = findViewById(R.id.homePage);
        homePage.setOnClickListener(initializeBoardActivity);
        loadData();
    }

    private void loadData() {
        SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.settings), MODE_PRIVATE);
        boolean isDarkTheme = preferences.getBoolean(getResources().getString(R.string.dark_theme), false);
        setTheme(isDarkTheme);
        setTextScoreText();
        setTextHighScoreText();
    }

    private void setTheme(boolean isDarkTheme) {
        ImageView darkThemeView = (ImageView) findViewById(R.id.darkThemeView);
        if (isDarkTheme) {
            darkThemeView.setImageResource(R.drawable.dark_theme_on);
        } else {
            darkThemeView.setImageResource(R.drawable.dark_theme_off);
        }
    }

    void setTextScoreText() {
        TextView textScore = (TextView) findViewById(R.id.textScore);
        textScore.setText(String.format("%s%s", "Your score:\n", score));
    }

    void setTextHighScoreText() {
        if (authentication) {
            TextView textScore = (TextView) findViewById(R.id.textHighScore);
            textScore.setText(String.format("%s%s", "High score:\n", highScore));
        }
    }

    private View.OnClickListener initializeBoardActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(EndGame.this, MainActivity.class));
        }
    };

    @Override
    public void onBackPressed() {
    }

}
