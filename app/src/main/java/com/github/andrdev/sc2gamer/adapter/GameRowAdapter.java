package com.github.andrdev.sc2gamer.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.andrdev.sc2gamer.R;
import com.github.andrdev.sc2gamer.database.GamesTable;
import com.github.andrdev.sc2gamer.network.LogoDownloader;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Cursor adapter for GamesListFragment.
 */
public class GameRowAdapter extends SimpleCursorAdapter {
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
    private LogoDownloader mThumbThread;
    private Context mContext;

    public GameRowAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags, LogoDownloader handler) {
        super(context, layout, c, from, to, flags);
        mSimpleDateFormat.setTimeZone(TimeZone.getDefault());
        mThumbThread = handler;
        mContext = context;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        paintRow(view, cursor);
    }

    private void paintRow(View view, Cursor cursor) {
        ViewHolder viewHolder;
        if (view.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.timeText = (TextView) view.findViewById(R.id.game_start_time);
            viewHolder.logo1 = (ImageView) view.findViewById(R.id.team1_logo);
            viewHolder.logo2 = (ImageView) view.findViewById(R.id.team2_logo);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        mThumbThread.queueThumbnail(viewHolder.logo1, cursor.getString(2));
        mThumbThread.queueThumbnail(viewHolder.logo2, cursor.getString(4));
        viewHolder.timeText.setText(mSimpleDateFormat.format(cursor.getLong(5) * 1000));
        if (cursor.getString(6).equals(GamesTable.SET_ALARM)) {
            viewHolder.timeText.setBackgroundColor(0xff550000);
        } else {
            viewHolder.timeText.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void setTeamLogo(ImageView imageView, String url) {
        Intent intent = new Intent(mContext, LogoDownloader.class);
    }

    static class ViewHolder {
        TextView timeText;
        ImageView logo1;
        ImageView logo2;
    }
}
