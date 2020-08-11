package com.game.a2048_app.credits;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import com.game.a2048_app.OnSwipeTouchListener;
import com.game.a2048_app.R;
import com.game.a2048_app.helpers.Preloader;

import java.util.Objects;

public class SwipeDownCreditsButton extends androidx.appcompat.widget.AppCompatButton  {

    private Preloader preloader = Preloader.getInstance();
    private Context context;

    public void initContext(Context context) {
        this.context = context;
    }

    public SwipeDownCreditsButton(Context context) {
        super(context);
        initContext(context);
        this.prepareButton();
    }

    public SwipeDownCreditsButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initContext(context);
        this.prepareButton();
    }

    public SwipeDownCreditsButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initContext(context);
        this.prepareButton();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            swipeDownCreditsButtonOnClick(v);
        }
    };

    private void prepareButton() {
        this.setSwipeUpCreditsImage();
        this.setOnClickListener(this.onClickListener);
    }


    /**
     * Sets the swipe up button's image
     */
    private void setSwipeUpCreditsImage() {
        this.setBackground(preloader.getSwipeUpCredits());
    }

    /**
     * Creates button on click listener to swipe up.
     * Starts new credits activity.
     */
    // TODO: 09.08.2020 chce to ustawić w xml
    public void swipeDownCreditsButtonOnClick(View v) {
        startNewActivity();
    }

    private void startNewActivity(){
        Intent intent = null;
        try {
            intent = new Intent(context, Class.forName(Objects.requireNonNull( ((Activity) this.context).getIntent().getStringExtra(Activity.ACTIVITY_SERVICE))));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) this.context).overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        }
    }


    public void setupSwipeBottomListener(final Context context, View view) {
        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(context, view);
        onSwipeTouchListener.onSwipe = new OnSwipeTouchListener.onSwipeListener() {
            @Override
            public void swipeRight() {
            }

            @Override
            public void swipeTop() {
            }

            @Override
            public void swipeBottom() {
                startNewActivity();
            }

            @Override
            public void swipeLeft() {
            }
        };
    }

}
