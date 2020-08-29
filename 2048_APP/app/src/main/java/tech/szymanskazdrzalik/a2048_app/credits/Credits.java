package tech.szymanskazdrzalik.a2048_app.credits;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import tech.szymanskazdrzalik.a2048_app.R;
import tech.szymanskazdrzalik.a2048_app.helpers.DarkModeHelper;

public class Credits extends AppCompatActivity {

    private final static String PRZEMEK_GITHUB = "https://github.com/ZdrzalikPrzemyslaw";
    private final static String JULIA_GITHUB = "https://github.com/JuliaSzymanska";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        ((SwipeDownCreditsButton) findViewById(R.id.authors)).setupSwipeBottomListener(this, findViewById(R.id.constraintLayoutCredits));
        this.loadData();
    }

    /**
     * On click listener for Przemek's text view.
     *
     * @param v view.
     */
    public void onClickTextViewPrzemek(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(PRZEMEK_GITHUB));
        startActivity(i);
    }

    /**
     * On click listener for Julia's text view.
     *
     * @param v view.
     */
    public void onClickTextViewJulia(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(JULIA_GITHUB));
        startActivity(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        ((SwipeDownCreditsButton) findViewById(R.id.authors)).swipeDownCreditsButtonOnClick();
    }

    /**
     * Call method to set theme.
     * Loads volume and current theme.
     */
    private void loadData() {
        DarkModeHelper.setTheme(findViewById(R.id.darkThemeView));
    }

}