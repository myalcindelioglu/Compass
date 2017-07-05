package com.myd.compass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor magnetometer;
    private Sensor accelerometer;
    private SensorEventListener sensorEventListener;

    private float[] gravityValues = new float[3];
    private float[] accelerometerValues = new float[3];
    private float[] rotationMatrix = new float[9];
    private float lastDirectionDegrees = 0f;

    private ImageView compassView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compassView = (ImageView) findViewById(R.id.activity_main_compass_iv);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorEventListener = new CompassSensorListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener,
                magnetometer,
                SensorManager.SENSOR_DELAY_FASTEST);

        sensorManager.registerListener(sensorEventListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    private class CompassSensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            calculateCompassDirection(event);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private void calculateCompassDirection(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValues = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                gravityValues = event.values.clone();
                break;

        }

        boolean success = SensorManager.getRotationMatrix(rotationMatrix,
                null,
                accelerometerValues,
                gravityValues);

        if (success) {
            float[] orientationValues = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientationValues);
            float azimuth = (float)Math.toDegrees(-orientationValues[0]);

            RotateAnimation rotateAnimation = new RotateAnimation(
                    lastDirectionDegrees,
                    azimuth,
                    RotateAnimation.RELATIVE_TO_SELF,
                    0.5f,
                    RotateAnimation.RELATIVE_TO_SELF,
                    0.5f);
            rotateAnimation.setDuration(50);
            rotateAnimation.setFillAfter(true);
            compassView.startAnimation(rotateAnimation);
            lastDirectionDegrees = azimuth;
        }
    }
}
