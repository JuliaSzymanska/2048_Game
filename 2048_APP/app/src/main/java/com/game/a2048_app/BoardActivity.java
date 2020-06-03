package com.game.a2048_app;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorAdditionalInfo;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.game.module.Field;
import com.game.module.Game;
import com.game.module.GameOverException;

public class BoardActivity extends AppCompatActivity implements SensorEventListener {
    private static final float VALUE_DRIFT = 0.05f;
// public class BoardActivity extends AppCompatActivity implements BoardActivity.OnSwipeTouchListener.onSwipeListener {

    OnSwipeTouchListener onSwipeTouchListener;

    private Game game = Game.getInstance();
    private ArrayAdapter<Field> adapter;
    private GridView gridView;
    // TODO: 01.06.2020 narazie mamy zwykla tablice fieldow
    private Field[] fields = game.getCopyOfTheBoard().toArray(new Field[0]);

    // System sensor manager instance.
    private SensorManager mSensorManager;

    // Accelerometer and magnetometer sensors, as retrieved from the
    // sensor manager.
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;

    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];

    // TextViews to display current sensor values.
    private TextView mTextSensorAzimuth;
    private TextView mTextSensorPitch;
    private TextView mTextSensorRoll;

    // Testing Rotation
    private ImageView mSpotTop;
    private ImageView mSpotBottom;
    private ImageView mSpotLeft;
    private ImageView mSpotRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: 03.06.2020 pluje sie ale tak jest w tutorialu na stronie androida
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        gridView = (GridView) findViewById(R.id.gridView);
        // TODO: 01.06.2020 cos mi sie obilo o uszy ze to moze byc listener ale i'm not sure to trzeba bedzie sprawdzic
        adapter = new ArrayAdapter<Field>(this,
                android.R.layout.simple_list_item_1, fields);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // TODO: 01.06.2020 to jest po prostu to ze jak klikniesz to na dole sie pojawia co wybrales
                //  dodalalm to zeby po prostu poprobowac cos trzeba to bedzie jakos madrze zrobic
                Toast.makeText(getApplicationContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show();

            }
        });
       this.onSwipeTouchListener = new OnSwipeTouchListener(this, gridView);
       this.onSwipeTouchListener.onSwipe = new OnSwipeTouchListener.onSwipeListener() {
           @Override
           public void swipeRight() throws GameOverException {
               game.move(Game.MOVE_RIGHT);
               // TODO: 02.06.2020 Po callnieciu adapter.notifyDataSetChanged() aktualizuje sie gridview.
               adapter.notifyDataSetChanged();
           }

           @Override
           public void swipeTop() throws GameOverException {
               game.move(Game.MOVE_UP);
               adapter.notifyDataSetChanged();
           }

           @Override
           public void swipeBottom() throws GameOverException {
               game.move(Game.MOVE_DOWN);
               adapter.notifyDataSetChanged();
           }

           @Override
           public void swipeLeft() throws GameOverException {
               game.move(Game.MOVE_LEFT);
               adapter.notifyDataSetChanged();
           }
       };

        // Get accelerometer and magnetometer sensors from the sensor manager.
        // The getDefaultSensor() method returns null if the sensor
        // is not available on the device.
        mSensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD);

        mTextSensorAzimuth = (TextView) findViewById(R.id.mTextSensorAzimuth);
        mTextSensorPitch = (TextView) findViewById(R.id.mTextSensorPitch);
        mTextSensorRoll = (TextView) findViewById(R.id.mTextSensorRoll);

        mSpotTop = (ImageView) findViewById(R.id.spot_top);
        mSpotBottom = (ImageView) findViewById(R.id.spot_bottom);
        mSpotLeft = (ImageView) findViewById(R.id.spot_left);
        mSpotRight = (ImageView) findViewById(R.id.spot_right);
    }


    private void setListOfFields() {

    }

    /**
     * Listeners for the sensors are registered in this callback so that
     * they can be unregistered in onStop().
     */

    // TODO: 03.06.2020
    //  https://codelabs.developers.google.com/codelabs/advanced-android-training-sensor-orientation/#0
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
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = event.values.clone();
                break;
            default:
                return;
        }
        float[] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix,
                null, mAccelerometerData, mMagnetometerData);
        float[] orientationValues = new float[3];
        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrix, orientationValues);
        }
        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        // malutkie odchylenie -> zmiana na 0
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

        // TODO: 03.06.2020 w zależności od rotacji wypełnia się alpha kulek
        //  wizualizacja efektów obrotu
        //  do usuniecia potem

        mSpotTop.setAlpha(0f);
        mSpotBottom.setAlpha(0f);
        mSpotLeft.setAlpha(0f);
        mSpotRight.setAlpha(0f);

        if (pitch > 0) {
            mSpotBottom.setAlpha((float) (pitch / Math.PI));
        } else {
            mSpotTop.setAlpha((float) Math.abs(pitch / Math.PI));
        }
        if (roll > 0) {
            mSpotLeft.setAlpha((float) (roll / Math.PI));
        } else {
            mSpotRight.setAlpha((float) Math.abs(roll / Math.PI));
        }

        // TODO: 03.06.2020 jak dodamy tutaj kiedyś już obsługe gry przez obracanie telefonem
        //  trzeba dodac opcje wylaczania tego
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}


