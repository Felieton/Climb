package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import static android.hardware.SensorManager.getAltitude;

public class JourneyActivity extends AppCompatActivity {

    private TextView alti, alti_lowest, alti_highest, calories, duration;
    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private Button startButton, stopButton;
    private boolean pressureSensorAvailable;
    private float lowest = Float.MAX_VALUE;
    private float highest = Float.MIN_VALUE;
    private float altitude = 0;
    private double calories_counter = 0;
    private boolean first_measurement = true;
    private float last_altitude;
    private long startTime;
    private long  durationTime;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        private long lastTimestamp;
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            long currentTimestamp = sensorEvent.timestamp;
            if (currentTimestamp - lastTimestamp >= TimeUnit.SECONDS.toNanos(1)) {
                lastTimestamp = currentTimestamp;
                float[] values = sensorEvent.values;
                altitude = getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, values[0]);
                if (!first_measurement) {
                    calories_counter += calculateCalories(last_altitude, altitude);
                    last_altitude = altitude;
                }
                if (first_measurement) {
                    last_altitude = altitude;
                    first_measurement = false;
                }

                alti.setText(String.format("%.1fm", altitude));
                calories.setText(String.format("%.1fkcal", calories_counter));
                if (altitude < lowest) {
                    lowest = altitude;
                    alti_lowest.setText(String.format("%.1fm", altitude));
                }
                if (altitude > highest) {
                    highest = altitude;
                    alti_highest.setText(String.format("%.1fm", altitude));
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;
            duration.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };

    private double calculateCalories(float altitude1, float altitude2) {
        float difference = altitude2 - altitude1;
        double kcal_burned = 0;
        if (difference > 0)
            kcal_burned = difference * 0.17 / 0.1905;
        if (difference < 0)
            kcal_burned = (-difference) * 0.05 / 0.1905;
        return kcal_burned;
    }

    private String getDateFormatted() {
        Calendar now = Calendar.getInstance();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("DD/MM/YYYY");
        int hours = now.get(Calendar.HOUR_OF_DAY);
        String minutes = "" + now.get(Calendar.MINUTE);

        if (Integer.parseInt(minutes) < 10)
            minutes = "0" + minutes;

        return hours + ":" + minutes + " " + formatter.format(currentTime);
    }

    private String getDurationFormatted(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        String returnString = seconds + "s";

        if (minutes > 0)
            returnString = minutes + "min" + " " + returnString;

        if (hours > 0)
            returnString = hours + "h" + " " + returnString;

        return returnString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        alti = findViewById(R.id.altitude);
        alti_lowest = findViewById(R.id.alti_lowest);
        alti_highest = findViewById(R.id.alti_highest);
        calories = findViewById(R.id.calories);
        duration = findViewById(R.id.duration);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        pressureSensorAvailable = pressureSensor != null;
        stopButton.setEnabled(false);
        setStartButton();
        setStopButton();
    }

    private void setStartButton() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = System.currentTimeMillis();
                timerHandler.postDelayed(timerRunnable, 0);
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                lowest = altitude;
                highest = altitude;
                calories_counter = 0;
                alti_lowest.setText(String.format("%.1fm", lowest));
                alti_highest.setText(String.format("%.1fm", highest));
                calories.setText(String.format("%.1fkcal", calories_counter));
            }
        });
    }

    private void setStopButton() {
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                durationTime = System.currentTimeMillis() - startTime;
                timerHandler.removeCallbacks(timerRunnable);
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
                DecimalFormat df = new DecimalFormat("0.0");
                DatabaseHandler databaseHandler = new DatabaseHandler(JourneyActivity.this);
                databaseHandler.addRecord(getDateFormatted(), getDurationFormatted(durationTime),
                        df.format(lowest) + "m", df.format(highest) + "m",
                        df.format(calories_counter) + "kcal");
            }
        });
    }

    protected void onResume() {
        super.onResume();
        if (pressureSensorAvailable)
            sensorManager.registerListener(sensorEventListener, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        else {
            alti.setText("???m");
            alti_lowest.setText("???m");
            alti_highest.setText("???m");
            calories.setText("???kcal");
            startButton.setEnabled(false);
            NoPressureSensorDialog noPressureSensorDialog = new NoPressureSensorDialog();
            noPressureSensorDialog.show(getSupportFragmentManager(), "");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
        timerHandler.removeCallbacks(timerRunnable);
    }
}
