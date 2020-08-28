package tech.szymanskazdrzalik.a2048_app.boardActivity;

import android.view.View;

public interface BoardActivityListener {
    void callback(View view, String result);

    void callback(String result);
}
