package com.game.a2048_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import com.game.module.Field;
import com.game.module.Game;
import com.game.module.GameOverException;
import com.game.module.GoalAchievedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// TODO: 20.07.2020 dzwiek: http://drpetter.se/project_sfxr.html

// TODO: 09.06.2020 wcale nie julaszym1212 oto animacja:
//  https://stackoverflow.com/questions/46359987/which-layoutmanager-for-the-animations-of-a-2048-game
//  https://github.com/wasabeef/recyclerview-animators - to chyba najlepsze
//  https://www.youtube.com/watch?v=33wOlQ2y0hQ - tez git
//  https://stackoverflow.com/questions/30997624/how-to-apply-animation-to-add-item-in-gridview-one-by-one

public class BoardActivity extends AppCompatActivity implements SensorEventListener {
    private static final float VALUE_DRIFT = 0.05f;

    OnSwipeTouchListener onSwipeTouchListener;

    private Game game;
    private ArrayAdapter<Integer> adapter;
    private GridView gridView;
    private ImageView darkThemeView;
    private ImageView scoreBoard;
    private Field[] fields;
    private Integer[] fieldsImages;
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

    // TODO: 15.07.2020 binding podwojny
    //  https://developer.android.com/topic/libraries/data-binding/two-way?fbclid=IwAR3nCMsvlFlrsTQVVEvW-Sk9wxKMeOh2HJm_XUM9BJNlJW9ZFFeH-26kXFM
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

    // FIXME: 12.07.2020 bad naming
    private final static float RESET_PITCH = 0.2f;
    private final static float RESET_ROLL = 0.2f;

    private final static float DETECT_MOVE_PITCH = 0.7f;
    private final static float DETECT_MOVE_ROLL = 0.7f;

    // FIXME: 12.07.2020 bad naming
    private final static double changeColourAzimunthBreakpoint1 = 0.25;
    private final static double changeColourAzimunthBreakpoint2 = 1.25;
    private final static double changeColourAzimunthBreakpoint3 = 1.75;
    private final static double changeColourAzimunthBreakpoint4 = 2.75;

    private final static int DARKMODE_ENABLE_LIGHT = 30;
    private final static int DARKMODE_DISABLE_LIGHT = 50;


    // znowu się spotykamy
    public final static int MOVE_UP = Game.MOVE_UP;
    public final static int MOVE_RIGHT = Game.MOVE_RIGHT;
    public final static int MOVE_DOWN = Game.MOVE_DOWN;
    public final static int MOVE_LEFT = Game.MOVE_LEFT;

    private boolean hasMoved = false;

    // settings
    private SharedPreferences preferences;
    private boolean isDarkTheme = false;

    private BoardActivity boardActivity = this;

    // TODO: 26.07.2020 https://stackoverflow.com/a/15189123 - tak zrobic dla każdego mediaPlayera
//    private MediaPlayer mediaPlayerPause;
//    private MediaPlayer mediaPlayerRestart;
//    private MediaPlayer mediaPlayerSettings;
    private MediaPlayer mediaPlayer;


    // TODO: 25.07.2020 blokada interakcji z użytkownikiem
    //  https://stackoverflow.com/questions/36918219/how-to-disable-user-interaction-while-progressbar-is-visible-in-android

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    // https://developer.android.com/guide/components/activities/activity-lifecycle
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_board);
        this.loadData();
        this.prepareViews();
        this.prepareSensors();
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
    }

    @Override
    public void onBackPressed() {
        if (!game.isUserAuthenticated()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BoardActivity.this);
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

    private void loadData() {
        // wolał bym przypisać preferences w jednym miejcu zamiasty dwu ale się z jakiegoś powodu psuje :/
        preferences = getSharedPreferences(getResources().getString(R.string.settings), MODE_PRIVATE);
        String[] sensorNames = getResources().getStringArray(R.array.sensors);
        for (int i = 0; i < this.choosenSensors.length; i++) {
            this.choosenSensors[i] = preferences.getBoolean(sensorNames[i], false);
        }
        this.isDarkTheme = preferences.getBoolean(getResources().getString(R.string.dark_theme), false);
        this.game = new Game(Boolean.parseBoolean(getIntent().getStringExtra(getResources().getString(R.string.authentication))), this);
        this.fields = game.getBoard().toArray(new Field[0]);
        this.fieldsImages = new Integer[fields.length];
        Arrays.fill(fieldsImages, R.drawable.zero);
    }

    void setTextScoreText() {
        textScore.setText(String.format("%s:\n%s", getResources().getString(R.string.score), game.getCurrentScore()));

    }

    void setTextHighScoreText() {
        if (game.isUserAuthenticated()) {
            textHighScore.setText(String.format("%s:\n%s", getResources().getString(R.string.high_score), game.getHighScore()));
        }
    }

    private void prepareViews() {
        gridView = (GridView) findViewById(R.id.gridView);
        this.prepareGrid();
        this.setupSwipeListener();
        textTime = (TextView) findViewById(R.id.time);
        prepareScoreText();
        prepareHighscoreText();
        restartGameButton = (Button) findViewById(R.id.restartGameButton);
        this.restartGameButton.setOnClickListener(restartGameListener);
        this.settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(settingsListener);
        undoButton = (Button) findViewById(R.id.undoMoveButton);
        undoButton.setOnClickListener(undoListener);
        pausePlayButton = (Button) findViewById(R.id.pausePlayButton);
        pausePlayButton.setOnClickListener(playPauseListener);
        darkThemeView = (ImageView) findViewById(R.id.darkThemeView);
        scoreBoard = (ImageView) findViewById(R.id.scoreBoard);
        undoTextView = (TextView) findViewById(R.id.undoTextView);
        muteButton = (Button) findViewById(R.id.muteButton);
        muteButton.setOnClickListener(muteListener);
        setUndoAmount();
        this.setTheme();
        adapter.notifyDataSetChanged();
    }

//    private View.OnClickListener muteListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            muteButton.setBackgroundResource(R.drawable.undo_clicked);
//            mediaPlayerUndo.start();
//            game.undoPreviousMove();
//            adapter.notifyDataSetChanged();
//            setScoreTexts();
//        }
//    };

    private void setTheme() {
        if (this.isDarkTheme) {
            darkThemeView.setImageResource(R.drawable.dark_theme_on);
        } else {
            darkThemeView.setImageResource(R.drawable.dark_theme_off);
        }
        // TODO: 23.07.2020 przeniosłem to do .xml, mam nadzieję że działa.
        //  jeśli działa to wszystko trzeba do .xml przenieść żeby był porządek
    }

    private View.OnClickListener restartGameListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            restartGameButton.setBackgroundResource(R.drawable.main_activity_button_clicked);
            Uri uri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, R.raw.restart);
            try {
                mediaPlayer.setDataSource(getApplicationContext(), uri);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    restartGameButton.setBackgroundResource(R.drawable.main_activity_button);
                    mp.release();
                }

            });
            restartGame();
        }
    };

    private View.OnClickListener undoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            undoButton.setBackgroundResource(R.drawable.undo_clicked);
            Uri uri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, R.raw.undo);
            try {
                mediaPlayer.setDataSource(getApplicationContext(), uri);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    setUndoAmount();
                    mp.release();
                }

            });
            game.undoPreviousMove();
            adapter.notifyDataSetChanged();
            setScoreTexts();
        }
    };

    private void setUndoAmount() {
        int undoAmount = BoardActivity.this.game.getAvaiableUndoAmount();
        System.out.println(undoAmount);
        if (undoAmount > 0) {
            undoButton.setBackgroundResource(R.drawable.undo);
            undoTextView.setText(String.format("%d", undoAmount));
            undoButton.setEnabled(true);
        } else if (undoAmount == 0) {
            undoButton.setBackgroundResource(R.drawable.undo_clicked);
            undoTextView.setText(String.format("%d", undoAmount));
            undoButton.setEnabled(false);
        } else {
            // TODO: 26.07.2020 tutaj jakis exception
        }
    }

    private View.OnClickListener settingsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            settingsButton.setBackgroundResource(R.drawable.settings_clicked);
            Uri uri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, R.raw.button_no_reverb);
            try {
                mediaPlayer.setDataSource(getApplicationContext(), uri);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(BoardActivity.this);
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
                    preferences = getSharedPreferences(getResources().getString(R.string.settings), MODE_PRIVATE);
                    String[] sensorNames = getResources().getStringArray(R.array.sensors);
                    SharedPreferences.Editor editor = preferences.edit();
                    for (int i = 0; i < choosenSensors.length; i++) {
                        editor.putBoolean(sensorNames[i], choosenSensors[i]);
                    }
                    settingsButton.setBackgroundResource(R.drawable.settings);
                    editor.apply();
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

    private View.OnClickListener playPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Uri uri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, R.raw.pause);
            try {
                mediaPlayer.setDataSource(getApplicationContext(), uri);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!game.isSuspended()) {
                game.pauseTimer();
                pausePlayButton.setBackgroundResource(R.drawable.pause_play_on);
            } else {
                game.unpauseTimer();
                pausePlayButton.setBackgroundResource(R.drawable.pause_play);
            }
        }
    };

    private void prepareScoreText() {
        textScore = (TextView) findViewById(R.id.score);
        this.setTextScoreText();
    }

    private void prepareHighscoreText() {
        textHighScore = (TextView) findViewById(R.id.highScore);
        this.setTextHighScoreText();
    }

    private void prepareSensors() {
        // Get accelerometer and magnetometer sensors from the sensor manager.
        // The getDefaultSensor() method returns null if the sensor
        // is not available on the device.
        mSensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD);
        mSensorLight = mSensorManager.getDefaultSensor(
                Sensor.TYPE_LIGHT);
        mSensorProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }


    private void prepareGrid() {
        this.adapter = new ArrayAdapter<Integer>(this,
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
                viewHolder.textViewItem.setBackgroundResource(mThumbIds);
                viewHolder.imageViewItem.setTag(position);
                viewHolder.imageViewItem.setImageResource(fieldsImages[position]);
                return convertView;
            }


            @Override
            public void notifyDataSetChanged() {
                setFieldsImages();
                super.notifyDataSetChanged();
            }
        };
        gridView.setAdapter(adapter);
    }

    private class ViewHolderItem {
        TextView textViewItem;
        ImageView imageViewItem;

        public ImageView getImageViewItem() {
            return imageViewItem;
        }

        public TextView getTextViewItem() {
            return textViewItem;
        }
    }


    private void setFieldsImagesToZeros() {
        for (int i = 0; i < fields.length; i++) {
            fieldsImages[i] = R.drawable.zero;
        }
    }

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
                    // TODO: 26.07.2020 czy to na pewno jest milion julka? xd
                    fieldsImages[i] = R.drawable.one_million;
                    break;
            }

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
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        if (mSensorLight != null) {
            mSensorManager.registerListener(this, mSensorLight,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        if (mSensorProximity != null) {
            mSensorManager.registerListener(this, mSensorProximity,
                    SensorManager.SENSOR_DELAY_GAME);
        }

        this.game.unpauseTimer();
        this.beginUpdatingTime();
        this.initMediaPlayer();
    }

    private void beginUpdatingTime() {
        updateTimeThread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(200);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textTime.setText(String.format("%s:\n%s", getResources().getString(R.string.time), game.getElapsedTimeToString()));
                            }
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                }
            }
        };
        updateTimeThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        mSensorManager.unregisterListener(this);
        this.game.pauseTimer();
        this.updateTimeThread.interrupt();
        this.mediaPlayer.release();
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

    // TODO: 16.07.2020 add binding so this is not needed
    void setScoreTexts() {
        this.setTextScoreText();
        this.setTextHighScoreText();
    }

    private void move(int direction) {
        List<Field> fieldsCopies = game.getCopyOfTheBoard();
        try {
            game.move(direction);
            if (direction == MOVE_UP) {
                animUP(fieldsCopies);
            } else if (direction == MOVE_DOWN) {
                animDown(fieldsCopies);
            } else if (direction == MOVE_LEFT) {
                animLeft(fieldsCopies);
            } else if (direction == MOVE_RIGHT) {
                animRight(fieldsCopies);
            }
        } catch (GameOverException e) {
            e.printStackTrace();
            changeToEndActivity();
        } catch (GoalAchievedException e) {
            e.printStackTrace();
            goalAchieved();
        }
        this.setScoreTexts();
        setUndoAmount();
    }

    private final static double ANIM_SPEED_SECONDS = 0.25;

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
        for (int i = 0; i < this.gridView.getChildCount(); i++) {
            if (fieldCopies.get(i).getValue() != 0) {
                Animation translateAnimation = translateAnimationList.get(i);
                translateAnimation.setAnimationListener(animationListener);
                this.gridView.getChildAt(i).startAnimation(translateAnimation);
            }
        }
        this.prepareGrid();
        Animation fadeOut = AnimationUtils.loadAnimation(BoardActivity.this, R.anim.fade_out);
        for (int i = this.gridView.getChildCount() - 1; i >= 0; i--) {
            this.gridView.getChildAt(i).startAnimation(fadeOut);
        }
        setFieldsImagesToZeros();
        Animation fadeIn = AnimationUtils.loadAnimation(BoardActivity.this, R.anim.fade_in);
        for (int i = this.gridView.getChildCount() - 1; i >= 0; i--) {
            this.gridView.getChildAt(i).startAnimation(fadeIn);
        }
    }

    private void animDown(List<Field> fieldCopies) {
        List<TranslateAnimation> translateAnimationList = new ArrayList<>();
        List<Integer> amountsMoved = this.game.getAmountMovedList();
        for (int i = this.gridView.getChildCount() - 1; i >= 0; i--) {
            this.gridView.setZ(i);
            View viewBeingAnimated = this.gridView.getChildAt(i);
            View viewBeingAnimatedTo = this.gridView.getChildAt(i + amountsMoved.get(i) * 4);
            translateAnimationList.add(prepareTranslateAnimation(viewBeingAnimated, viewBeingAnimatedTo));
        }
        Collections.reverse(translateAnimationList);
        startAnimation(fieldCopies, translateAnimationList);
    }

    private void animUP(List<Field> fieldCopies) {
        List<TranslateAnimation> translateAnimationList = new ArrayList<>();
        List<Integer> amountsMoved = this.game.getAmountMovedList();
        for (int i = 0; i < this.gridView.getChildCount(); i++) {
            this.gridView.setZ(i);
            View viewBeingAnimated = this.gridView.getChildAt(i);
            View viewBeingAnimatedTo = this.gridView.getChildAt(i - amountsMoved.get(i) * 4);
            translateAnimationList.add(prepareTranslateAnimation(viewBeingAnimated, viewBeingAnimatedTo));
        }
        startAnimation(fieldCopies, translateAnimationList);
    }

    private void animLeft(List<Field> fieldCopies) {
        List<TranslateAnimation> translateAnimationList = new ArrayList<>();
        List<Integer> amountsMoved = this.game.getAmountMovedList();
        for (int i = this.gridView.getChildCount() - 1; i >= 0; i--) {
            this.gridView.setZ(i);
            View viewBeingAnimated = this.gridView.getChildAt(i);
            View viewBeingAnimatedTo = this.gridView.getChildAt(i - amountsMoved.get(i));
            translateAnimationList.add(prepareTranslateAnimation(viewBeingAnimated, viewBeingAnimatedTo));
        }
        Collections.reverse(translateAnimationList);
        startAnimation(fieldCopies, translateAnimationList);
    }

    private void animRight(List<Field> fieldCopies) {
        List<TranslateAnimation> translateAnimationList = new ArrayList<>();
        List<Integer> amountsMoved = this.game.getAmountMovedList();
        for (int i = 0; i < this.gridView.getChildCount(); i++) {
            this.gridView.setZ(i);
            View viewBeingAnimated = this.gridView.getChildAt(i);
            View viewBeingAnimatedTo = this.gridView.getChildAt(i + amountsMoved.get(i));
            translateAnimationList.add(prepareTranslateAnimation(viewBeingAnimated, viewBeingAnimatedTo));
        }
        startAnimation(fieldCopies, translateAnimationList);
    }

    private void changeToEndActivity() {
        Intent i = new Intent(BoardActivity.this, EndGame.class);
        i.putExtra(getResources().getString(R.string.score), Integer.toString(game.getCurrentScore()));
        i.putExtra(getResources().getString(R.string.high_score), Integer.toString(game.getHighScore()));
        i.putExtra(getResources().getString(R.string.authentication), Boolean.toString(game.isUserAuthenticated()));
        startActivity(i);
        // FIXME: 23.07.2020 TO JEST TAKIE XD ale no działa i dzięki temu się nie psuje display po koncu gry xD
        this.adapter = null;
        restartGame();
    }

    private void goalAchieved() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BoardActivity.this);
        builder.setMessage(R.string.goal_achieved_question)
                .setPositiveButton(R.string.continue_game_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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

    private class DarkMode implements Runnable {
        @Override
        public void run() {
            // Light Sensor - gdy jest ciemno włącza się dark mode
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    preferences = getSharedPreferences(getResources().getString(R.string.settings), MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    if (mLightData <= DARKMODE_ENABLE_LIGHT && !isDarkTheme) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                darkThemeView.setImageResource(R.drawable.dark_theme_on);
                            }
                        });
                        isDarkTheme = true;
                        editor.putBoolean(getResources().getString(R.string.dark_theme), isDarkTheme);
                        adapter.notifyDataSetChanged();
                    } else if (mLightData >= DARKMODE_DISABLE_LIGHT && isDarkTheme) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                darkThemeView.setImageResource(R.drawable.dark_theme_off);
                            }
                        });
                        isDarkTheme = false;
                        editor.putBoolean(getResources().getString(R.string.dark_theme), isDarkTheme);
                        adapter.notifyDataSetChanged();
                    }
                    editor.apply();
                }
            });
        }
    }

    private void setDarkMode() {

    }

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

    private final static double horizontalPitchMax = 0.5;
    private final static double horizontalPitchMin = -0.5;

    private class ChangeColourMagnetometer implements Runnable {
        @Override
        public void run() {
            float[] orientationValues = magnetometerSetup();
            final float azimuth = orientationValues[0];
            final float pitch = orientationValues[1];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pitch > horizontalPitchMin && pitch < horizontalPitchMax) {
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

    private void restartGame() {
        this.game.restartGame();
        this.setTextScoreText();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        this.setUndoAmount();
    }

    private void setupSwipeListener() {
        onSwipeTouchListener = new OnSwipeTouchListener(this, this.gridView);
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


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}


