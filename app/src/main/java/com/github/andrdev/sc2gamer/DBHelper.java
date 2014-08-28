package com.github.andrdev.sc2gamer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SC2Gamer";
    private static final int SCHEMA = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        GamesTable.onCreate(db);
        NewsTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        GamesTable.onUpgrade(db, oldVersion, newVersion);
        NewsTable.onUpgrade(db, oldVersion, newVersion);
    }
}

