package com.game.a2048_app;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.game.module.Game;

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

    interface onSwipeListener {
        void swipeRight();

        void swipeTop();

        void swipeBottom();

        void swipeLeft();
    }

    onSwipeListener onSwipe;

    // FIXME: 13.07.2020 wrócić do BoardActivity
    static void setupListener(OnSwipeTouchListener onSwipeTouchListener, final View view, final BoardActivity boardActivity,
                              final Game game, final ArrayAdapter<Integer> adapter,
                              final TextView score, final TextView highScore) {
        onSwipeTouchListener = new OnSwipeTouchListener(boardActivity, view);
        onSwipeTouchListener.onSwipe = new OnSwipeTouchListener.onSwipeListener() {
            @Override
            public void swipeRight() {
                boardActivity.move(BoardActivity.MOVE_RIGHT);
            }

            @Override
            public void swipeTop() {
                boardActivity.move(BoardActivity.MOVE_UP);
            }

            @Override
            public void swipeBottom() {
                boardActivity.move(BoardActivity.MOVE_DOWN);
            }

            @Override
            public void swipeLeft() {
                boardActivity.move(BoardActivity.MOVE_LEFT);
            }
        };
    }
}