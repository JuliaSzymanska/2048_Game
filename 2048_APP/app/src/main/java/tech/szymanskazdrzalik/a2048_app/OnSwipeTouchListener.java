package tech.szymanskazdrzalik.a2048_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeTouchListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context ctx, View mainView) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        mainView.setOnTouchListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public class GestureListener extends
            GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 40;
        private static final int SWIPE_VELOCITY_THRESHOLD = 40;


        /**
         * {@inheritDoc}
         */
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        /**
         * {@inheritDoc}
         */
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

    void onSwipeRight() {
        this.onSwipe.swipeRight();
    }

    void onSwipeLeft() {
        this.onSwipe.swipeLeft();
    }

    void onSwipeTop() {
        this.onSwipe.swipeTop();
    }

    void onSwipeBottom() {
        this.onSwipe.swipeBottom();
    }

    public interface onSwipeListener {
        void swipeRight();

        void swipeTop();

        void swipeBottom();

        void swipeLeft();
    }

    public onSwipeListener onSwipe;
}