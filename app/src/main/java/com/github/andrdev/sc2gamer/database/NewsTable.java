package com.github.andrdev.sc2gamer.database;


import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;


public class NewsTable implements BaseColumns {
    public static final String TABLE = "News";
    public static final String TITLE = "title";
    public static final String LINK = "link";
    private static final String CREATE = "CREATE TABLE " + TABLE +
            " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE + " TEXT, " + LINK + " TEXT NOT NULL);";

    public static void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(CREATE);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        onCreate(db);
    }
}
