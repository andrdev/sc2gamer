package com.github.andrdev.sc2gamer.activity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.github.andrdev.sc2gamer.R;
import com.github.andrdev.sc2gamer.database.GamesTable;
import com.github.andrdev.sc2gamer.database.Sc2provider;
import com.github.andrdev.sc2gamer.service.AlarmCreatorService;

import java.io.File;
import java.io.IOException;


public class AlarmDialogActivity extends SherlockActivity {
    private TextView mTeamName1;
    private TextView mTeamName2;
    private ImageView mTeamLogo1;
    private ImageView mTeamLogo2;
    private File mImageFolder;
    private Button mOk;
    private MediaPlayer mMediaPlayer;
    private final static String PREFERENCE_ALARM_SOUND = "pref_alarm_sound";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_dialog);
        initialize();
        populateViews();
        startSound();
    }

    void initialize() {
        mTeamName1 = (TextView) findViewById(R.id.dialogAlarmTeamName1);
        mTeamName2 = (TextView) findViewById(R.id.dialogAlarmTeamName2);
        mTeamLogo1 = (ImageView) findViewById(R.id.dialogAlarmTeamLogo1);
        mTeamLogo2 = (ImageView) findViewById(R.id.dialogAlarmTeamLogo2);
        mOk = (Button) findViewById(R.id.dialogAlarmButtonOk);
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer.release();
                finish();
            }
        });
    }

    void populateViews() {
        String alarmId = String.valueOf(getIntent().getIntExtra(AlarmCreatorService.ALARM_ID, 1));
        mImageFolder = this.getCacheDir();
        Cursor cursor = this.getContentResolver().query(Sc2provider.CONTENT_URI_ALARMS,
                null, GamesTable._ID + " = ?", new String[]{alarmId}, null);
        try {
            cursor.moveToFirst();
            mTeamName1.setText(cursor.getString(1));
            mTeamName2.setText(cursor.getString(3));
            Bitmap bitmap = BitmapFactory
                    .decodeFile(mImageFolder + "/" + cursor.getString(2));
            mTeamLogo1.setImageBitmap(bitmap);
            bitmap = BitmapFactory
                    .decodeFile(mImageFolder + "/" + cursor.getString(4));
            mTeamLogo2.setImageBitmap(bitmap);
        } finally {
            cursor.close();
        }
    }

    void startSound() {
        try {
            Uri alert = Uri.parse(PreferenceManager
                    .getDefaultSharedPreferences(this).getString(PREFERENCE_ALARM_SOUND, ""));
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(this, alert);
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException ioe) {
            Log.e("AlarmDialog", "error playing sound", ioe);
        }
    }

}
