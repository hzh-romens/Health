package com.romens.yjk.health;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends WearableActivity implements SensorEventListener,MobvoiApiClient.ConnectionCallbacks,
        MobvoiApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainActivity";
    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;


    SensorManager mSensorManager;
    Sensor mHeartRateSensor;
    Sensor mStepCountSensor;
    Sensor mStepDetectSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);
        mSensorManager= ((SensorManager)getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        //mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        //mStepDetectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this,mStepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this,mStepDetectSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

//    private void getStepCount() {
//
//
//        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        mSensorManager.registerListener(this, mStepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        mSensorManager.registerListener(this, mStepDetectSensor, SensorManager.SENSOR_DELAY_NORMAL);
//    }

    private String currentTimeStr() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(c.getTime());
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "" + (int)event.values[0];
            mTextView.append(msg);
            mTextView.append(new Gson().toJson(event.sensor));

            Log.d(TAG, msg);
        }
        else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            String msg = "Count: " + (int)event.values[0];
            mTextView.append(new Gson().toJson(event.sensor));
            mTextView.append(msg);
            Log.d(TAG, msg);
        }
        else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            String msg = "Detected at " + currentTimeStr();
            mTextView.append(new Gson().toJson(event.sensor));
            mTextView.append(msg);
            Log.d(TAG, msg);
        }
        else
            Log.d(TAG, "Unknown sensor type");
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}