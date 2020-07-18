package com.game.a2048_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.game.module.Field;
import com.game.module.Game;
import com.game.module.GameOverException;

import java.lang.reflect.Array;
import java.util.Arrays;

// TODO: 09.06.2020 wcale nie julaszym1212 oto animacja:
//  https://stackoverflow.com/questions/46359987/which-layoutmanager-for-the-animations-of-a-2048-game
//  https://github.com/wasabeef/recyclerview-animators - to chyba najlepsze
//  https://www.youtube.com/watch?v=33wOlQ2y0hQ - tez git
//  https://stackoverflow.com/questions/30997624/how-to-apply-animation-to-add-item-in-gridview-one-by-one

// TODO: 08.06.2020 coś nie halo jest w tym proximity, co ci pisałem, trzeba zobaczyc

public class BoardActivity extends AppCompatActivity implements SensorEventListener {
    private static final float VALUE_DRIFT = 0.05f;


    // TODO: 13.07.2020 https://developer.android.com/guide/topics/ui/dialogs.html
    // ^ settings

    OnSwipeTouchListener onSwipeTouchListener;

    private Game game = Game.getInstance();
    private ArrayAdapter<Integer> adapter;
    private GridView gridView;
    private Field[] fields;
    private Integer[] fieldsImages;

    private Integer mThumbIds = R.drawable.button_green;


    // System sensor manager instance.
    private SensorManager mSensorManager;
    //
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
    private boolean hasMoved = false;

    // TextViews to display current sensor values.
//    private TextView mTextSensorAzimuth;
//    private TextView mTextSensorPitch;
//    private TextView mTextSensorRoll;
//    private TextView mTextSensorLux;

    // TODO: 15.07.2020 binding podwojny
    //  https://developer.android.com/topic/libraries/data-binding/two-way?fbclid=IwAR3nCMsvlFlrsTQVVEvW-Sk9wxKMeOh2HJm_XUM9BJNlJW9ZFFeH-26kXFM
    private TextView textScore;
    private TextView textHighScore;
    private TextView textTime;

    private Button restartGameButton;

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

    // to glupie zeby miec tablice 4bool i pamietac ktory, do ktorego sensora, ale narazie tak zostawiam
    // TODO: 16.07.2020 zrobić to tak żeby się nie resetowało przy każdej zmianie otwartego okna (tzn to i tak ma byc
    //  w DB i być zapisywane na 'stałe' ale no wiadomo cojest5
    private final boolean[] chosenSensors = new boolean[]{false, false, false, false};

    // Azimuth: The direction (north/south/east/west) the device is pointing. 0 is magnetic north.
    // Pitch: The top-to-bottom tilt of the device. 0 is flat.
    // Roll: The left-to-right tilt of the device. 0 is flat.

    private float[] previousValuesAzimuthPitchRoll = new float[3];


    // znowu się spotykamy
    public final static int MOVE_UP = Game.MOVE_UP;
    public final static int MOVE_RIGHT = Game.MOVE_RIGHT;
    public final static int MOVE_DOWN = Game.MOVE_DOWN;
    public final static int MOVE_LEFT = Game.MOVE_LEFT;

    // settings

    SharedPreferences preferences;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    // https://developer.android.com/guide/components/activities/activity-lifecycle
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_board);
        this.loadData();
        fieldsImages = new Integer[fields.length];
        Arrays.fill(fieldsImages, R.drawable.zero);
        this.prepareViews();
        this.prepareSensors();


        // TODO: 12.07.2020 should probably not be in a different file in such a convoluted function
        OnSwipeTouchListener.setupListener(this.onSwipeTouchListener, this.gridView,
                this, this.game, this.adapter, this.textScore, this.textHighScore);
    }

    private void loadData() {
        // wolał bym przypisać preferences w jednym miejcu zamiasty dwu ale się z jakiegoś powodu psuje :/
        preferences = getSharedPreferences(getResources().getString(R.string.settings), MODE_PRIVATE);
        String[] sensorNames =  getResources().getStringArray(R.array.sensors);
        this.chosenSensors[0] = preferences.getBoolean(sensorNames[0], false);
        this.chosenSensors[1] = preferences.getBoolean(sensorNames[1], false);
        this.chosenSensors[2] = preferences.getBoolean(sensorNames[2], false);
        this.chosenSensors[3] = preferences.getBoolean(sensorNames[3], false);
        Game.getInstance().setContext(this);
        Game.getInstance().loadGame();
        Game.getInstance().setContext(null);
        this.fields = game.getCopyOfTheBoard().toArray(new Field[0]);
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
//        mTextSensorAzimuth = (TextView) findViewById(R.id.mTextSensorAzimuth);
//        mTextSensorPitch = (TextView) findViewById(R.id.mTextSensorPitch);
//        mTextSensorRoll = (TextView) findViewById(R.id.mTextSensorRoll);
//        mTextSensorLux = (TextView) findViewById(R.id.mTextSensorLux);
        textTime = (TextView) findViewById(R.id.time);
        prepareScoreText();
        prepareHighscoreText();
        restartGameButton = (Button) findViewById(R.id.restartGameButton);
        this.restartGameButton.setOnClickListener(restartGameListener);
        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(settingsListener);
        Button pausePlayButton = (Button) findViewById(R.id.pausePlayButton);
        pausePlayButton.setOnClickListener(playPauseListener);
        adapter.notifyDataSetChanged();
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
            builder.setMultiChoiceItems(R.array.sensors, chosenSensors, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    chosenSensors[which] = isChecked;
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
                    editor.putBoolean(sensorNames[0], chosenSensors[0]);
                    editor.putBoolean(sensorNames[1], chosenSensors[1]);
                    editor.putBoolean(sensorNames[2], chosenSensors[2]);
                    editor.putBoolean(sensorNames[3], chosenSensors[3]);
                    editor.apply();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };

    private View.OnClickListener playPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!game.isSuspended()) {
                game.pauseTimer();
            } else {
                game.unpauseTimer();
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
    // TODO: 03.06.2020
    //  https://codelabs.developers.google.com/codelabs/advanced-android-training-sensor-orientation/#0
    //  https://androidkennel.org/android-sensors-game-tutorial/
    //  https://www.androidauthority.com/how-to-add-sensor-support-to-your-apps-810715/
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
        this.beingUpdateTime();
    }

    private void beingUpdateTime() {
        Thread updateTimeThread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @SuppressLint("DefaultLocale")
                            @Override
                            public void run() {
                                long elapsedTime = game.getElapsedTimeSeconds();
                                int minutes = (int) elapsedTime / 60;
                                long seconds = elapsedTime % 60;
                                textTime.setText(String.format("Time:\n%02d:%02d", minutes, seconds));
                            }
                        });


                    } catch (InterruptedException e) {
                        // TODO: 13.07.2020 logger
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
    }

    // TODO: 12.07.2020 wszystko to powinno być asynchroniczne
    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            // TODO: 15.07.2020 rename all Runnable classes to things that make sense
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = event.values.clone();
                if (chosenSensors[0]) {
                    new Thread(new PositionGyroscope()).start();
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = event.values.clone();
                if (chosenSensors[1]) {
                    new Thread(new ChangeColourMagnetometer()).start();
                }
                break;
            case Sensor.TYPE_LIGHT:
                mLightData = event.values[0];
                if (chosenSensors[2]) {
                    new Thread(new DarkMode()).start();
                }
                break;
            case Sensor.TYPE_PROXIMITY:
                mProximityData = event.values[0];
                if (chosenSensors[3]) {
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

            final float finalAzimuth = azimuth;
            final float finalPitch = pitch;
            final float finalRoll = roll;
            // has to run on main thread
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mTextSensorAzimuth.setText(getResources().getString(
//                            R.string.value_format, finalAzimuth));
//                    mTextSensorPitch.setText(getResources().getString(
//                            R.string.value_format, finalPitch));
//                    mTextSensorRoll.setText(getResources().getString(
//                            R.string.value_format, finalRoll));
//
//                }
//            });

            if (!hasMoved && (Math.abs(pitch) >= DETECT_MOVE_PITCH || Math.abs(roll) >= DETECT_MOVE_ROLL)) {
                final float finalPitch1 = pitch;
                final float finalRoll1 = roll;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalPitch1 >= DETECT_MOVE_PITCH) {
                            move(MOVE_UP);
                        } else if (finalPitch1 <= -DETECT_MOVE_PITCH) {
                            move(MOVE_DOWN);
                        } else if (finalRoll1 >= DETECT_MOVE_ROLL) {
                            move(MOVE_RIGHT);
                        } else if (finalRoll1 <= -DETECT_MOVE_ROLL) {
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
            restartGame();
            startActivity(new Intent(BoardActivity.this, EndGame.class));
        }
        adapter.notifyDataSetChanged();
        this.setScoreTexts();
    }

    // TODO: 15.07.2020 mało daje multi threading
    private class DarkMode implements Runnable {
        @Override
        public void run() {
            // Light Sensor - gdy jest ciemno włącza się dark mode
            final ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.constraintLayout);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mTextSensorLux.setText(getResources().getString(R.string.value_format, mLightData));
                    int isNightTheme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                    if (mLightData <= DARKMODE_ENABLE_LIGHT && isNightTheme == Configuration.UI_MODE_NIGHT_NO) {
                        // TODO: 15.07.2020 chciałbym to tak
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        cl.setBackgroundResource(R.drawable.background_dark);
                        mThumbIds = R.drawable.button_dark;
                        adapter.notifyDataSetChanged();
                        //https://developer.android.com/guide/topics/ui/look-and-feel/darktheme
                    } else if (mLightData >= DARKMODE_DISABLE_LIGHT && isNightTheme == Configuration.UI_MODE_NIGHT_YES) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        cl.setBackgroundResource(R.drawable.background);
                        mThumbIds = R.drawable.button_blue;
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
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
            final int isNightTheme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            // FIXME: 13.07.2020 to constant
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO: 15.07.2020 mój telefon zauważyłem że ma problem z azymuntem tzn praktycznie nie wychodzi poza 2-3 i -3 - -2
                    if (pitch > horizontalPitchMin && pitch < horizontalPitchMax && isNightTheme == Configuration.UI_MODE_NIGHT_NO) {
                        if (azimuth >= changeColourAzimunthBreakpoint2 && azimuth < changeColourAzimunthBreakpoint3) {
                            // FIXME: 13.07.2020 to constant
//                            mTextSensorLux.setTextColor(Color.rgb(109, 198, 150));
                            mThumbIds = R.drawable.button_green;
                        } else if (azimuth >= changeColourAzimunthBreakpoint4 || azimuth < -changeColourAzimunthBreakpoint4) {
//                            mTextSensorLux.setTextColor(Color.rgb(112, 175, 212));
                            mThumbIds = R.drawable.button_green_light;
                        } else if (azimuth >= -changeColourAzimunthBreakpoint1 && azimuth < changeColourAzimunthBreakpoint1) {
//                            mTextSensorLux.setTextColor(Color.rgb(181, 114, 106));
                            mThumbIds = R.drawable.button_blue;
                        } else if (azimuth > -changeColourAzimunthBreakpoint3 && azimuth < -changeColourAzimunthBreakpoint2) {
//                            mTextSensorLux.setTextColor(Color.rgb(228, 63, 222));
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


