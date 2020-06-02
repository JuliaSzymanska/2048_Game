package com.game.a2048_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.game.module.Field;
import com.game.module.Game;
import com.game.module.GameOverException;

public class BoardActivity extends AppCompatActivity {
// public class BoardActivity extends AppCompatActivity implements BoardActivity.OnSwipeTouchListener.onSwipeListener {

    OnSwipeTouchListener onSwipeTouchListener;

    private Game game = Game.getInstance();
    private ArrayAdapter<Field> adapter;
    private GridView gridView;
    // TODO: 01.06.2020 narazie mamy zwykla tablice fieldow
    private Field[] fields = game.getCopyOfTheBoard().toArray(new Field[0]);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        gridView = (GridView) findViewById(R.id.gridView);
        // TODO: 01.06.2020 cos mi sie obilo o uszy ze to moze byc listener ale i'm not sure to trzeba bedzie sprawdzic
        adapter = new ArrayAdapter<Field>(this,
                android.R.layout.simple_list_item_1, fields);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // TODO: 01.06.2020 to jest po prostu to ze jak klikniesz to na dole sie pojawia co wybrales
                //  dodalalm to zeby po prostu poprobowac cos trzeba to bedzie jakos madrze zrobic
                Toast.makeText(getApplicationContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show();

            }
        });
       this.onSwipeTouchListener = new OnSwipeTouchListener(this, gridView);
       this.onSwipeTouchListener.onSwipe = new OnSwipeTouchListener.onSwipeListener() {
           @Override
           public void swipeRight() throws GameOverException {
               game.move(Game.MOVE_RIGHT);
               // TODO: 02.06.2020 Po callnieciu adapter.notifyDataSetChanged() aktualizuje sie gridview.
               adapter.notifyDataSetChanged();
           }

           @Override
           public void swipeTop() throws GameOverException {
               game.move(Game.MOVE_UP);
               adapter.notifyDataSetChanged();
           }

           @Override
           public void swipeBottom() throws GameOverException {
               game.move(Game.MOVE_DOWN);
               adapter.notifyDataSetChanged();
           }

           @Override
           public void swipeLeft() throws GameOverException {
               game.move(Game.MOVE_LEFT);
               adapter.notifyDataSetChanged();
           }
       };

    }


    private void setListOfFields() {

    }

    // TODO: 02.06.2020 Aktualnie bardzo mocno polegam na staticach (game oraz adapter)
    //  oraz kurcze no uzywam oryginalnego boarda a nie chce
    //  jestem pewien że da się lepiej ale dopiero zaczyma to ogarniac a teraz ide sobie zrobic jesc
    //  https://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures
    //  https://www.tutorialspoint.com/how-to-detect-swipe-direction-between-left-right-and-up-down-in-android
    public static class OnSwipeTouchListener implements View.OnTouchListener {
        private final GestureDetector gestureDetector;
        Context context;

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
}


