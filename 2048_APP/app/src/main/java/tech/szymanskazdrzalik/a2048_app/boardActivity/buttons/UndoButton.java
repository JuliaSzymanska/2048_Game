package tech.szymanskazdrzalik.a2048_app.boardActivity.buttons;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import tech.szymanskazdrzalik.a2048_app.R;
import tech.szymanskazdrzalik.a2048_app.boardActivity.BoardActivityListener;
import tech.szymanskazdrzalik.a2048_app.helpers.Preloader;
import tech.szymanskazdrzalik.a2048_app.helpers.SoundPlayer;
import tech.szymanskazdrzalik.module.Game;

public class UndoButton extends androidx.appcompat.widget.AppCompatButton {

    private Game game;
    private TextView undoTextView;
    private Preloader preloader = Preloader.getInstance();
    private Context context;
    private BoardActivityListener boardActivityListener;
    /**
     * Calls method after sound finished.
     */
    private MediaPlayer.OnCompletionListener setUndoAmountListener = mp -> setUndoNumber();
    private OnClickListener onClickListener = new OnClickListener() {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            undoButtonOnClick();
        }
    };

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

    /**
     * Sets class context, on click listener and amount of available undo's.
     *
     * @param context context from activity.
     */
    private void setupButton(Context context) {
        this.context = context;
        this.setOnClickListener(this.onClickListener);
        this.undoTextView = findViewById(R.id.undoMoveButton);
        this.boardActivityListener = (BoardActivityListener) context;
    }

    /**
     * Sets class attribute to param.
     *
     * @param game current game.
     */
    public void setGame(Game game) {
        this.game = game;
        this.setUndoNumber();
    }

    /**
     * Method called by on click listener. Play sound and change button's image.
     * Calls game's method to undo previous move.
     */
    public void undoButtonOnClick() {
        this.setBackground(preloader.getUndoClicked());
        SoundPlayer soundPlayer = SoundPlayer.getInstance();
        soundPlayer.playSound(soundPlayer.getAsset(this.context, R.raw.undo), setUndoAmountListener);
        game.undoPreviousMove();
        boardActivityListener.callback(this, context.getString(R.string.Undo_Succeed));
    }

    /**
     * Sets undo's button's image and TextView with number of available undo.
     */
    public void setUndoNumber() {
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
