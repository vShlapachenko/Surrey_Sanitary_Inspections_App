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

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "favourites_table";
    private static final String COL1 = "restaurant_id";
    private static final String COL2 = "number_inspections";

    public DatabaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + COL1 + " TEXT PRIMARY KEY, " +
                COL2 + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String restaurant_id, Integer number_inspections) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, restaurant_id);
        contentValues.put(COL2, number_inspections);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public List<String> getUpdatedRestaurants(List<String> trackingNumbers, List<Integer> inspectionNumbers) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> updatedRestaurants = new ArrayList<>();
        Cursor data = null;
        for (int i = 0; i < trackingNumbers.size(); i++) {
            String trackingNumber = trackingNumbers.get(i);
            Integer inspectionNumber = inspectionNumbers.get(i);
            String query = ("SELECT " + COL1 + " FROM " + TABLE_NAME + " WHERE "
                    + COL1 + " = '" + trackingNumber + "' AND " + COL2 + " != '" + inspectionNumber + "'");
            data = db.rawQuery(query, null);
            if (data != null && data.moveToFirst()) {
                do {
                    updatedRestaurants.add(data.getString(0));
                } while (data.moveToNext());
            }
        }
        return updatedRestaurants;
    }

    public void updateAllRestaurants(List<String> trackingNumbers, List<Integer> inspectionNumbers) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = null;
        for (int i = 0; i < trackingNumbers.size(); i++) {
            String trackingNumber = trackingNumbers.get(i);
            Integer inspectionNumber = inspectionNumbers.get(i);
            String query = ("UPDATE " + TABLE_NAME + " SET " + COL2 + " = '" + inspectionNumber + "' " +
                    "WHERE " + COL1 + " = '" + trackingNumber + "'");
            db.execSQL(query);
        }
    }

    public void deleteFavourite(String trackingNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + " = '" + trackingNumber + "'";
        db.execSQL(query);
    }
}



















