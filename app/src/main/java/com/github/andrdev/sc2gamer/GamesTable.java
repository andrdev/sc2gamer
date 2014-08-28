package com.github.andrdev.sc2gamer;


import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class GamesTable implements BaseColumns {
    static final String TABLE = "Games";
    static final String TEAM1_NAME = "team1_name";
    static final String TEAM1_LOGO = "team1_logo";
    static final String TEAM2_NAME = "team2_name";
    static final String TEAM2_LOGO = "team2_logo";
    static final String TIME = "time";

    static final String ALARM = "alarm";
    static final String DEFAULT_ALARM = "false";
    static final String SET_ALARM = "true";
    private static final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
    static {
    mSimpleDateFormat.setTimeZone(TimeZone.getDefault());}
    //    static final String GAMES_TRIGGER_NAME = "alarm_trigger";
//    static final String GAMES_TRIGGER = "CREATE TRIGGER " + GAMES_TRIGGER_NAME + " BEFORE INSERT ON " + TABLE +
//            " BEGIN DELETE FROM " + TABLE +
//            " WHERE " + TIME + " < " + " strftime(%s, 'now') " + " OR " + ALARM + " = " + DEF_ALARM + ");";
    private static final String CREATE = "CREATE TABLE " + TABLE + " ( "
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TEAM1_NAME + " TEXT NOT NULL, "
            + TEAM1_LOGO + " TEXT NOT NULL, " + TEAM2_NAME + " TEXT NOT NULL, " + TEAM2_LOGO
            + " TEXT NOT NULL, " + TIME + " INTEGER NOT NULL, " + ALARM + " TEXT DEFAULT "
            + DEFAULT_ALARM + ", UNIQUE ( " + TEAM1_NAME + ", " + TEAM2_NAME + ", " + TIME + "));";

    public static void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(CREATE);
//            db.execSQL(GAMES_TRIGGER);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE);
//            db.execSQL("DROP TRIGGER IF EXISTS " + GAMES_TRIGGER_NAME);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        onCreate(db);
    }

     public static class GameCursor extends CursorWrapper {
        public GameCursor(Cursor cursor) {
            super(cursor);
        }

        public Game getGame() {
            if (isBeforeFirst() || isAfterLast()) {
                return null;
            }
            Game game = new Game();
            game.setTeam1Name(getString(getColumnIndex(TEAM1_NAME)));
            game.setTeam1Logo(getString(getColumnIndex(TEAM1_LOGO)));
            game.setTeam2Name(getString(getColumnIndex(TEAM2_NAME)));
            game.setTeam2Logo(getString(getColumnIndex(TEAM2_LOGO)));
            game.setTime(mSimpleDateFormat.format(getLong(getColumnIndex(TIME))));
//            game.setBo(getString(B));
            return game;
        }
    }
}
