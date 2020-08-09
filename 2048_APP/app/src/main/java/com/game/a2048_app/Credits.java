package com.game.a2048_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.game.a2048_app.helpers.DarkModeHelper;
import com.game.a2048_app.helpers.PreferencesHelper;
import com.game.a2048_app.helpers.Preloader;

public class Credits extends AppCompatActivity {

    PreferencesHelper preferencesHelper = PreferencesHelper.getInstance();

    Preloader preloader = Preloader.getInstance();

    // TODO: 02.08.2020 uzupelnic
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        setupSwipeListener();
        this.loadData();
    }

    /**
     * Call method to set theme.
     * Loads volume and current theme.
     */
    private void loadData() {
        DarkModeHelper.setTheme((ImageView) findViewById(R.id.darkThemeView));
    }


    private void setupSwipeListener() {
        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(this, findViewById(R.id.constraintLayoutCredits));
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
                startActivity(new Intent(credits, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            }

            @Override
            public void swipeLeft() {
            }
        };
    }

}