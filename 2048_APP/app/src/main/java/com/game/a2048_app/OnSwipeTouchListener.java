package com.game.a2048_app;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.game.module.Field;
import com.game.module.Game;
import com.game.module.GameOverException;

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
            } catch (GameOverException ignored) {
                // FIXME: 07.07.2020 GameOver
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

    // FIXME: 13.07.2020 wrócić do BoardActivity
    static void setupListener(OnSwipeTouchListener onSwipeTouchListener, final View view, final BoardActivity boardActivity,
                              final Game game, final ArrayAdapter<Field> adapter,
                              final TextView score, final TextView highScore) {
        onSwipeTouchListener = new OnSwipeTouchListener(boardActivity, view);
        onSwipeTouchListener.onSwipe = new OnSwipeTouchListener.onSwipeListener() {
            @Override
            public void swipeRight() throws GameOverException {
               boardActivity.moveRight();
            }

            @Override
            public void swipeTop() throws GameOverException {
                boardActivity.moveUp();
            }

            @Override
            public void swipeBottom() throws GameOverException {
                boardActivity.moveDown();
            }

            @Override
            public void swipeLeft() throws GameOverException {
                boardActivity.moveLeft();
            }
        };
    }
}