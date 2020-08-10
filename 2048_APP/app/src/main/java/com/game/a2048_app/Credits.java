package com.game.a2048_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.game.a2048_app.helpers.DarkModeHelper;
import com.game.a2048_app.helpers.PreferencesHelper;
import com.game.a2048_app.helpers.Preloader;

public class Credits extends AppCompatActivity {

    private PreferencesHelper preferencesHelper = PreferencesHelper.getInstance();

    private final String PRZEMEK_GITHUB = "https://github.com/ZdrzalikPrzemyslaw";
    private final String DZULKA_GITHUB = "https://github.com/JuliaSzymanska";

    Preloader preloader = Preloader.getInstance();

    // TODO: 02.08.2020 uzupelnic
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        setupSwipeListener();
        this.loadData();
    }

    public void onClickTextViewPrzemek(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(PRZEMEK_GITHUB));
        startActivity(i);
    }

    public void onClickTextViewDzulka(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(DZULKA_GITHUB));
        startActivity(i);
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
        onSwipeTouchListener.onSwipe = new OnSwipeTouchListener.onSwipeListener() {
            @Override
            public void swipeRight() {
            }

            @Override
            public void swipeTop() {
            }

            @Override
            public void swipeBottom() {
                startActivity(new Intent(Credits.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            }

            @Override
            public void swipeLeft() {
            }
        };
    }

}