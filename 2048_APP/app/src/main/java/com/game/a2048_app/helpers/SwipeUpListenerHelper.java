package com.game.a2048_app.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.game.a2048_app.Credits;
import com.game.a2048_app.OnSwipeTouchListener;
import com.game.a2048_app.R;

public class SwipeUpListenerHelper {

    public static void setupSwipeListener(final Context context, View view) {
        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(context, view);
        onSwipeTouchListener.onSwipe = new OnSwipeTouchListener.onSwipeListener() {
            @Override
            public void swipeRight() {
            }

            @Override
            public void swipeTop() {
                if (context == null) {
                    throw new NullPointerException("Null context. ");
                }
                Intent intent = new Intent(context, Credits.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }

            @Override
            public void swipeBottom() {
            }

            @Override
            public void swipeLeft() {
            }
        };
    }

}
