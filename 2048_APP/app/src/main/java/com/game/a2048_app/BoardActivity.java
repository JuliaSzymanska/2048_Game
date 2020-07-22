package com.game.a2048_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Arrays;

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
    Button pausePlayButton;

    private Thread updateTimeThread;

    private final static int PROXIMITY_DISTANCE = 5;

    // FIXME: 12.07.2020 bad naming
    private final static float RESET_PITCH = 0.2f;
    private final static float RESET_ROLL = 0.2f;

    private final static float DETECT_MOVE_PITCH = 0.7f;
    private final static float DETECT_MOVE_ROLL = 0.7f;

    // FIXME: 12.07.2020 bad naming
    private final static double changeColourAzimunthBreakpoint1 = 0.5;
    private final static double changeColourAzimunthBreakpoint2 = 1;
    private final static double changeColourAzimunthBreakpoint3 = 2;
    private final static double changeColourAzimunthBreakpoint4 = 2.5;

    private final static int DARKMODE_ENABLE_LIGHT = 30;
    private final static int DARKMODE_DISABLE_LIGHT = 50;


    // znowu się spotykamy
    public final static int MOVE_UP = Game.MOVE_UP;
    public final static int MOVE_RIGHT = Game.MOVE_RIGHT;
    public final static int MOVE_DOWN = Game.MOVE_DOWN;
    public final static int MOVE_LEFT = Game.MOVE_LEFT;

    private boolean hasMoved = false;

    // settings
    SharedPreferences preferences;
    private boolean isDarkTheme = false;

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


        // TODO: 12.07.2020 should probably not be in a different file in such a convoluted function
        OnSwipeTouchListener.setupListener(this.onSwipeTouchListener, this.gridView,
                this, this.game, this.adapter, this.textScore, this.textHighScore);
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
        this.game = new Game(Boolean.parseBoolean(getIntent().getStringExtra(String.valueOf(R.string.authentication))), this);
        this.fields = game.getCopyOfTheBoard().toArray(new Field[0]);
        this.fieldsImages = new Integer[fields.length];
        Arrays.fill(fieldsImages, R.drawable.zero);
    }

    void setTextScoreText() {
        textScore.setText(String.format("%s%s", "Score:\n", game.getCurrentScore()));

    }

    void setTextHighScoreText() {
        if (game.isUserAuthenticated()) {
            textHighScore.setText(String.format("%s%s", "Highscore:\n", game.getHighScore()));
        }
    }

    private void prepareViews() {
        gridView = (GridView) findViewById(R.id.gridView);
        this.prepareGrid();
        textTime = (TextView) findViewById(R.id.time);
        prepareScoreText();
        prepareHighscoreText();
        restartGameButton = (Button) findViewById(R.id.restartGameButton);
        this.restartGameButton.setOnClickListener(restartGameListener);
        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(settingsListener);
        pausePlayButton = (Button) findViewById(R.id.pausePlayButton);
        pausePlayButton.setOnClickListener(playPauseListener);
        darkThemeView = (ImageView) findViewById(R.id.darkThemeView);
        this.setTheme();
        adapter.notifyDataSetChanged();
    }

    private void setTheme() {
        if (this.isDarkTheme) {
            darkThemeView.setImageResource(R.drawable.dark_theme_on);
        } else {
            darkThemeView.setImageResource(R.drawable.dark_theme_off);
        }
    }

    private View.OnClickListener restartGameListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            restartGame();
        }
    };

    private View.OnClickListener settingsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BoardActivity.this);
            builder.setMultiChoiceItems(R.array.sensors, choosenSensors, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    choosenSensors[which] = isChecked;
                }
            });
            builder.setCancelable(false);
            builder.setTitle("Which sensors to enable?");
            builder.setPositiveButton(getResources().getString(R.string.dialog_accept), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    preferences = getSharedPreferences(getResources().getString(R.string.settings), MODE_PRIVATE);
                    String[] sensorNames = getResources().getStringArray(R.array.sensors);
                    SharedPreferences.Editor editor = preferences.edit();
                    for (int i = 0; i < choosenSensors.length; i++) {
                        editor.putBoolean(sensorNames[i], choosenSensors[i]);
                    }
                    editor.apply();
                }
            });
            AlertDialog dialog = builder.create();
            final ListView alertDialogList = dialog.getListView();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    for (int position = 0; position < alertDialogList.getChildCount(); position++) {
                        // TODO: 20.07.2020 trzeba bedzie to zmienic, chciałeś jakoś tak:
                        //  wypełnić array wartościami z string.xml i sprwadzac czy pole array jest rowne stringowi ze string.xml ktory ma odpowiedni klucz
                        if ((mSensorAccelerometer == null && getResources().getStringArray(R.array.sensors)[position].equals("Accelerometer + Gyroscope"))
                                || (mSensorMagnetometer == null && getResources().getStringArray(R.array.sensors)[position].equals("Magnetometer"))
                                || mSensorLight == null && getResources().getStringArray(R.array.sensors)[position].equals("Light sensor")
                                || mSensorProximity == null && getResources().getStringArray(R.array.sensors)[position].equals("Proximity sensor")) {
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
                    viewHolder.imageViewItem = (ImageView) convertView.findViewById(R.id.imageView);
                    viewHolder.textViewItem = (TextView) convertView.findViewById(R.id.textView);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolderItem) convertView.getTag();
                }
                viewHolder.textViewItem.setBackgroundResource(mThumbIds);
                viewHolder.imageViewItem.setTag(position);
                viewHolder.imageViewItem.setImageResource(fieldsImages[position]);

                return convertView;
            }

            class ViewHolderItem {
                TextView textViewItem;
                ImageView imageViewItem;
            }

            @Override
            public void notifyDataSetChanged() {
                setFieldsImages();
                super.notifyDataSetChanged();
            }
        };
        gridView.setAdapter(adapter);
    }

    // TODO: 15.07.2020 bardzo mi się to nie podoba, ale chwilowo nic innego nie przychodzi mi do głowy
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
        this.beginUpdateTime();
    }

    private void beginUpdateTime() {
        updateTimeThread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textTime.setText(String.format("Time:\n%s", game.getElapsedTimeToString()));
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

    void move(int direction) {
        try {
            game.move(direction);
        } catch (GameOverException e) {
            e.printStackTrace();
            Intent i = new Intent(BoardActivity.this, EndGame.class);
            i.putExtra(String.valueOf(R.string.score), Integer.toString(game.getCurrentScore()));
            i.putExtra(String.valueOf(R.string.high_score), Integer.toString(game.getHighScore()));
            i.putExtra(String.valueOf(R.string.authentication), Boolean.toString(game.isUserAuthenticated()));
            startActivity(i);
            restartGame();
        }
        adapter.notifyDataSetChanged();
        this.setScoreTexts();
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
                        if (azimuth >= changeColourAzimunthBreakpoint2 && azimuth < changeColourAzimunthBreakpoint3) {
                            mThumbIds = R.drawable.button_green;
                        } else if (azimuth >= changeColourAzimunthBreakpoint4 || azimuth < -changeColourAzimunthBreakpoint4) {
                            mThumbIds = R.drawable.button_green_light;
                        } else if (azimuth >= -changeColourAzimunthBreakpoint1 && azimuth < changeColourAzimunthBreakpoint1) {
                            mThumbIds = R.drawable.button_blue;
                        } else if (azimuth > -changeColourAzimunthBreakpoint3 && azimuth < -changeColourAzimunthBreakpoint2) {
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
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}


