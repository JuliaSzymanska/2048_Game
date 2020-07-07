package com.game.a2048_app;

import android.app.Activity;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.game.module.Field;
import com.game.module.Game;
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

    static void setupListener(OnSwipeTouchListener onSwipeTouchListener, View view, Activity activity, final Game game, final ArrayAdapter<Field> adapter, final TextView score) {
        onSwipeTouchListener = new OnSwipeTouchListener(activity, view);
        onSwipeTouchListener.onSwipe = new OnSwipeTouchListener.onSwipeListener() {
            @Override
            public void swipeRight() throws GameOverException {
                game.move(Game.MOVE_RIGHT);
                setScoreAndUpdate();
            }

            @Override
            public void swipeTop() throws GameOverException {
                game.move(Game.MOVE_UP);
                setScoreAndUpdate();
            }

            @Override
            public void swipeBottom() throws GameOverException {
                game.move(Game.MOVE_DOWN);
                setScoreAndUpdate();
            }

            @Override
            public void swipeLeft() throws GameOverException {
                game.move(Game.MOVE_LEFT);
                setScoreAndUpdate();
            }

            private void setScoreAndUpdate() {
                // TODO: 02.06.2020 Po callnieciu adapter.notifyDataSetChanged() aktualizuje sie gridview.
                adapter.notifyDataSetChanged();
                // TODO: 04.06.2020 narazie tak to jest potem trzebabedzie dodac jakies ladne listenery albo bindingi
                score.setText(String.format("%s%s", "Wynik: ", game.getCurrentScore()));
            }
        };
    }

    onSwipeListener onSwipe;
}