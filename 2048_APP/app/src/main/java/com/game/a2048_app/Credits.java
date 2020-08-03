package com.game.a2048_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Credits extends AppCompatActivity {

    OnSwipeTouchListener onSwipeTouchListener;

    ConstraintLayout constraintLayout;

    // TODO: 02.08.2020 uzupelnic
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        constraintLayout = findViewById(R.id.constraintLayoutCredits);
        setupSwipeListener();
    }

    private void setupSwipeListener() {
        onSwipeTouchListener = new OnSwipeTouchListener(this, constraintLayout);
        final Credits credits = this;
        onSwipeTouchListener.onSwipe = new OnSwipeTouchListener.onSwipeListener() {
            @Override
            public void swipeRight() {
            }

            @Override
            public void swipeTop() {
            }

            @Override
            public void swipeBottom() {
                // TODO: 02.08.2020 dodać odpowiedni transition
                //overridePendingTransition();
                startActivity(new Intent(credits, MainActivity.class));
            }

            @Override
            public void swipeLeft() {
            }
        };
    }

}