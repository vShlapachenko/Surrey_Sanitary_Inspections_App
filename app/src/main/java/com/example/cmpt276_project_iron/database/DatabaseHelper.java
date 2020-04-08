package com.example.cmpt276_project_iron.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int UNABLE_TO_INSERT = -1;
    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "favourites_table";
    private static final String COL1 = "restaurant_id";
    private static final String COL2 = "number_inspections";

    public DatabaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + COL1 + " TEXT PRIMARY KEY, " +
                COL2 + " INTEGER)";
        database.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public boolean addData(String restaurant_id, Integer number_inspections) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, restaurant_id);
        contentValues.put(COL2, number_inspections);

        long result = database.insert(TABLE_NAME, null, contentValues);
        if (result == UNABLE_TO_INSERT) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData() {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "Select * FROM " + TABLE_NAME;
        Cursor data = database.rawQuery(query, null);
        return data;
    }

    public List<String> getUpdatedRestaurants(List<String> trackingNumbers, List<Integer> inspectionNumbers) {
        SQLiteDatabase database = this.getWritableDatabase();
        List<String> updatedRestaurants = new ArrayList<>();
        Cursor data = null;
        for (int i = 0; i < trackingNumbers.size(); i++) {
            String trackingNumber = trackingNumbers.get(i);
            int inspectionNumber = inspectionNumbers.get(i);
            String query = ("SELECT " + COL1 + " FROM " + TABLE_NAME + " WHERE "
                    + COL1 + " = '" + trackingNumber + "' AND " + COL2 + " != '" + inspectionNumber + "'");
            data = database.rawQuery(query, null);
            if (data != null && data.moveToFirst()) {
                do {
                    updatedRestaurants.add(data.getString(0));
                } while (data.moveToNext());
            }
        }
        return updatedRestaurants;
    }

    public void updateAllRestaurants(List<String> trackingNumbers, List<Integer> inspectionNumbers) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor data = null;
        for (int i = 0; i < trackingNumbers.size(); i++) {
            String trackingNumber = trackingNumbers.get(i);
            int inspectionNumber = inspectionNumbers.get(i);
            String query = ("UPDATE " + TABLE_NAME + " SET " + COL2 + " = '" + inspectionNumber + "' " +
                    "WHERE " + COL1 + " = '" + trackingNumber + "'");
            database.execSQL(query);
        }
    }

    public void deleteFavourite(String trackingNumber) {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + " = '" + trackingNumber + "'";
        database.execSQL(query);
    }
}



















