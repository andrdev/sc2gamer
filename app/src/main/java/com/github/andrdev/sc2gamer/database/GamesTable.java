package com.github.andrdev.sc2gamer.database;


import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;


public class GamesTable implements BaseColumns {
    public static final String TABLE = "Games";
    public static final String TEAM1_NAME = "team1_name";
    public static final String TEAM1_LOGO = "team1_logo";
    public static final String TEAM2_NAME = "team2_name";
    public static final String TEAM2_LOGO = "team2_logo";
    public static final String TIME = "time";

    public static final String ALARM = "alarm";
    public static final String DEFAULT_ALARM = "false";
    public static final String SET_ALARM = "true";

    private static final String CREATE = "CREATE TABLE " + TABLE + " ( "
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TEAM1_NAME + " TEXT NOT NULL, "
            + TEAM1_LOGO + " TEXT NOT NULL, " + TEAM2_NAME + " TEXT NOT NULL, " + TEAM2_LOGO
            + " TEXT NOT NULL, " + TIME + " INTEGER NOT NULL, " + ALARM + " TEXT DEFAULT "
            + DEFAULT_ALARM + ", UNIQUE ( " + TEAM1_NAME + ", " + TEAM2_NAME + ", " + TIME + "));";

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
