package com.game.a2048_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.game.module.Game;

public class BoardActivity extends AppCompatActivity {

    private Game game = Game.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
    }

    private void fillGrid(){

    }

}
