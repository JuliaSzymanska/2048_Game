package com.game.a2048_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.game.a2048_app.helpers.PreferencesHelper;
import com.game.module.Field;
import com.game.module.Game;
import com.game.module.exceptions.GameOverException;
import com.game.module.exceptions.GoalAchievedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BoardActivity extends AppCompatActivity implements SensorEventListener {
    private static final float VALUE_DRIFT = 0.05f;

    OnSwipeTouchListener onSwipeTouchListener;

    private Game game;
    private ArrayAdapter<Integer> adapter;
    private GridView gridView;
    private ImageView darkThemeView;
    private Field[] fields;
    private Integer[] fieldsImages;
    private Integer[] fieldsBackground;
    private Integer mThumbIds = R.drawable.button_green;

    // System sensor manager instance.
    private SensorManager mSensorManager;

    // Accelerometer and magnetometer sensors, as retrieved from the
    // sensor manager.
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;
    private Sensor mSensorLight;
    private Sensor mSensorProximity;

    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];
    private float mLightData;
    private float mProximityData;

    private final boolean[] choosenSensors = new boolean[]{false, false, false, false};

    private TextView textScore;
    private TextView textHighScore;
    private TextView textTime;

    private Button restartGameButton;
    private Button pausePlayButton;
    private Button undoButton;
    private Button settingsButton;
    private Button muteButton;
    private TextView undoTextView;

    private Thread updateTimeThread;

    private final static int PROXIMITY_DISTANCE = 5;

    private final static float RESET_PITCH = 0.2f;
    private final static float RESET_ROLL = 0.2f;

    private final static float DETECT_MOVE_PITCH = 0.7f;
    private final static float DETECT_MOVE_ROLL = 0.7f;

    private final static double HORIZONTAL_PITCH_MAX = 0.5;
    private final static double HORIZONTAL_PITCH_MIN = -0.5;

    private final static double changeColourAzimunthBreakpoint1 = 0.25;
    private final static double changeColourAzimunthBreakpoint2 = 1.25;
    private final static double changeColourAzimunthBreakpoint3 = 1.75;
    private final static double changeColourAzimunthBreakpoint4 = 2.75;

    private final static int DARKMODE_ENABLE_LIGHT = 30;
    private final static int DARKMODE_DISABLE_LIGHT = 50;

    private final static double ANIM_SPEED_SECONDS = 2;

    public final static int MOVE_UP = Game.MOVE_UP;
    public final static int MOVE_RIGHT = Game.MOVE_RIGHT;
    public final static int MOVE_DOWN = Game.MOVE_DOWN;
    public final static int MOVE_LEFT = Game.MOVE_LEFT;

    private boolean hasMoved = false;

    private boolean isDarkTheme = false;
    private int volume = 1;
    private static final PreferencesHelper preferencesHelper = PreferencesHelper.getInstance();

    private BoardActivity boardActivity = this;

    private MediaPlayer mediaPlayer;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    // https://developer.android.com/guide/components/activities/activity-lifecycle
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesHelper.initContext(boardActivity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_board);
        this.loadData();
        this.initMediaPlayer();
        this.prepareViews();
        this.prepareSensors();
    }

    /**
     * Loads settings, game and grid drawable resources.
     */
    private void loadData() {
        preferencesHelper.getChoosenSensors(this.choosenSensors);
        this.isDarkTheme = preferencesHelper.getDarkTheme();
        this.volume = preferencesHelper.getVolume();
        this.game = new Game(Boolean.parseBoolean(getIntent().getStringExtra(getResources().getString(R.string.authentication))), boardActivity);
        this.fields = game.getBoard().toArray(new Field[0]);
        this.fieldsImages = new Integer[fields.length];
        this.fieldsBackground = new Integer[fields.length];
        Arrays.fill(fieldsImages, R.drawable.zero);
        Arrays.fill(fieldsBackground, mThumbIds);
    }

    /**
     * Creates and initialize Media Player.
     */
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
    }

    /**
     * Prepares views like TextViews, Buttons, ImageViews, sets id and onClickListeners.
     */
    private void prepareViews() {
        this.gridView = (GridView) findViewById(R.id.gridView);
        this.prepareGrid();
        this.setupSwipeListener();
        this.textTime = (TextView) findViewById(R.id.time);
        this.prepareScoreText();
        this.prepareHighscoreText();
        this.restartGameButton = (Button) findViewById(R.id.restartGameButton);
        this.restartGameButton.setOnClickListener(restartGameListener);
        this.settingsButton = (Button) findViewById(R.id.settingsButton);
        this.settingsButton.setOnClickListener(settingsListener);
        this.undoButton = (Button) findViewById(R.id.undoMoveButton);
        this.undoButton.setOnClickListener(undoListener);
        this.pausePlayButton = (Button) findViewById(R.id.pausePlayButton);
        this.pausePlayButton.setOnClickListener(playPauseListener);
        this.muteButton = (Button) findViewById(R.id.muteButton);
        this.muteButton.setOnClickListener(muteListener);
        this.setMuteSettings();
        this.darkThemeView = (ImageView) findViewById(R.id.darkThemeView);
        this.setTheme();
        this.undoTextView = (TextView) findViewById(R.id.undoTextView);
        this.setUndoNumber();
        this.adapter.notifyDataSetChanged();
    }

    /**
     * Prepares grid for game's board.
     * Creates array adapter.
     * Converts each textView to viewHolder and sets their background and numbers resources.
     */
    private void prepareGrid() {
        this.adapter = new ArrayAdapter<Integer>(boardActivity,
                android.R.layout.simple_list_item_1, fieldsImages) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                ViewHolderItem viewHolder;
                if (convertView == null) {
                    LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
                    convertView = inflater.inflate(R.layout.item, parent, false);
                    viewHolder = new ViewHolderItem();
                    viewHolder.textViewItem = (TextView) convertView.findViewById(R.id.textView);
                    viewHolder.imageViewItem = (ImageView) convertView.findViewById(R.id.imageView);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolderItem) convertView.getTag();
                }
                viewHolder.textViewItem.setBackgroundResource(fieldsBackground[position]);
                viewHolder.imageViewItem.setTag(position);
                viewHolder.imageViewItem.setImageResource(fieldsImages[position]);
                return convertView;
            }

            @Override
            public void notifyDataSetChanged() {
                setFieldsImages();
                setFieldsBackground();
                super.notifyDataSetChanged();
            }
        };
        gridView.setAdapter(adapter);
    }

    /**
     * Sets swipe touch listener.
     * Each swipe is responsible for the appropriate movement in game.
     */
    private void setupSwipeListener() {
        onSwipeTouchListener = new OnSwipeTouchListener(boardActivity, this.gridView);
        final BoardActivity boardActivity = this;
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

    /**
     * Prepares TextView to display current score.
     */
    private void prepareScoreText() {
        textScore = (TextView) findViewById(R.id.score);
        this.setTextScoreText();
    }

    /**
     * Sets text to display current score.
     */
    void setTextScoreText() {
        textScore.setText(String.format("%s:\n%s", getResources().getString(R.string.score), game.getScore()));
    }

    /**
     * Prepares TextView to display high score.
     */
    private void prepareHighscoreText() {
        textHighScore = (TextView) findViewById(R.id.highScore);
        this.setTextHighScoreText();
    }

    /**
     * Sets text to display high score.
     */
    void setTextHighScoreText() {
        if (game.isUserAuthenticated()) {
            textHighScore.setText(String.format("%s:\n%s", getResources().getString(R.string.high_score), game.getHighScore()));
        }
    }

    /**
     * Creates button on click listener to restart game.
     * Play sound after click and change button's image.
     */
    private View.OnClickListener restartGameListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            restartGameButton.setBackgroundResource(R.drawable.main_activity_button_clicked);
            setMediaPlayer(R.raw.restart);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    restartGameButton.setBackgroundResource(R.drawable.main_activity_button);
                }

            });
            restartGame();
        }
    };

    /**
     * Creates button on click listener to open settings dialog.
     * Play sound after click and change button's image.
     * Creates dialog to allow the user to turn sensors on or off.
     */
    private View.OnClickListener settingsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            settingsButton.setBackgroundResource(R.drawable.settings_clicked);
            setMediaPlayer(R.raw.button_no_reverb);
            AlertDialog.Builder builder = new AlertDialog.Builder(BoardActivity.this.boardActivity);
            builder.setMultiChoiceItems(R.array.sensors, choosenSensors, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    choosenSensors[which] = isChecked;
                }
            });
            builder.setCancelable(false);
            builder.setTitle(R.string.settings_menu_title);
            builder.setPositiveButton(getResources().getString(R.string.dialog_accept), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    preferencesHelper.setChoosenSensors(choosenSensors);
                    settingsButton.setBackgroundResource(R.drawable.settings);
                }
            });
            AlertDialog dialog = builder.create();
            final ListView alertDialogList = dialog.getListView();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    for (int position = 0; position < alertDialogList.getChildCount(); position++) {
                        // TODO: 23.07.2020 Zobaczyć czy dziala
                        if ((mSensorAccelerometer == null && getResources().getStringArray(R.array.sensors)[position]
                                .equals(getResources().getString(R.string.Gyroscope_And_Accelerometer_Settings))
                                || (mSensorMagnetometer == null && getResources().getStringArray(R.array.sensors)[position]
                                .equals(getResources().getString(R.string.Magnetometer_Settings))
                                || mSensorLight == null && getResources().getStringArray(R.array.sensors)[position]
                                .equals(getResources().getString(R.string.Light_Sensor_Settings))
                                || mSensorProximity == null && getResources().getStringArray(R.array.sensors)[position]
                                .equals(getResources().getString(R.string.Proximity_Sensor_Settings))))) {
                            alertDialogList.getChildAt(position).setEnabled(false);
                            alertDialogList.getChildAt(position).setOnClickListener(null);
                        }
                    }
                }
            });
            dialog.show();
        }
    };

    /**
     * Creates button on click listener to undo last move.
     * Play sound after click and change button's image.
     */
    private View.OnClickListener undoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            undoButton.setBackgroundResource(R.drawable.undo_clicked);
            setMediaPlayer(R.raw.undo);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    setUndoNumber();
                }

            });
            game.undoPreviousMove();
            adapter.notifyDataSetChanged();
            setScoreTexts();
        }
    };

    /**
     * Creates button on click listener to pause or unpause game.
     * Play sound after click and change button's image.
     */
    private View.OnClickListener playPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setMediaPlayer(R.raw.pause);
            if (!game.isSuspended()) {
                game.pauseTimer();
                pausePlayButton.setBackgroundResource(R.drawable.pause_play_on);
            } else {
                game.unpauseTimer();
                pausePlayButton.setBackgroundResource(R.drawable.pause_play);
            }
        }
    };

    /**
     * Creates button on click listener to mute or unmute application.
     * Play sound after click and change button's image.
     */
    private View.OnClickListener muteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (volume == 0) {
                volume = 1;
            } else if (volume == 1) {
                volume = 0;
            } else {
                throw new IllegalArgumentException("Argument value should be 0 or 1");
            }
            preferencesHelper.setVolume(volume);
            setMuteSettings();
        }
    };

    /**
     * Sets Media Player sound to param and play it.
     *
     * @param id id of sound in resources.
     */
    private void setMediaPlayer(int id) {
        AssetFileDescriptor assetFileDescriptor = getApplicationContext().getResources().openRawResourceFd(id);
        try {
            try {
                mediaPlayer.isPlaying();
            } catch (IllegalStateException e) {
                this.mediaPlayer.release();
                this.initMediaPlayer();
            }
            this.mediaPlayer.reset();
            this.mediaPlayer.setDataSource(assetFileDescriptor);
            this.mediaPlayer.prepare();
            this.mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets volume level - mute or unmute.
     * Changes button's image.
     */
    private void setMuteSettings() {
        if (volume == 1) {
            muteButton.setBackgroundResource(R.drawable.mute_off);
        } else if (volume == 0) {
            muteButton.setBackgroundResource(R.drawable.mute_on);
        } else {
            throw new IllegalArgumentException("Argument value should be 0 or 1");
        }
        mediaPlayer.setVolume(volume, volume);
    }

    /**
     * Sets dark mode or light mode image resource.
     */
    private void setTheme() {
        if (this.isDarkTheme) {
            darkThemeView.setImageResource(R.drawable.dark_theme_on);
        } else {
            darkThemeView.setImageResource(R.drawable.dark_theme_off);
        }
    }

    /**
     * Sets undo's button's image and TextView with number of available undo.
     */
    @SuppressLint("DefaultLocale")
    private void setUndoNumber() {
        int undoNumber = BoardActivity.this.game.getAvaiableUndoNumber();
        if (undoNumber > 0) {
            undoButton.setBackgroundResource(R.drawable.undo);
            undoTextView.setText(String.format("%d", undoNumber));
            undoButton.setEnabled(true);
        } else if (undoNumber == 0) {
            undoButton.setBackgroundResource(R.drawable.undo_clicked);
            undoTextView.setText(String.format("%d", undoNumber));
            undoButton.setEnabled(false);
        } else {
            throw new IllegalArgumentException("Undo number has to be positive number. ");
        }
    }

    /**
     * Listeners for the sensors are registered in this callback so that
     * they can be unregistered in onStop().
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Listeners for the sensors are registered in this callback and
        // can be unregistered in onStop().
        //
        // Check to ensure sensors are available before registering listeners.
        // Both listeners are registered with a "normal" amount of delay
        // (SENSOR_DELAY_NORMAL).
        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(boardActivity, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(boardActivity, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        if (mSensorLight != null) {
            mSensorManager.registerListener(boardActivity, mSensorLight,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        if (mSensorProximity != null) {
            mSensorManager.registerListener(boardActivity, mSensorProximity,
                    SensorManager.SENSOR_DELAY_GAME);
        }

        this.game.unpauseTimer();
        this.beginUpdatingTime();
    }

    // TODO: 29.07.2020 nie wiem jak to ladnie opisac ಠ_ಠ
    private static class ViewHolderItem {
        TextView textViewItem;
        ImageView imageViewItem;
    }

    /**
     * Sets all field's images to transparent.
     */
    private void setFieldsImagesToZeros() {
        for (int i = 0; i < fields.length; i++) {
            fieldsImages[i] = R.drawable.zero;
            fieldsBackground[i] = mThumbIds;
        }
    }

    /**
     * Sets field's image based on current field's value.
     */
    private void setFieldsImages() {
        for (int i = 0; i < fields.length; i++) {
            switch (fields[i].getValue()) {
                case 0:
                    fieldsImages[i] = R.drawable.zero;
                    break;
                case 2:
                    fieldsImages[i] = R.drawable.two;
                    break;
                case 4:
                    fieldsImages[i] = R.drawable.four;
                    break;
                case 8:
                    fieldsImages[i] = R.drawable.eight;
                    break;
                case 16:
                    fieldsImages[i] = R.drawable.sixteen;
                    break;
                case 32:
                    fieldsImages[i] = R.drawable.thirty_two;
                    break;
                case 64:
                    fieldsImages[i] = R.drawable.sixty_four;
                    break;
                case 128:
                    fieldsImages[i] = R.drawable.one_hundred_twenty_eight;
                    break;
                case 256:
                    fieldsImages[i] = R.drawable.two_hundred_fifty_six;
                    break;
                case 512:
                    fieldsImages[i] = R.drawable.five_hundred_twelve;
                    break;
                case 1024:
                    fieldsImages[i] = R.drawable.thousand;
                    break;
                case 2048:
                    fieldsImages[i] = R.drawable.two_thousands;
                    break;
                case 4096:
                    fieldsImages[i] = R.drawable.four_thousands;
                    break;
                case 8192:
                    fieldsImages[i] = R.drawable.eight_thousands;
                    break;
                case 16384:
                    fieldsImages[i] = R.drawable.sixteen_thousands;
                    break;
                case 32768:
                    fieldsImages[i] = R.drawable.thirty_two_thousands;
                    break;
                case 65536:
                    fieldsImages[i] = R.drawable.sixty_five_thousands;
                    break;
                case 131072:
                    fieldsImages[i] = R.drawable.one_hundred_thousands;
                    break;
            }

        }
    }

    /**
     * Sets field's background based on current field's value.
     */
    private void setFieldsBackground() {
        int lightGreen = R.drawable.button_green_light;
        int darkGreen = R.drawable.button_green;
        int lightBlue = R.drawable.button_blue_light;
        int darkBlue = R.drawable.button_blue;
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getValue() == 0) {
                fieldsBackground[i] = mThumbIds;
            } else {
                if (mThumbIds == lightGreen) {
                    fieldsBackground[i] = darkGreen;
                } else if (mThumbIds == darkGreen) {
                    fieldsBackground[i] = lightGreen;
                } else if (mThumbIds == lightBlue) {
                    fieldsBackground[i] = darkBlue;
                } else if (mThumbIds == darkBlue) {
                    fieldsBackground[i] = lightBlue;
                }
            }
        }
    }

    /**
     * Updates game time every 100 milliseconds.
     */
    private void beginUpdatingTime() {
        updateTimeThread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(100);
                        if (textTime.getText().length() == 0 || textTime.getText().subSequence(textTime.getText().length() - 8, textTime.getText().length()) != game.getElapsedTimeToString()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textTime.setText(String.format("%s:\n%s", getResources().getString(R.string.time), game.getElapsedTimeToString()));
                                }
                            });
                        }

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                }
            }
        };
        updateTimeThread.start();
    }

    /**
     * Calls methods to set TextViews for score and high score.
     */
    void setScoreTexts() {
        this.setTextScoreText();
        this.setTextHighScoreText();
    }

    /**
     * Makes move in appropriate direction and calls move animation.
     *
     * @param direction of movement.
     */
    private void move(int direction) {
        List<Field> fieldsCopies = game.getCopyOfTheBoard();
        try {
            game.move(direction);
            this.makeAnimation(fieldsCopies, direction);
        } catch (GameOverException e) {
            e.printStackTrace();
            changeToEndActivity();
        } catch (GoalAchievedException e) {
            e.printStackTrace();
            makeAnimation(fieldsCopies, direction);
            goalAchieved();
        }
        this.setScoreTexts();
        setUndoNumber();
    }

    /**
     * Calls appropriate base on direction of movement.
     *
     * @param fieldsCopies
     * @param direction
     */
    private void makeAnimation(List<Field> fieldsCopies, int direction) {
        if (direction == MOVE_UP) {
            animationUp(fieldsCopies);
        } else if (direction == MOVE_DOWN) {
            animationDown(fieldsCopies);
        } else if (direction == MOVE_LEFT) {
            animationLeft(fieldsCopies);
        } else if (direction == MOVE_RIGHT) {
            animationRight(fieldsCopies);
        }
    }

    // TODO: 29.07.2020 zrob tutaj ladne javadoci
    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation arg0) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        public void onAnimationEnd(Animation arg0) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    private TranslateAnimation prepareTranslateAnimation(View viewBeingAnimated, View viewBeingAnimatedTo) {
        TranslateAnimation translateAnimation =
                new TranslateAnimation(0, viewBeingAnimatedTo.getX() - viewBeingAnimated.getX(),
                        0, viewBeingAnimatedTo.getY() - viewBeingAnimated.getY());
        translateAnimation.setRepeatMode(0);
        translateAnimation.setDuration((long) (ANIM_SPEED_SECONDS * 1000));
        translateAnimation.setFillAfter(true);
        return translateAnimation;
    }


    private void startAnimation(List<Field> fieldCopies, List<TranslateAnimation> translateAnimationList) {
        List<Integer> amountsMoved = this.game.getAmountMovedList();
        for (int i = 0; i < this.gridView.getChildCount(); i++) {
            if (fieldCopies.get(i).getValue() != 0) {
                System.out.println(i);
                Animation translateAnimation = translateAnimationList.get(i);
                translateAnimation.setAnimationListener(animationListener);
                this.gridView.getChildAt(i).startAnimation(translateAnimation);
            }
        }
        this.prepareGrid();
        setFieldsImagesToZeros();
    }

    private void animationDown(List<Field> fieldCopies) {
        List<TranslateAnimation> translateAnimationList = new ArrayList<>();
        List<Integer> amountsMoved = this.game.getAmountMovedList();
        for (int i = this.gridView.getChildCount() - 1; i >= 0; i--) {
            if (fieldCopies.get(i).getValue() != 0) {
                View viewBeingAnimated = this.gridView.getChildAt(i);
                View viewBeingAnimatedTo = this.gridView.getChildAt(i + amountsMoved.get(i) * 4);
                translateAnimationList.add(prepareTranslateAnimation(viewBeingAnimated, viewBeingAnimatedTo));
            } else { ;
                translateAnimationList.add(null);
            }
        }
        Collections.reverse(translateAnimationList);
        startAnimation(fieldCopies, translateAnimationList);
    }


    private void animationUp(List<Field> fieldCopies) {
        List<TranslateAnimation> translateAnimationList = new ArrayList<>();
        List<Integer> amountsMoved = this.game.getAmountMovedList();
        for (int i = 0; i < this.gridView.getChildCount(); i++) {
            if (fieldCopies.get(i).getValue() != 0) {
                View viewBeingAnimated = this.gridView.getChildAt(i);
                View viewBeingAnimatedTo = this.gridView.getChildAt(i - amountsMoved.get(i) * 4);
                translateAnimationList.add(prepareTranslateAnimation(viewBeingAnimated, viewBeingAnimatedTo));
            } else {
                translateAnimationList.add(null);
            }
        }
        startAnimation(fieldCopies, translateAnimationList);
    }

    private void animationLeft(List<Field> fieldCopies) {
        List<TranslateAnimation> translateAnimationList = new ArrayList<>();
        List<Integer> amountsMoved = this.game.getAmountMovedList();
        for (int i = this.gridView.getChildCount() - 1; i >= 0; i--) {
            if (fieldCopies.get(i).getValue() != 0) {
                View viewBeingAnimated = this.gridView.getChildAt(i);
                View viewBeingAnimatedTo = this.gridView.getChildAt(i - amountsMoved.get(i));
                translateAnimationList.add(prepareTranslateAnimation(viewBeingAnimated, viewBeingAnimatedTo));
            } else {
                translateAnimationList.add(null);
            }
        }
        Collections.reverse(translateAnimationList);
        startAnimation(fieldCopies, translateAnimationList);
    }

    private void animationRight(List<Field> fieldCopies) {
        List<TranslateAnimation> translateAnimationList = new ArrayList<>();
        List<Integer> amountsMoved = this.game.getAmountMovedList();
        for (int i = 0; i < this.gridView.getChildCount(); i++) {
            if (fieldCopies.get(i).getValue() != 0) {
                View viewBeingAnimated = this.gridView.getChildAt(i);
                View viewBeingAnimatedTo = this.gridView.getChildAt(i + amountsMoved.get(i));
                translateAnimationList.add(prepareTranslateAnimation(viewBeingAnimated, viewBeingAnimatedTo));
            } else {
                translateAnimationList.add(null);
            }
        }
        startAnimation(fieldCopies, translateAnimationList);
    }

    /**
     * If the game's goal is achieved, the dialog is shown asking whether the user wants to continue the game or end it.
     */
    private void goalAchieved() {
        game.pauseTimer();
        AlertDialog.Builder builder = new AlertDialog.Builder(BoardActivity.this.boardActivity);
        builder.setMessage(R.string.goal_achieved_question)
                .setPositiveButton(R.string.continue_game_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        game.unpauseTimer();
                    }
                })
                .setNegativeButton(R.string.end_game_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        changeToEndActivity();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * Changes current activity to EndGameActivity.
     * Saves score, high score and user authentication to next activity.
     */
    private void changeToEndActivity() {
        Intent i = new Intent(BoardActivity.this.boardActivity, EndGame.class);
        i.putExtra(getResources().getString(R.string.score), Integer.toString(game.getScore()));
        i.putExtra(getResources().getString(R.string.high_score), Integer.toString(game.getHighScore()));
        i.putExtra(getResources().getString(R.string.authentication), Boolean.toString(game.isUserAuthenticated()));
        startActivity(i);
        // FIXME: 23.07.2020 TO JEST TAKIE XD ale no działa i dzięki temu się nie psuje display po koncu gry xD
        this.adapter = null;
        restartGame();
    }

    private void prepareSensors() {
        // Get accelerometer and magnetometer sensors from the sensor manager.
        // The getDefaultSensor() method returns null if the sensor
        // is not available on the device.
        mSensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        mSensorAccelerometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD);
        mSensorLight = mSensorManager.getDefaultSensor(
                Sensor.TYPE_LIGHT);
        mSensorProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            // TODO: 15.07.2020 rename all Runnable classes to things that make sense
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = event.values.clone();
                if (choosenSensors[0]) {
                    new Thread(new PositionGyroscope()).start();
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = event.values.clone();
                if (choosenSensors[1]) {
                    new Thread(new ChangeColourMagnetometer()).start();
                }
                break;
            case Sensor.TYPE_LIGHT:
                mLightData = event.values[0];
                if (choosenSensors[2]) {
                    new Thread(new DarkMode()).start();
                }
                break;
            case Sensor.TYPE_PROXIMITY:
                mProximityData = event.values[0];
                if (choosenSensors[3]) {
                    new Thread(new StopGameProximity()).start();
                }
                break;
            default:
                // FIXME: 15.07.2020, nie powinno być runtime ale nie chce dodawać throws wszędzie dla placeholdera
                throw new RuntimeException("PLACEHOLDER, onSensorChanged");
        }
    }

    private float[] magnetometerSetup() {
        float[] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix,
                null, mAccelerometerData, mMagnetometerData);
        float[] orientationValues = new float[3];
        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrix, orientationValues);
        }
        return orientationValues;
    }

    private class PositionGyroscope implements Runnable {
        @Override
        public void run() {
            float[] orientationValues = magnetometerSetup();
            // Azimuth: The direction (north/south/east/west) the device is pointing. 0 is magnetic north.
            // Pitch: The top-to-bottom tilt of the device. 0 is flat.
            // Roll: The left-to-right tilt of the device. 0 is flat.
            float azimuth = orientationValues[0];
            float pitch = orientationValues[1];
            float roll = orientationValues[2];

            // malutkie odchylenie -> zmiana na 0
            // nam to raczej nie potrzebne
            if (Math.abs(pitch) < VALUE_DRIFT) {
                pitch = 0;
            }
            if (Math.abs(roll) < VALUE_DRIFT) {
                roll = 0;
            }


            if (!hasMoved && (Math.abs(pitch) >= DETECT_MOVE_PITCH || Math.abs(roll) >= DETECT_MOVE_ROLL)) {
                final float finalPitch = pitch;
                final float finalRoll = roll;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalPitch >= DETECT_MOVE_PITCH) {
                            move(MOVE_UP);
                        } else if (finalPitch <= -DETECT_MOVE_PITCH) {
                            move(MOVE_DOWN);
                        } else if (finalRoll >= DETECT_MOVE_ROLL) {
                            move(MOVE_RIGHT);
                        } else if (finalRoll <= -DETECT_MOVE_ROLL) {
                            move(MOVE_LEFT);
                        }
                        hasMoved = true;
                    }
                });
            }
            if (Math.abs(pitch) < RESET_PITCH && Math.abs(roll) < RESET_ROLL) {
                hasMoved = false;
            }
        }
    }

    /**
     * Sets dark or light mode depending on the light level.
     */
    private class DarkMode implements Runnable {
        @Override
        public void run() {
            // Light Sensor - gdy jest ciemno włącza się dark mode
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mLightData <= DARKMODE_ENABLE_LIGHT && !isDarkTheme) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                darkThemeView.setImageResource(R.drawable.dark_theme_on);
                            }
                        });
                        isDarkTheme = true;
                        preferencesHelper.setDarkTheme(isDarkTheme);
                        adapter.notifyDataSetChanged();
                    } else if (mLightData >= DARKMODE_DISABLE_LIGHT && isDarkTheme) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                darkThemeView.setImageResource(R.drawable.dark_theme_off);
                            }
                        });
                        isDarkTheme = false;
                        preferencesHelper.setDarkTheme(isDarkTheme);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    /**
     * Pauses or unpauses game depending on proximity level.
     */
    private class StopGameProximity implements Runnable {
        @Override
        public void run() {
            // Proximity sensor - zatrzymuje sie czas po zblizeniu
            if (mProximityData < PROXIMITY_DISTANCE) {
                if (!game.isSuspended()) {
                    game.pauseTimer();
                }
            } else if (game.isSuspended()) {
                game.unpauseTimer();
            }
        }
    }

    /**
     * Changes fields background colour depending on magnetometer value.
     */
    private class ChangeColourMagnetometer implements Runnable {
        @Override
        public void run() {
            float[] orientationValues = magnetometerSetup();
            final float azimuth = orientationValues[0];
            final float pitch = orientationValues[1];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pitch > HORIZONTAL_PITCH_MIN && pitch < HORIZONTAL_PITCH_MAX) {
                        if (azimuth >= changeColourAzimunthBreakpoint1 && azimuth < changeColourAzimunthBreakpoint2) {
                            mThumbIds = R.drawable.button_green;
                        } else if (azimuth >= changeColourAzimunthBreakpoint3 && azimuth < changeColourAzimunthBreakpoint4) {
                            mThumbIds = R.drawable.button_green_light;
                        } else if (azimuth >= -changeColourAzimunthBreakpoint4 && azimuth < -changeColourAzimunthBreakpoint3) {
                            mThumbIds = R.drawable.button_blue;
                        } else if (azimuth > -changeColourAzimunthBreakpoint2 && azimuth < -changeColourAzimunthBreakpoint1) {
                            mThumbIds = R.drawable.button_blue_light;
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    /**
     * Restarts game and TextViews.
     */
    private void restartGame() {
        this.game.restartGame();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Pressing back button will change current activity to main activity.
     * If the user is not authenticated dialogue is shown to warn the user that the game will not be saved.
     */
    @Override
    public void onBackPressed() {
        if (!game.isUserAuthenticated()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BoardActivity.this.boardActivity);
            builder.setMessage(R.string.dialog_back_question)
                    .setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            BoardActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * On activity stop media player is released, game is paused and updating time is stopped.
     */
    @Override
    protected void onStop() {
        super.onStop();
        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        mSensorManager.unregisterListener(boardActivity);
        this.game.pauseTimer();
        if (!this.updateTimeThread.isInterrupted()) {
            this.updateTimeThread.interrupt();
        }
        this.mediaPlayer.release();
    }
}


