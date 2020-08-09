package com.game.a2048_app.boardActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.databinding.Observable;

import com.game.a2048_app.R;
import com.game.a2048_app.helpers.Preloader;
import com.game.a2048_app.helpers.SoundPlayer;
import com.game.module.Board;
import com.game.module.Game;

// TODO: 09.08.2020 dodac xml

public class UndoButton extends androidx.appcompat.widget.AppCompatButton {
    private Game game;
    private TextView undoTextView;
    private Preloader preloader = Preloader.getInstance();
    private Context context;

    private OnClickListener onClickListener = new OnClickListener() {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
           undoButtonOnClick(v);
        }
    };

    private void setupButton(Context context) {
        this.context = context;
        this.setOnClickListener(this.onClickListener);
    }

    public UndoButton(Context context) {
        super(context);
        this.setupButton(context);
    }

    public UndoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setupButton(context);
    }

    public UndoButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setupButton(context);
    }

    // TODO: 09.08.2020 Cos tak czulem zeby zostawic singleton ehh
    void setGame(Game game) {
        this.game = game;
    }

    private MediaPlayer.OnCompletionListener setUndoAmountListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            setUndoNumber();
        }
    };

    public void undoButtonOnClick(View v) {
        this.setBackground(preloader.getUndoClicked());
        SoundPlayer soundPlayer = SoundPlayer.getInstance();
        soundPlayer.playSound(soundPlayer.getAsset(this.context, R.raw.undo), setUndoAmountListener);
        game.undoPreviousMove();
    }

    /**
     * Sets undo's button's image and TextView with number of available undo.
     */
    @SuppressLint("DefaultLocale")
    private void setUndoNumber() {
        int undoNumber = this.game.getAvailableUndoNumber();
        if (undoNumber > 0) {
            this.setBackground(preloader.getUndo());
            undoTextView.setText(String.format("%d", undoNumber));
            this.setEnabled(true);
        } else if (undoNumber == 0) {
            this.setBackground(preloader.getUndoClicked());
            undoTextView.setText(String.format("%d", undoNumber));
            this.setEnabled(false);
        } else {
            throw new IllegalArgumentException("Undo number has to be positive number.");
        }
    }

}
