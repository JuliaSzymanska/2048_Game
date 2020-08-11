package com.game.a2048_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import com.game.a2048_app.helpers.Preloader;


public class SwipeUpCreditsButton extends androidx.appcompat.widget.AppCompatButton {
    private Preloader preloader = Preloader.getInstance();
    private Context context;



    public void initContext(Context context) {
        this.context = context;
    }

    public SwipeUpCreditsButton(Context context) {
        super(context);
        initContext(context);
        this.prepareButton();
    }

    public SwipeUpCreditsButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initContext(context);
        this.prepareButton();
    }

    public SwipeUpCreditsButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initContext(context);
        this.prepareButton();
    }

    private OnClickListener onClickListener = new OnClickListener() {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            swipeUpCreditsButtonOnClick(v);
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
    // TODO: 09.08.2020 chce to usstawić w xml
    public void swipeUpCreditsButtonOnClick(View v) {
        Intent intent = new Intent(context, Credits.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) this.context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        }
    }
}
