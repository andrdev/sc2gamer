package com.github.andrdev.sc2gamer;


import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;


public class NewsTable implements BaseColumns {
    static final String TABLE = "News";
    static final String TITLE = "title";
    static final String LINK = "link";
    //    static final String NEWS_TRIGGER_NAME = "news_trigger";
//    static final String NEWS_TRIGGER = "CREATE TRIGGER " + NEWS_TRIGGER_NAME + " BEFORE INSERT ON " + TABLE +
//            " BEGIN DELETE FROM " + TABLE + ");";
    private static final String CREATE = "CREATE TABLE " + TABLE +
            " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE + " TEXT, " + LINK + " TEXT NOT NULL);";

    public static void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(CREATE);
//            db.execSQL(NEWS_TRIGGER);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE);
//            db.execSQL("DROP TRIGGER IF EXISTS " + NEWS_TRIGGER_NAME);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        onCreate(db);
    }
}
