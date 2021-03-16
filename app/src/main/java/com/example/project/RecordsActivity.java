package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;

public class RecordsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CustomAdapter customAdapter;
    DatabaseHandler databaseHandler;
    ArrayList<String> records_id, records_date, records_time, records_lowest, records_highest, records_calories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        databaseHandler = new DatabaseHandler(RecordsActivity.this);
        recyclerView = findViewById(R.id.recyclerView);
        records_id = new ArrayList<>();
        records_date = new ArrayList<>();
        records_time = new ArrayList<>();
        records_lowest = new ArrayList<>();
        records_highest = new ArrayList<>();
        records_lowest = new ArrayList<>();
        records_calories = new ArrayList<>();
        storeDataInArrays();
        customAdapter = new CustomAdapter(RecordsActivity.this, records_id, records_date, records_time,
                records_lowest, records_highest, records_calories);
        customAdapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(String id, int position) {
                databaseHandler.deleteOneRow(id);
                removeItem(position);
                customAdapter.notifyItemRemoved(position);
            }
        });
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(RecordsActivity.this));
    }

    void removeItem(int position) {
        records_id.remove(position);
        records_date.remove(position);
        records_time.remove(position);
        records_lowest.remove(position);
        records_highest.remove(position);
        records_calories.remove(position);
    }

    void storeDataInArrays(){
        Cursor cursor = databaseHandler.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()){
                records_id.add(cursor.getString(0));
                records_date.add(cursor.getString(1));
                records_time.add(cursor.getString(2));
                records_lowest.add(cursor.getString(3));
                records_highest.add(cursor.getString(4));
                records_calories.add(cursor.getString(5));
            }
            Collections.reverse(records_id);
            Collections.reverse(records_date);
            Collections.reverse(records_time);
            Collections.reverse(records_lowest);
            Collections.reverse(records_highest);
            Collections.reverse(records_calories);
        }
    }


}