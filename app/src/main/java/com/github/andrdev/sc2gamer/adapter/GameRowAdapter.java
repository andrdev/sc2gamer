package com.github.andrdev.sc2gamer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.github.andrdev.sc2gamer.LogosDownloader;
import com.github.andrdev.sc2gamer.R;
import com.github.andrdev.sc2gamer.database.GamesTable;

import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class GameRowAdapter extends SimpleCursorAdapter {
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
    LogosDownloader mThumbThread;

    public GameRowAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags, LogosDownloader handler) {
        super(context, layout, c, from, to, flags);
        mSimpleDateFormat.setTimeZone(TimeZone.getDefault());
        mThumbThread = handler;
    }

    // setting date from db - get, convert, set
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        paintRow(view, cursor);
    }

    private void paintRow(View view, Cursor cursor) {
        ViewHolder viewHolder;
        if (view.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.game_start);
            viewHolder.logo1 = (ImageView) view.findViewById(R.id.team1_logo);
            viewHolder.logo2 = (ImageView) view.findViewById(R.id.team2_logo);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        mThumbThread.queueThumbnail(viewHolder.logo1, cursor.getString(2));
        mThumbThread.queueThumbnail(viewHolder.logo2, cursor.getString(4));
        viewHolder.text.setText(mSimpleDateFormat.format(cursor.getLong(5)));
        if (cursor.getString(6).equals(GamesTable.SET_ALARM)) {
            view.setBackgroundColor(0xff550000);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT); //alarm set - background green
        }
    }

    static class ViewHolder {
        TextView text;
        ImageView logo1;
        ImageView logo2;
    }
}
