package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import static android.hardware.SensorManager.getAltitude;


public class AltimeterActivity extends AppCompatActivity {
    private TextView alti;
    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private boolean pressureSensorAvailable;
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] values = sensorEvent.values;
            float altitude = getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, values[0]);
            alti.setText(String.format("%.1fm", altitude));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_altimeter);
        alti = findViewById(R.id.altitude);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        pressureSensorAvailable = pressureSensor != null;
    }

    protected void onResume() {
        super.onResume();
        if (pressureSensorAvailable)
            sensorManager.registerListener(sensorEventListener, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        else {
            alti.setText("???m");
            NoPressureSensorDialog noPressureSensorDialog = new NoPressureSensorDialog();
            noPressureSensorDialog.show(getSupportFragmentManager(), "");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }
}
