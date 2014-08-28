package com.github.andrdev.sc2gamer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import java.io.File;
import java.io.IOException;

/**
 * Created by taiyokaze on 8/27/14.
 */
public class AlarmDialogActivity extends SherlockActivity {
    TextView mTeamName1;
    TextView mTeamName2;
    ImageView mTeamLogo1;
    ImageView mTeamLogo2;
    File mImageFolder;
    Button mOk;
    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_dialog);
        init();
        populate();
        startSound();
    }
    void init(){
        mTeamName1 = (TextView) findViewById(R.id.dialogName1);
        mTeamName2 = (TextView) findViewById(R.id.dialogName2);
        mTeamLogo1 = (ImageView) findViewById(R.id.dialogLogo1);
        mTeamLogo2 = (ImageView) findViewById(R.id.dialogLogo2);
        mOk = (Button) findViewById(R.id.buttonAlert);
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                finish();
            }
        });
    }
    void populate(){
        String alarmId = String.valueOf(getIntent().getIntExtra("AlarmId", 1));
        mImageFolder = this.getCacheDir();
        Cursor cursor = this.getContentResolver().query(Sc2provider.CONTENT_URI_ALARMS,
                null, GamesTable._ID + " = ?", new String[]{alarmId}, null);
        cursor.moveToFirst();
        mTeamName1.setText(cursor.getString(1));
        mTeamName2.setText(cursor.getString(3));
        Bitmap bitmap = BitmapFactory
                .decodeFile(mImageFolder + "/" + cursor.getString(2));
        mTeamLogo1.setImageBitmap(bitmap);
        bitmap = BitmapFactory
                .decodeFile(mImageFolder + "/" + cursor.getString(4));
        mTeamLogo2.setImageBitmap(bitmap);
    }

    void startSound() {
        try {
            Uri alert = Uri.parse(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_alarm_sound", ""));
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
