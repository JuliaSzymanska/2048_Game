package com.game.a2048_app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.game.a2048_app.helpers.DarkModeHelper;

import java.util.Objects;

public class Credits extends AppCompatActivity {

    private final static String PRZEMEK_GITHUB = "https://github.com/ZdrzalikPrzemyslaw";
    private final static String JULIA_GITHUB = "https://github.com/JuliaSzymanska";

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

    public void onClickTextViewJulia(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(JULIA_GITHUB));
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
                Intent intent = getIntent();
                try {
                    startActivity(new Intent(Credits.this, Class.forName(Objects.requireNonNull(intent.getStringExtra(Activity.ACTIVITY_SERVICE)))));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            }

            @Override
            public void swipeLeft() {
            }
        };
    }

}