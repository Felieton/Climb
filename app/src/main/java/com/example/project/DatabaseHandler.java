package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "RecordsDataBase.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "records";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_DATE = "record_date";
    private static final String COLUMN_TIME = "record_time";
    private static final String COLUMN_LOWEST = "record_lowest";
    private static final String COLUMN_HIGHEST = "record_highest";
    private static final String COLUMN_CALORIES = "record_calories";

    public DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_DATE + " TEXT, " + COLUMN_TIME + " TEXT, " +
                        COLUMN_LOWEST + " TEXT, " + COLUMN_HIGHEST + " TEXT, " +
                        COLUMN_CALORIES + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addRecord(String date, String duration, String lowest, String highest, String calories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TIME, duration);
        cv.put(COLUMN_LOWEST, lowest);
        cv.put(COLUMN_HIGHEST, highest);
        cv.put(COLUMN_CALORIES, calories);
        long result =  db.insert(TABLE_NAME, null, cv);
        if (result != -1) {
            Toast.makeText(context, "Record stored", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }

        return cursor;
    }

    void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to delete.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Record deleted.", Toast.LENGTH_SHORT).show();
        }
    }
}
