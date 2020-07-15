package com.game.a2048_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class EndGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        Button homePage = findViewById(R.id.homePage);
        homePage.setBackgroundResource(R.drawable.main_activity_button);
        homePage.setOnClickListener(initializeBoardActivity);
    }

    private View.OnClickListener initializeBoardActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(EndGame.this, MainActivity.class));
        }
    };

}
