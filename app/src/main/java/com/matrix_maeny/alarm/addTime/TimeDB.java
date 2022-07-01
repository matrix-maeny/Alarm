package com.matrix_maeny.alarm.addTime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TimeDB extends SQLiteOpenHelper {
    public TimeDB(@Nullable Context context) {
        super(context, "Time.db", null, 1);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL("Create Table Time(id INT primary key, code INT,time TEXT, enabled INT)");
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists Time");

    }


    public boolean insertTime(int id,int code, String time, int enabled) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("id", id);
        cv.put("code", code);
        cv.put("time", time);
        cv.put("enabled", enabled);

        long result = db.insert("Time", null, cv);

        return result != -1;
    }

    public boolean updateTime(int id,int code, String time, int enabled) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("time", time);
        cv.put("code", code);
        cv.put("enabled", enabled);

        long result = db.update("Time", cv, "id=?", new String[]{String.valueOf(id)});

        return result != -1;

    }
    public boolean updateEnabled(int id,int enabled){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("enabled", enabled);

        long result = db.update("Time", cv, "id=?", new String[]{String.valueOf(id)});

        return result != -1;
    }
    public void updateEnabledUsingTime(String time, int enabled){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("enabled", enabled);

        long result = db.update("Time", cv, "time=?", new String[]{time});

    }


    public boolean deleteTime(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete("Time", "id=?", new String[]{String.valueOf(id)});

        return result != 0;
    }

    public boolean deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("Time", null, null);

        return result != 0;

    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("Select * from Time", null);

    }
}
