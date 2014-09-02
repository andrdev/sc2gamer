package com.github.andrdev.sc2gamer.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.github.andrdev.sc2gamer.database.GamesTable;
import com.github.andrdev.sc2gamer.database.Sc2provider;
import com.github.andrdev.sc2gamer.receiver.AlarmReceiver;

/**
 * Service, that responsible for setting and canceling alarms on upcoming games.
 * BOOT and CLICK are constants for actions, that sent with intent.
 */

public class AlarmCreatorService extends IntentService {
    public final static int BOOT = 1;
    public final static int CLICK = 2;
    public final static String ALARM_EVENT = "alarm_event";
    private AlarmManager alarmManager;
    private Intent alarmIntent;
    public final static String ALARM_ID = "alarmId";
    private final static String ALARM_TIME_PREF = "pref_alarm_time";

    public AlarmCreatorService() {
        super("AlarmCreatorService");
    }

    /**
     * Reads ALARM_EVENT extra from the intent, and proceed accordingly.
     * BOOT sent from the BootAlarmReceiver class, and reset alarms that still must be played.
     * CLICK sent from GamesListFragment class.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(this, AlarmReceiver.class);
        int action = intent.getIntExtra(ALARM_EVENT, 0);
        switch (action) {
            case BOOT:
                bootAlarmStart();
                break;
            case CLICK:
                ContentValues cv = new ContentValues();
                String alarmAction = intent.getStringExtra(GamesTable.ALARM);
                int id = intent.getIntExtra(GamesTable._ID, -1);
                if (alarmAction != null && alarmAction.equals(GamesTable.DEFAULT_ALARM)) {
                    alarmAction = GamesTable.SET_ALARM;
                    int time = intent.getIntExtra(GamesTable.TIME, -1);
                    setAlarm(id, time);
                } else {
                    alarmAction = GamesTable.DEFAULT_ALARM;
                    cancelAlarm(id);
                }
                cv.put(GamesTable.ALARM, alarmAction);
                getApplicationContext().getContentResolver().update(Sc2provider.CONTENT_URI_ALARMS, cv,
                        GamesTable._ID + " = ?", new String[]{String.valueOf(id)});
                break;
            default:
                Toast.makeText(this, "Unknown event or action", Toast.LENGTH_LONG).show();
                break;
        }
    }

    //getting all alarms, and calling setAlarm method on each
    private void bootAlarmStart() {
        String[] columns = {GamesTable._ID, GamesTable.TIME};
        Cursor cursor = this.getContentResolver().query(Sc2provider.CONTENT_URI_ALARMS,
                columns, GamesTable.ALARM + " = ?", new String[]{GamesTable.SET_ALARM}, null);
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    setAlarm(cursor.getInt(0), cursor.getInt(1));
                }
            }
        } finally {
            cursor.close();
        }
    }

    // sending alarm intent, only if alarmTime-earlyAlarmTime > current time
    private void setAlarm(int id, int gameTime) {
        long earlyAlarmTime = getEarlyAlarmTimeMillis();
        long alarmTime = gameTime * 1000 - earlyAlarmTime;
        if (alarmTime > System.currentTimeMillis()) {
            alarmIntent.putExtra(ALARM_ID, id);
            PendingIntent pi = PendingIntent.getBroadcast
                    (this, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);
        }
    }

    private long getEarlyAlarmTimeMillis() {
        String earlyAlarmPreference = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(ALARM_TIME_PREF, "0");
        long earlyAlarmTime = 0;
        if (!earlyAlarmPreference.isEmpty()) {
            earlyAlarmTime = 60 * 1000 * Integer.valueOf(earlyAlarmPreference);
        }
        return earlyAlarmTime;
    }

    private void cancelAlarm(int id) {
        PendingIntent pi = PendingIntent.getBroadcast
                (this, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pi);
    }
}



