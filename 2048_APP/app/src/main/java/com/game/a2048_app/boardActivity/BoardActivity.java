package com.game.a2048_app.boardActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.game.a2048_app.EndGame;
import com.game.a2048_app.OnSwipeTouchListener;
import com.game.a2048_app.R;
import com.game.a2048_app.boardActivity.Sensors.DarkMode;
import com.game.a2048_app.boardActivity.Sensors.OrientationSensors;
import com.game.a2048_app.boardActivity.Sensors.StopGameProximity;
import com.game.a2048_app.boardActivity.buttons.UndoButton;
import com.game.a2048_app.helpers.DarkModeHelper;
import com.game.a2048_app.helpers.PreferencesHelper;
import com.game.a2048_app.helpers.Preloader;
import com.game.a2048_app.helpers.SoundPlayer;
import com.game.module.Field;
import com.game.module.Game;
import com.game.module.exceptions.GameOverException;
import com.game.module.exceptions.GoalAchievedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.game.a2048_app.boardActivity.buttons.SettingsButton.chosenSensors;

public class BoardActivity extends AppCompatActivity implements BoardActivityListener {

    // TODO: 16.08.2020 usunac i uzywac tej flagi, z jakiegoś powodu nie umiem flagowa

    private Game game;
    private ArrayAdapter<Drawable> adapter;
    private GridView gridView;
    private Field[] fields;
    private Drawable[] fieldsImages;
    private Drawable[] fieldsBackground;
    private Preloader preloader = Preloader.getInstance();
    private Drawable mThumbIds = preloader.getButtonBlue();
    private OrientationSensors orientationSensors;
    private StopGameProximity stopGameProximity;
    private DarkMode darkMode;

    // System sensor manager instance.
    private SensorManager mSensorManager;

    // Accelerometer and magnetometer sensors, as retrieved from the
    // sensor manager.
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;
    private Sensor mSensorLight;
    private Sensor mSensorProximity;
    private Sensor mSensorGyroscope;

    private TextView textScore;
    private TextView textHighScore;
    private TextView textTime;

    private Button pausePlayButton;
    private UndoButton undoButton;

    private Thread updateTimeThread;

    private final static double ANIM_SPEED_SECONDS = 0.2;


    private final PreferencesHelper preferencesHelper = PreferencesHelper.getInstance();


    /**
     * {@inheritDoc}
     */
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    // https://developer.android.com/guide/components/activities/activity-lifecycle
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesHelper.initContext(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_board);
        this.loadData();
        this.prepareViews();
        this.prepareSensors();
    }

    /**
     * Loads settings, game and grid drawable resources.
     */
    private void loadData() {
        preferencesHelper.getChoosenSensors(chosenSensors);
        this.game = new Game(getIntent().getBooleanExtra(getResources().getString(R.string.authentication), false), this);
        this.fields = game.getBoard().toArray(new Field[0]);
        this.fieldsImages = new Drawable[fields.length];
        this.fieldsBackground = new Drawable[fields.length];
        Arrays.fill(fieldsImages, preloader.getZero());
        Arrays.fill(fieldsBackground, mThumbIds);
        this.orientationSensors = new OrientationSensors(this);
        this.stopGameProximity = new StopGameProximity(this, this.game);
        this.darkMode = new DarkMode(this);
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

        this.undoButton = (UndoButton) findViewById(R.id.undoMoveButton);
        this.undoButton.setGame(this.game);

        this.pausePlayButton = (Button) findViewById(R.id.pausePlayButton);

        DarkModeHelper.setTheme((ImageView) findViewById(R.id.darkThemeView));

        this.adapter.notifyDataSetChanged();
    }

    /**
     * Prepares grid for game's board.
     * Creates array adapter.
     * Converts each textView to viewHolder and sets their background and numbers resources.
     */
    private void prepareGrid() {
        this.adapter = new ArrayAdapter<Drawable>(this,
                android.R.layout.simple_list_item_1, fieldsImages) {
            /**
             * {@inheritDoc}
             */
            // TODO: 29.07.2020 nie wiem jak to ladnie opisac ಠ_ಠ
            class ViewHolderItem {
                TextView textViewItem;
                ImageView imageViewItem;
            }

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
                viewHolder.textViewItem.setBackground(fieldsBackground[position]);
                viewHolder.imageViewItem.setTag(position);
                viewHolder.imageViewItem.setImageDrawable(fieldsImages[position]);
                return convertView;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void notifyDataSetChanged() {
                if ((getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) == 0) {
                    setFieldsImages();
                    setFieldsBackground();
                    super.notifyDataSetChanged();
                }
            }
        };
        gridView.setAdapter(adapter);
    }

    /**
     * Sets swipe touch listener.
     * Each swipe is responsible for the appropriate movement in game.
     */
    private void setupSwipeListener() {
        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(this, this.gridView);
        final BoardActivity boardActivity = this;
        onSwipeTouchListener.onSwipe = new OnSwipeTouchListener.onSwipeListener() {
            @Override
            public void swipeRight() {
                boardActivity.move(Game.MOVE_RIGHT);
            }

            @Override
            public void swipeTop() {
                boardActivity.move(Game.MOVE_UP);
            }

            @Override
            public void swipeBottom() {
                boardActivity.move(Game.MOVE_DOWN);
            }

            @Override
            public void swipeLeft() {
                boardActivity.move(Game.MOVE_LEFT);
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

    public void restartGameButtonOnClick(View v) {
        SoundPlayer soundPlayer = SoundPlayer.getInstance();
        soundPlayer.playSound(soundPlayer.getAsset(getApplicationContext(), R.raw.restart));
        new AlertDialogRestartGame(this).show();
    }

    /**
     * Creates button on click listener to pause or unpause game.
     * Play sound after click and change button's image.
     */
    public void playPauseButtonOnClick(View v) {
        SoundPlayer soundPlayer = SoundPlayer.getInstance();
        soundPlayer.playSound(soundPlayer.getAsset(getApplicationContext(), R.raw.pause));
        if (!game.isSuspended()) {
            pauseGameWithButton();
        } else {
            unPauseGameWithButton();
        }
    }

    private void pauseGameWithButton() {
        game.pauseTimer();
        pausePlayButton.setBackground(preloader.getPausePlayOn());
    }

    private void unPauseGameWithButton() {
        game.unPauseTimer();
        pausePlayButton.setBackground(preloader.getPausePlayOff());
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
        if (mSensorGyroscope != null) {
            mSensorManager.registerListener(orientationSensors, mSensorGyroscope,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(orientationSensors, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(orientationSensors, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        if (mSensorLight != null) {
            mSensorManager.registerListener(darkMode, mSensorLight,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        if (mSensorProximity != null) {
            mSensorManager.registerListener(stopGameProximity, mSensorProximity,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        unPauseGameWithButton();
        this.beginUpdatingTime();
    }

    // TODO: 09.08.2020 JAVADOC - to jest listener na przyciski
    @Override
    public void callback(View view, String result) {
        if (result.equals(getString(R.string.Undo_Succeed))) {
            adapter.notifyDataSetChanged();
            setScoreTexts();
        }
    }

    // TODO: 09.08.2020 chcialbym uzyc swita ale sie nie da i wgl no jakos ladniej bym chcial xd
    //  może zrobić kilka listenerów i każdy będzie miał osobny callback czy coś
    //  a moze tak zostawic
    @Override
    public void callback(String result) {
        if (result.equals(getString(R.string.Button_Green))) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mThumbIds = preloader.getButtonGreen();
                }
            });
        } else if (result.equals(getString(R.string.Button_Green_Light))) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mThumbIds = preloader.getButtonGreenLight();
                }
            });
        } else if (result.equals(getString(R.string.Button_Blue))) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mThumbIds = preloader.getButtonBlue();
                }
            });
        } else if (result.equals(getString(R.string.Button_Blue_Light))) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mThumbIds = preloader.getButtonBlueLight();
                }
            });
        } else if (result.equals(getString(R.string.Notify_Adapter))) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        } else if (result.equals(getString(R.string.SetTheme))) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DarkModeHelper.setTheme((ImageView) findViewById(R.id.darkThemeView));
                }
            });
        } else if (result.equals(getString(R.string.SetPauseOn))) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pauseGameWithButton();
                }
            });
        } else if (result.equals(getString(R.string.SetPauseOff))) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    unPauseGameWithButton();
                }
            });
        }
        if ((getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) == 0) {
            if (result.equals(getString(R.string.MoveUP))) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        move(Game.MOVE_UP);
                    }
                });
            } else if (result.equals(getString(R.string.MoveDown))) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        move(Game.MOVE_DOWN);
                    }
                });
            } else if (result.equals(getString(R.string.MoveLeft))) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        move(Game.MOVE_LEFT);
                    }
                });
            } else if (result.equals(getString(R.string.MoveRight))) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        move(Game.MOVE_RIGHT);
                    }
                });
            }
        }

    }


    /**
     * Sets all field's images to transparent.
     */
    private void setFieldsImagesToZeros() {
        for (int i = 0; i < fields.length; i++) {
            fieldsImages[i] = preloader.getZero();
            fieldsBackground[i] = mThumbIds;
        }
    }

    /**
     * Ordered list of all Drawables used to display game values.
     * Correct drawable for each value is found at the index of the list equal to log base 2 of the value of the field.
     */
    private final List<Drawable> fieldImagesDrawablesList = new ArrayList<>(
            Arrays.asList(
                    preloader.getZero(),
                    preloader.getTwo(),
                    preloader.getFour(),
                    preloader.getEight(),
                    preloader.getSixteen(),
                    preloader.getThirtyTwo(),
                    preloader.getSixtyFour(),
                    preloader.getOneHundred(),
                    preloader.getTwoHundred(),
                    preloader.getFiveHundred(),
                    preloader.getOneThousand(),
                    preloader.getTwoThousand(),
                    preloader.getFourThousand(),
                    preloader.getEightThousand(),
                    preloader.getSixteenThousand(),
                    preloader.getThirtyTwoThousand(),
                    preloader.getSixtyFiveThousand(),
                    preloader.getOneHundredThousand())
    );


    /**
     * Checks if param is power of number_two.
     *
     * @param x value to check.
     * @return if param is power of number_two.
     */
    private boolean isPowerOfTwo(int x) {
        return (x & (x - 1)) == 0;
    }

    /**
     * Sets field's image based on current field's value.
     */
    private void setFieldsImages() {
        for (int i = 0; i < fields.length; i++) {
            int fieldValue = fields[i].getValue();
            if (isPowerOfTwo(fieldValue)) {
                if (fieldValue == 0) {
                    fieldsImages[i] = fieldImagesDrawablesList.get(0);
                } else {
                    fieldsImages[i] = fieldImagesDrawablesList.get((int) (Math.log(fieldValue) / Math.log(2)));
                }
            }
        }
    }

    /**
     * Sets field's background based on current field's value.
     */
    private void setFieldsBackground() {
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getValue() == 0) {
                fieldsBackground[i] = mThumbIds;
            } else {
                if (mThumbIds == preloader.getButtonGreenLight()) {
                    fieldsBackground[i] = preloader.getButtonGreen();
                } else if (mThumbIds == preloader.getButtonGreen()) {
                    fieldsBackground[i] = preloader.getButtonGreenLight();
                } else if (mThumbIds == preloader.getButtonBlueLight()) {
                    fieldsBackground[i] = preloader.getButtonBlue();
                } else if (mThumbIds == preloader.getButtonBlue()) {
                    fieldsBackground[i] = preloader.getButtonBlueLight();
                }
            }
        }
    }

    /**
     * Updates game time every 100 milliseconds.
     */
    private void beginUpdatingTime() {
        setTimeText();
        updateTimeThread = new Thread(updateTimeRunnable);
        updateTimeThread.start();
    }

    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(100);
                    if (textTime.getText().length() <= 8 || textTime.getText().subSequence(textTime.getText().length() - 8, textTime.getText().length()) != game.getElapsedTimeToString()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setTimeText();
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

    private void setTimeText() {
        textTime.setText(String.format("%s:\n%s", getResources().getString(R.string.time), game.getElapsedTimeToString()));
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

    private boolean isGameOver = false;

    private void move(int direction) {
        List<Field> fieldsCopies = game.getCopyOfTheBoard();
        // TODO: 16.08.2020 sprawdzic czy to cokolwiek daje
        try {
            game.move(direction);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (GameOverException e) {
            e.printStackTrace();
            this.isGameOver = true;
            changeToEndActivity();
        } catch (GoalAchievedException e) {
            e.printStackTrace();
            goalAchieved();
        } finally {
            updateActivityAfterMove(fieldsCopies, direction);
        }
    }


    private void updateActivityAfterMove(List<Field> fieldsCopies, int direction) {
        this.animate(fieldsCopies, direction);
        if (!isGameOver) {
            this.setScoreTexts();
            undoButton.setUndoNumber();
        }
    }


    // TODO: 29.07.2020 zrob tutaj ladne javadoci
    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onAnimationStart(Animation arg0) {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onAnimationEnd(Animation arg0) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            adapter.notifyDataSetChanged();
        }

        /**
         * {@inheritDoc}
         */
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
        for (int i = 0; i < this.gridView.getChildCount(); i++) {
            if (fieldCopies.get(i).getValue() != 0) {
                Animation translateAnimation = translateAnimationList.get(i);
                translateAnimation.setAnimationListener(animationListener);
                this.gridView.getChildAt(i).startAnimation(translateAnimation);
            }
        }
        setFieldsImagesToZeros();
        this.prepareGrid();
    }

    private View getViewBeingAnimatedTo(int direction, int viewBeingAnimatedIndex, int amountMoved) {
        View returnView;
        switch (direction) {
            case Game.MOVE_RIGHT:
                returnView = this.gridView.getChildAt(viewBeingAnimatedIndex + amountMoved);
                break;
            case Game.MOVE_DOWN:
                returnView = this.gridView.getChildAt(viewBeingAnimatedIndex + amountMoved * 4);
                break;
            case Game.MOVE_LEFT:
                returnView = this.gridView.getChildAt(viewBeingAnimatedIndex - amountMoved);
                break;
            case Game.MOVE_UP:
                returnView = this.gridView.getChildAt(viewBeingAnimatedIndex - amountMoved * 4);
                break;
            default:
                throw new IllegalArgumentException("Value can only be equal to 0, 1, 2 or 3");
        }
        return returnView;
    }

    private void animate(List<Field> fieldCopies, int direction) {
        List<TranslateAnimation> translateAnimationList = new ArrayList<>();
        List<Integer> amountsMoved = this.game.getAmountMovedList();
        for (int i = 0; i < this.gridView.getChildCount(); i++) {
            if (fieldCopies.get(i).getValue() != 0) {
                View viewBeingAnimated = this.gridView.getChildAt(i);
                View viewBeingAnimatedTo = getViewBeingAnimatedTo(direction, i, amountsMoved.get(i));
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
        this.pauseGameWithButton();
        new AlertDialogGoalAchieved(this).show();
    }

    private class AlertDialogGoalAchieved extends Dialog {
        /**
         * {@inheritDoc}
         */
        public AlertDialogGoalAchieved(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_alert_dialog);
            findViewById(R.id.alert_dialog_button_yes).setOnClickListener(listenerYes);
            findViewById(R.id.alert_dialog_button_no).setOnClickListener(listenerNo);
            ((TextView) findViewById(R.id.alert_dialog_text)).setText(getText(R.string.goal_achieved_question));
            Objects.requireNonNull(this.getWindow()).getDecorView().setBackgroundColor(Color.TRANSPARENT);
        }

        public View.OnClickListener listenerYes = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unPauseGameWithButton();
                dismiss();
            }
        };

        public View.OnClickListener listenerNo = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToEndActivity();
                dismiss();
            }
        };
    }

    /**
     * Changes current activity to EndGameActivity.
     * Saves score, high score and user authentication to next activity.
     */
    private void changeToEndActivity() {
        Intent i = new Intent(BoardActivity.this, EndGame.class);
        i.putExtra(getResources().getString(R.string.score), Integer.toString(game.getScore()));
        i.putExtra(getResources().getString(R.string.high_score), Integer.toString(game.getHighScore()));
        i.putExtra(getResources().getString(R.string.authentication), game.isUserAuthenticated());
        adapter = null;
        game.restartGame();
        startActivity(i);
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
        mSensorGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

    }

    /**
     * Restarts game and reloads activity.
     */
    private void restartGame() {
        game.restartGame();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }


    private class AlertDialogRestartGame extends Dialog {
        /**
         * {@inheritDoc}
         */
        public AlertDialogRestartGame(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_alert_dialog);
            findViewById(R.id.alert_dialog_button_yes).setOnClickListener(listenerYes);
            findViewById(R.id.alert_dialog_button_no).setOnClickListener(listenerNo);
            this.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        }

        public View.OnClickListener listenerYes = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
                dismiss();
            }
        };

        public View.OnClickListener listenerNo = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        };
    }

    /**
     * {@inheritDoc}
     * Pressing back button will change current activity to main activity.
     * If the user is not authenticated a dialogue is shown to warn the user that the game will not be saved.
     */
    @Override
    public void onBackPressed() {
        if (!game.isUserAuthenticated()) {
            new AlertDialogOnBackPressed(this).show();
        } else {
            super.onBackPressed();
        }
    }

    private class AlertDialogOnBackPressed extends Dialog {
        /**
         * {@inheritDoc}
         */
        public AlertDialogOnBackPressed(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_alert_dialog);
            findViewById(R.id.alert_dialog_button_yes).setOnClickListener(listenerYes);
            findViewById(R.id.alert_dialog_button_no).setOnClickListener(listenerNo);
            ((TextView) findViewById(R.id.alert_dialog_text)).setText(getText(R.string.dialog_back_question));
            Objects.requireNonNull(this.getWindow()).getDecorView().setBackgroundColor(Color.TRANSPARENT);
        }

        public View.OnClickListener listenerYes = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoardActivity.super.onBackPressed();
                dismiss();
            }
        };

        public View.OnClickListener listenerNo = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        };
    }

    /**
     * Unregisters listeners for sensors.
     */
    private void unregisterSensors() {
        mSensorManager.unregisterListener(darkMode);
        mSensorManager.unregisterListener(stopGameProximity);
        mSensorManager.unregisterListener(orientationSensors);
    }

    /**
     * {@inheritDoc}
     * On activity stop game is paused and updating time is stopped.
     */
    @Override
    protected void onStop() {
        super.onStop();
        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        this.game.pauseTimer();
        if (!this.updateTimeThread.isInterrupted()) {
            this.updateTimeThread.interrupt();
        }
        unregisterSensors();
    }
}