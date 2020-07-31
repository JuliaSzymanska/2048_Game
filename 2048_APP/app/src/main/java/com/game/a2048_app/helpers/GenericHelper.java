package com.game.a2048_app.helpers;

import android.os.Build;

public class GenericHelper {
    public static boolean canCancelAnimation() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }
}
