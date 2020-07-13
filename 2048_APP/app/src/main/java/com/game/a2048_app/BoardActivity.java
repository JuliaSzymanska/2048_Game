package com.game.a2048_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.game.module.Field;
import com.game.module.Game;
import com.game.module.GameOverException;

// TODO: 09.06.2020 wcale nie julaszym1212 oto animacja:
//  https://stackoverflow.com/questions/46359987/which-layoutmanager-for-the-animations-of-a-2048-game
//  https://github.com/wasabeef/recyclerview-animators - to chyba najlepsze
//  https://www.youtube.com/watch?v=33wOlQ2y0hQ - tez git
//  https://stackoverflow.com/questions/30997624/how-to-apply-animation-to-add-item-in-gridview-one-by-one

// TODO: 08.06.2020 coś nie halo jest w tym proximity, co ci pisałem, trzeba zobaczyc

public class BoardActivity extends AppCompatActivity implements SensorEventListener {
    private static final float VALUE_DRIFT = 0.05f;


    OnSwipeTouchListener onSwipeTouchListener;

    private Game game = Game.getInstance();
    private ArrayAdapter<Field> adapter;
    private GridView gridView;
    private Field[] fields = game.getCopyOfTheBoard().toArray(new Field[0]);
    private Integer mThumbIds = R.drawable.button_green;
    private HighScoreDao db;
    private int highScore = 0;

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
    private boolean hasMoved = false;

    // TextViews to display current sensor values.
    private TextView mTextSensorAzimuth;
    private TextView mTextSensorPitch;
    private TextView mTextSensorRoll;
    private TextView mTextSensorLux;

    private TextView textScore;
    private TextView textHighScore;
    private TextView textTime;

    private Button restartGameButton;
    private Button endGame;

    private Thread updateTimeThread;


    // Azimuth: The direction (north/south/east/west) the device is pointing. 0 is magnetic north.
    // Pitch: The top-to-bottom tilt of the device. 0 is flat.
    // Roll: The left-to-right tilt of the device. 0 is flat.

    private float[] previousValuesAzimuthPitchRoll = new float[3];

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    // https://developer.android.com/guide/components/activities/activity-lifecycle
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_board);
        db = new HighScoreDao(this);
        this.prepareViews();
        this.prepareSensors();

        // TODO: 12.07.2020 should probably not be in a different file in such a convoluted function
        OnSwipeTouchListener.setupListener(this.onSwipeTouchListener, this.gridView,
                this, this.game, this.adapter, this.textScore, this.textHighScore);
    }

    void setTextScoreText() {
        textScore.setText(String.format("%s%s", "Score: ", game.getCurrentScore()));
    }

    void setTextHighScoreText() {
        highScore = db.getHighScore();
        textHighScore.setText(String.format("%s%s", "Highscore: ", highScore));
    }

    private View.OnClickListener restartGameListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            restartGame();
        }
    };

    private View.OnClickListener endGameListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateHighScore();
            startActivity(new Intent(BoardActivity.this, EndGame.class));
        }
    };

    private void prepareViews() {
        gridView = (GridView) findViewById(R.id.gridView);
        this.prepareGrid();
        mTextSensorAzimuth = (TextView) findViewById(R.id.mTextSensorAzimuth);
        mTextSensorPitch = (TextView) findViewById(R.id.mTextSensorPitch);
        mTextSensorRoll = (TextView) findViewById(R.id.mTextSensorRoll);
        mTextSensorLux = (TextView) findViewById(R.id.mTextSensorLux);
        textTime = (TextView) findViewById(R.id.time);
        textScore = (TextView) findViewById(R.id.score);
        this.setTextScoreText();
        textHighScore = (TextView) findViewById(R.id.highScore);
        this.setTextHighScoreText();
        endGame = (Button) findViewById(R.id.endGame);
        endGame.setOnClickListener(endGameListener);
        restartGameButton = (Button) findViewById(R.id.restartGameButton);
        this.restartGameButton.setOnClickListener(restartGameListener);
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
        this.adapter = new ArrayAdapter<Field>(this,
                android.R.layout.simple_list_item_1, fields) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) super.getView(position, convertView, parent);
                view.setBackgroundResource(mThumbIds);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(30);
                textView.setTextColor(Color.WHITE);
                return view;
            }
        };
        gridView.setAdapter(adapter);
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
        Thread updateTimeThread = new Thread(){
            @Override
            public void run() {
                while(!isInterrupted()){
                    try {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long elapsedTime = game.getElapsedTimeSeconds();
                                int minutes = (int) elapsedTime / 60;
                                long seconds = elapsedTime % 60;
                                textTime.setText(String.format("Time: %s:%s", minutes, seconds));
                            }
                        });


                    } catch (InterruptedException e) {
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
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = event.values.clone();
                positionGyroscope();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = event.values.clone();
                changeColourMagnetometer();
                break;
            case Sensor.TYPE_LIGHT:
                mLightData = event.values[0];
                darkMode();
                break;
            case Sensor.TYPE_PROXIMITY:
                mProximityData = event.values[0];
                stopGameProximity();
                break;
            default:
                // FIXME: 07.07.2020 logger albo exception
                System.out.println("Unexpected sensor event");
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

    private void positionGyroscope() {
        float[] orientationValues = magnetometerSetup();
        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        float prevAzimuth = this.previousValuesAzimuthPitchRoll[0];
        float prevPitch = this.previousValuesAzimuthPitchRoll[1];
        float prevRoll = this.previousValuesAzimuthPitchRoll[2];

        // malutkie odchylenie -> zmiana na 0
        // nam to raczej nie potrzebne
        if (Math.abs(pitch) < VALUE_DRIFT) {
            pitch = 0;
        }
        if (Math.abs(roll) < VALUE_DRIFT) {
            roll = 0;
        }

        mTextSensorAzimuth.setText(getResources().getString(
                R.string.value_format, azimuth));
        mTextSensorPitch.setText(getResources().getString(
                R.string.value_format, pitch));
        mTextSensorRoll.setText(getResources().getString(
                R.string.value_format, roll));

        if (Math.abs(prevPitch) < 0.4 && Math.abs(prevRoll) < 0.7) {
            if (!hasMoved && Math.abs(pitch) >= 0.4 || Math.abs(roll) >= 0.7) {
                try {
                    if (pitch >= 0.4) {
                        game.move(Game.MOVE_UP);
                        adapter.notifyDataSetChanged();
                    } else if (pitch <= -0.4) {
                        game.move(Game.MOVE_DOWN);
                        adapter.notifyDataSetChanged();
                    } else if (roll >= 0.7) {
                        game.move(Game.MOVE_RIGHT);
                        adapter.notifyDataSetChanged();
                    } else if (roll <= -0.7) {
                        game.move(Game.MOVE_LEFT);
                        adapter.notifyDataSetChanged();
                    }
                    hasMoved = true;
                } catch (GameOverException e) {
                    e.printStackTrace();
                    startActivity(new Intent(BoardActivity.this, EndGame.class));
                }
            }
        }

        if (Math.abs(pitch) < 0.2 && Math.abs(roll) < 0.2) {
            hasMoved = false;
        }
        this.previousValuesAzimuthPitchRoll = orientationValues;
    }

    void updateHighScore() {
        int score = game.getCurrentScore();
        if (score > highScore && game.isUserAuthenticated()) {
            db.addHighScore(score);
            highScore = score;
        }
    }

    private void darkMode() {
        // Light Sensor - gdy jest ciemno włącza się dark mode
        mTextSensorLux.setText(getResources().getString(R.string.value_format, mLightData));
        ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.constraintLayout);
        ColorDrawable viewColor = (ColorDrawable) cl.getBackground();
        int colorId = viewColor.getColor();
        if (mLightData < 30 && colorId != -5924712) {
            cl.setBackgroundColor(Color.rgb(165, 152, 152));
        } else if (mLightData >= 30 && colorId != -10281) {
            cl.setBackgroundColor(Color.rgb(255, 215, 215));
        }
    }

    private void stopGameProximity() {
        // Proximity sensor - zatrzymuje sie czas po zblizeniu
        if (mProximityData < 5) {
            if (!game.isSuspended()) {
                game.pauseTimer();
            }
        } else if (game.isSuspended()) {
            game.unpauseTimer();
        }
    }

    private void changeColourMagnetometer() {
        float azimuth = magnetometerSetup()[0];
        if (azimuth >= 0.75 && azimuth < 2.25) {
            mTextSensorLux.setTextColor(Color.rgb(109, 198, 150));
            mThumbIds = R.drawable.button_green;
        } else if (azimuth >= 2.25 || azimuth < -2.25) {
            mTextSensorLux.setTextColor(Color.rgb(112, 175, 212));
            mThumbIds = R.drawable.button_blue;
        } else if (azimuth >= -0.75 && azimuth < 0.75) {
            mTextSensorLux.setTextColor(Color.rgb(181, 114, 106));
            mThumbIds = R.drawable.button_red;
        } else {
            mTextSensorLux.setTextColor(Color.rgb(228, 63, 222));
            mThumbIds = R.drawable.button_pink;
        }
        adapter.notifyDataSetChanged();
    }


    private void restartGame() {
        this.game.restartGame();
        this.setTextScoreText();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}


