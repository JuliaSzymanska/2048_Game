package com.game.a2048_app;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.game.module.GameOverException;

// TODO: 02.06.2020 Aktualnie bardzo mocno polegam na staticach (game oraz adapter) EDIT: JUZ NIE ale zostawiam narazie historycznie xD
//  oraz no uzywam oryginalnego boarda a nie chce
//  jestem pewien że da się lepiej ale dopiero zaczyma to ogarniac
//  https://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures
//  https://www.tutorialspoint.com/how-to-detect-swipe-direction-between-left-right-and-up-down-in-android

public class OnSwipeTouchListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector;
    private Context context;

    OnSwipeTouchListener(Context ctx, View mainView) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        mainView.setOnTouchListener(this);
        context = ctx;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public class GestureListener extends
            GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 10;
        private static final int SWIPE_VELOCITY_THRESHOLD = 10;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    void onSwipeRight() throws GameOverException {
        this.onSwipe.swipeRight();
    }

    void onSwipeLeft() throws GameOverException {
        this.onSwipe.swipeLeft();
    }

    void onSwipeTop() throws GameOverException {
        this.onSwipe.swipeTop();
    }

    void onSwipeBottom() throws GameOverException {
        this.onSwipe.swipeBottom();
    }

    interface onSwipeListener {
        void swipeRight() throws GameOverException;

        void swipeTop() throws GameOverException;

        void swipeBottom() throws GameOverException;

        void swipeLeft() throws GameOverException;
    }

    onSwipeListener onSwipe;
}