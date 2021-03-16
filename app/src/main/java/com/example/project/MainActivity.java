package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void climbOnClick(View view) {
        final Intent intent = new Intent(this, JourneyActivity.class);
        startActivity(intent);
    }

    public void recordsOnClick(View view) {
        final Intent intent = new Intent(this, RecordsActivity.class);
        startActivity(intent);
    }

    public void altimeterOnClick(View view) {
        final Intent intent = new Intent(this, AltimeterActivity.class);
        startActivity(intent);
    }

    public void infoOnClick(View view) {
        final Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }
}
