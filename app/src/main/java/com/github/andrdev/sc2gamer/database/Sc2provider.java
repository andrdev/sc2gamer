package com.github.andrdev.sc2gamer.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * ContentProvider class that handles db interactions and and sends
 * notifications to the ContentResolver.
 * Consist is a number of constants for creating and working with Uri, and some methods
 * for work with db.
 */
public class Sc2provider extends ContentProvider {
    private DBHelper dbHelper;

    /**
     * Constant that helps to use an Uri on switch.
     */
    private final static int GAMES_LIST = 10;
    private final static int GAMES_ALARM = 20;
    private final static int NEWS_LIST = 30;

    /**
     * Constants for creating Uri.
     */
    private final static String PATH_GAMES = "games";
    private final static String PATH_NEWS = "news";
    private final static String PATH_ALARMS = "alarms";
    private final static String AUTHORITY = "com.git.andrdev.sc2gamer.contentprovider";

    private final static String SORT_GAMES = GamesTable.TIME;

    /**
     *An Uri's.
     */
    public static final Uri CONTENT_URI_GAMES = Uri.parse("content://" + AUTHORITY + "/" + PATH_GAMES);
    public static final Uri CONTENT_URI_NEWS = Uri.parse("content://" + AUTHORITY + "/" + PATH_NEWS);
    public static final Uri CONTENT_URI_ALARMS = Uri.parse("content://" + AUTHORITY +
            "/" + PATH_GAMES + "/" + PATH_ALARMS);

    /**
     *Creating matcher and adding Uri's to it.
     */
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        MATCHER.addURI(AUTHORITY, PATH_GAMES, GAMES_LIST);
        MATCHER.addURI(AUTHORITY, PATH_GAMES + "/" + PATH_ALARMS, GAMES_ALARM);
        MATCHER.addURI(AUTHORITY, PATH_NEWS, NEWS_LIST);
    }

    /**
     * Create DBHelper object to interact with the db.
     * @return true
     */
    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    /**
     *query the DB according to the Uri sent.
     * Alarm Uri returns a cursor with the List of games where alarms are set.
     * Games and News Uri returns list of games or news. Games list sorted by time ascending.
     * Sent notification
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        switch (MATCHER.match(uri)) {
            case GAMES_ALARM:
//                queryBuilder.appendWhere(GamesTable.ALARM + " = " + GamesTable.SET_ALARM);
            case GAMES_LIST:
                sortOrder = SORT_GAMES;
                queryBuilder.setTables(GamesTable.TABLE);
                break;
            case NEWS_LIST:
                queryBuilder.setTables(NewsTable.TABLE);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("GetType not supported.");
    }

    /**
     * bulkInsert inserts the array of ContentValues to the proper table (according to the Uri).
     * ContentResolver notifies loader with Uri.
     * @param uri
     * @param values
     * @return
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (MATCHER.match(uri)) {
            case GAMES_ALARM:
                throw new UnsupportedOperationException("Bulkinsert for the alarm is not supported.");
            case GAMES_LIST:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        db.insertWithOnConflict(GamesTable.TABLE, GamesTable.ALARM, value, SQLiteDatabase.CONFLICT_IGNORE);
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case NEWS_LIST:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        db.insert(NewsTable.TABLE, NewsTable.TITLE, value);
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return values.length;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = 0;
        switch (MATCHER.match(uri)) {
//            case GAMES_ALARM:
//                break;
            case GAMES_LIST:
                db.beginTransaction();
                try {
                    id = db.insert(GamesTable.TABLE, GamesTable.ALARM, values);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
//            case NEWS_LIST:
//                id = db.insert("News", null, values);
//                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
//        throw new UnsupportedOperationException("Insert not supported. Use bulkInsert.");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    /**
     * clears all news table, or games in games table where alarm column are false, or time > currenttime.
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (MATCHER.match(uri)) {
            case GAMES_LIST:
                db.delete(GamesTable.TABLE, GamesTable.ALARM + " = ? OR "
                        + GamesTable.TIME + " > ?", new String[]{"false", "STRFTIME(%s, 'now')"});
                break;
            case GAMES_ALARM:
                break;
            case NEWS_LIST:
                db.delete(NewsTable.TABLE, null, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return 0;
    }

    /**
     * update an alarm column of the row in games table after click. ContentResolver notifies loader
     * from GamesListFragment with GAMES_ALARM Uri.
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (MATCHER.match(uri)) {
            case GAMES_LIST:
                break;
            case GAMES_ALARM:
                db.beginTransaction();
                try {
                    db.update(GamesTable.TABLE, values, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case NEWS_LIST:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }



}

