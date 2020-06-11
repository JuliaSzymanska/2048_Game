package com.game.a2048_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
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
// TODO: 09.06.2020 wcale nie julaszym1212 oto animacja:
//  https://stackoverflow.com/questions/46359987/which-layoutmanager-for-the-animations-of-a-2048-game

// TODO: 09.06.2020 cos o bindingach:
//  https://stackoverflow.com/questions/31915270/is-there-something-like-a-javafx-stringproperty-for-android

// TODO: 08.06.2020 coś nie halo jest w tym proximity, co ci pisałem, trzeba zobaczyc 

public class BoardActivity extends AppCompatActivity implements SensorEventListener {
    private static final float VALUE_DRIFT = 0.05f;
// public class BoardActivity extends AppCompatActivity implements BoardActivity.OnSwipeTouchListener.onSwipeListener {

    // TODO: 04.06.2020 możesz dodać żeby było widac jaki ruch wykonalismy i kiedy
    //  żebyśmy wiedzieli czy się zgadza

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
    private Sensor mSensorLight;
    private Sensor mSensorProximity;

    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];
    private float mLightData;
    private float mProximityData;
    private boolean hasMoved = false;
    private boolean isRunning = true;
    int scoreGame = game.getCurrentScore();

    // TextViews to display current sensor values.
    private TextView mTextSensorAzimuth;
    private TextView mTextSensorPitch;
    private TextView mTextSensorRoll;
    private TextView mTextSensorLux;
    private TextView mTextSensorProximity;
    private TextView score;
    private TextView time;

    // Testing Rotation
    private ImageView mSpotTop;
    private ImageView mSpotBottom;
    private ImageView mSpotLeft;
    private ImageView mSpotRight;

    // Azimuth: The direction (north/south/east/west) the device is pointing. 0 is magnetic north.
    // Pitch: The top-to-bottom tilt of the device. 0 is flat.
    // Roll: The left-to-right tilt of the device. 0 is flat.

    private float[] previousValuesAzimuthPitchRoll = new float[3];

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                // TODO: 09.06.2020 4 Julia S https://stackoverflow.com/questions/6687666/android-how-to-set-the-colour-of-a-toasts-text
                Toast.makeText(getApplicationContext(), ((TextView)v).getText(), Toast.LENGTH_SHORT).show();

            }
        });
        this.onSwipeTouchListener = new OnSwipeTouchListener(this, gridView);
        this.onSwipeTouchListener.onSwipe = new OnSwipeTouchListener.onSwipeListener() {
            @Override
            public void swipeRight() throws GameOverException {
                game.move(Game.MOVE_RIGHT);
                // TODO: 02.06.2020 Po callnieciu adapter.notifyDataSetChanged() aktualizuje sie gridview.
                adapter.notifyDataSetChanged();
                // TODO: 04.06.2020 narazie tak to jest potem trzebabedzie dodac jakies ladne listenery albo bindingi
                score.setText(String.format("%s%s", "Wynik: ", game.getCurrentScore()));
            }

            @Override
            public void swipeTop() throws GameOverException {
                game.move(Game.MOVE_UP);
                adapter.notifyDataSetChanged();
                score.setText(String.format("%s%s", "Wynik: ", game.getCurrentScore()));
            }

            @Override
            public void swipeBottom() throws GameOverException {
                game.move(Game.MOVE_DOWN);
                adapter.notifyDataSetChanged();
                score.setText(String.format("%s%s", "Wynik: ", game.getCurrentScore()));
            }

            @Override
            public void swipeLeft() throws GameOverException {
                game.move(Game.MOVE_LEFT);
                adapter.notifyDataSetChanged();
                score.setText(String.format("%s%s", "Wynik: ", game.getCurrentScore()));
            }
        };

        // Get accelerometer and magnetometer sensors from the sensor manager.
        //        // The getDefaultSensor() method returns null if the sensor
        //        // is not available on the device.
        mSensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD);
        mSensorLight = mSensorManager.getDefaultSensor(
                Sensor.TYPE_LIGHT);

        mTextSensorAzimuth = (TextView) findViewById(R.id.mTextSensorAzimuth);
        mTextSensorPitch = (TextView) findViewById(R.id.mTextSensorPitch);
        mTextSensorRoll = (TextView) findViewById(R.id.mTextSensorRoll);
        mTextSensorLux = (TextView) findViewById(R.id.mTextSensorLux);
        score = (TextView) findViewById(R.id.score);
        time = (TextView) findViewById(R.id.time);


//        mSpotTop = (ImageView) findViewById(R.id.spot_top);
//        mSpotBottom = (ImageView) findViewById(R.id.spot_bottom);
//        mSpotLeft = (ImageView) findViewById(R.id.spot_left);
//        mSpotRight = (ImageView) findViewById(R.id.spot_right);
    }


    private void setListOfFields() {

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
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        mSensorManager.unregisterListener(this);
    }

    // TODO: 03.06.2020 myślę że trzeba jakoś założyć że telefon leży na 'plecach'
    //  i sprawdzać czy na tych plecach faktycznie leży, bo to ułatwi bardzo operowanie na tym czyms
    //  i teraz nie wiem czy po prostu sprawdzać czy pochlenie w danym kierunku zmieniło się ponad ileś
    //  np ponad 30 stopni albo 45, i czy poprzednio pochylenie było mniejsze od tej wartości
    //  czy moze jakos predkosc tej zmiany sprawdzac?

    // TODO: 03.06.2020 Aktualnie chyba działa, ale nie ładowałem na telefon.
    //  Działa w ten sposób że jeżeli po prostu przekroczymy wychylenie o ponad 45 stopni(leżąc na plecach)
    //  to sie odpowiednio rusza.
    //  obejrzyj sobie

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
            case Sensor.TYPE_LIGHT:
                mLightData = event.values[0];
            case Sensor.TYPE_PROXIMITY:
                mProximityData = event.values[0];
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

        float prevAzimuth = this.previousValuesAzimuthPitchRoll[0];
        float prevPitch = this.previousValuesAzimuthPitchRoll[1];
        float prevRoll = this.previousValuesAzimuthPitchRoll[2];

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
        score.setText(String.format("%s%s", "Wynik: ", game.getCurrentScore()));
        long milisec = game.getElapsedTime() / 1000000;
        int sec = (int)Math.floor((double)(milisec / 1000));
        milisec = milisec - (sec * 1000);
        time.setText(String.format("Czas: %s:%s", sec, milisec));
//        time.setText("Czas:");

        // TODO: 03.06.2020 w zależności od rotacji wypełnia się alpha kulek
        //  wizualizacja efektów obrotu
        //  do usuniecia potem

//        mSpotTop.setAlpha(0f);
//        mSpotBottom.setAlpha(0f);
//        mSpotLeft.setAlpha(0f);
//        mSpotRight.setAlpha(0f);

//        if (pitch > 0) {
//            mSpotBottom.setAlpha((float) (pitch / Math.PI));
//        } else {
//            mSpotTop.setAlpha((float) Math.abs(pitch / Math.PI));
//        }
//        if (roll > 0) {
//            mSpotLeft.setAlpha((float) (roll / Math.PI));
//        } else {
//            mSpotRight.setAlpha((float) Math.abs(roll / Math.PI));
//        }

        // TODO: 03.06.2020 jesli dobrze rozumiem to wtedy znaczy że telefon lezy poziomo lub jest maks o 45 stopni wychylony
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
                    // TODO: 03.06.2020 konczyc tu gre
                    e.printStackTrace();
                }
            }
        }

        // TODO: 04.06.2020 SPRAWDZ CZY JEST OK
        if (Math.abs(pitch) < 0.2 && Math.abs(roll) < 0.2) {
            hasMoved = false;
        }

        // TODO: 04.06.2020 Proximity sensor zatrzymuje sie czas po zblizeniu
        if (mProximityData < 10) {
            if (isRunning == true) {
                game.pauseTimer();
                isRunning = false;
            }
        } else if (isRunning == false) {
            game.unpauseTimer();
            isRunning = true;
        }

        // TODO: 04.06.2020 Narazie sprawdzialm jakby dziala ta zmiana kolorow w tekstfieldach
        if (azimuth >= 0.75 && azimuth < 2.25) {
            mTextSensorLux.setTextColor(Color.rgb(109, 198, 150));
        } else if (azimuth >= 2.25 || azimuth < -2.25) {
            mTextSensorLux.setTextColor(Color.rgb(112, 175, 212));
        } else if (azimuth >= -0.75 && azimuth < 0.75) {
            mTextSensorLux.setTextColor(Color.rgb(181, 114, 106));
        } else {
            mTextSensorLux.setTextColor(Color.rgb(153, 105, 181));
        }


        this.previousValuesAzimuthPitchRoll = orientationValues;

        // TODO: 04.06.2020 Tutaj jest czujnik swiatla - darkmode niby
        mTextSensorLux.setText(getResources().getString(R.string.value_format, mLightData));
        ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.constraintLayout);
        ColorDrawable viewColor = (ColorDrawable) cl.getBackground();
        int colorId = viewColor.getColor();
        System.out.println(colorId);
        if (mLightData < 30 && colorId != -5924712) {
            cl.setBackgroundColor(Color.rgb(165, 152, 152));
        } else if (mLightData >= 30 && colorId != -10281) {
            cl.setBackgroundColor(Color.rgb(255, 215, 215));
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}


