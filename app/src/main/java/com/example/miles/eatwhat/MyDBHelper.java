package com.example.miles.eatwhat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by miles on 2015/11/12.
 */
public class MyDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Eatwat";
    private static final int DATABASE_VERSION = 1;

    public MyDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE list (_id "
            +"integer primary key autoincrement, "+
            "time integer no null, "+
            "type integer no null, " +
            "star integer no null, " +
            "price integer no null, " +
            "name text no null, " +
            "address text no null, " +
            "criticize text no null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS list");
        onCreate(db);
    }
}
