package com.github.andrdev.sc2gamer;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/**
 * GameListFragment is a ListFragment that loads data from internet and db asynchronously.
 * Asynchronous work handled by LoaderManager.LoaderCallbacks for db interactions,
 * and Robospice for loading data from the internet. In GameListFragments onCreateView
 * method loader creates CursorAdapter and populates ListView from db, if data exists.
 * On refresh button click Robospice service is started, if successful - old data deleted
 * from the db and new is loaded. After load to the db loader is notified and repopulates
 * ListView. On fail - toast is displayed.
 * On row click intent is sent to the AlarmCreatorService with the data from the cursor.
 * It sets or cancels alarm. Also color of the row background is changed.
 */
public class GamesListFragment extends SherlockListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private final int mLoaderId = 2;
    private final String[] mGamesColumns = {GamesTable.TEAM1_NAME, GamesTable.TEAM2_NAME, GamesTable.TIME};
    private final int[] mGamesFields = {R.id.team1_name, R.id.team2_name, R.id.game_start};

    private final SpiceManager mSpiceManager = new SpiceManager(UncachedSpiceService.class);
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
    private boolean mIsRefreshing = false;
    private GamesAdapter mSimpleCursorAdapter;
    private MenuItem mRefreshButton;
    private LogosDownloader mThumbThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mSimpleDateFormat.setTimeZone(TimeZone.getDefault());
//        mSharedPreferences = getActivity().getSharedPreferences(MainActivity.APP_PREFRENCES,
//                Context.MODE_PRIVATE);
//        setRefreshState(false);
        mThumbThread = new LogosDownloader(new Handler(), getActivity().getCacheDir());
        mThumbThread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Dree", "crv " + JsoupHelper.getGamesPageCount());
        mSimpleCursorAdapter = new GamesAdapter
                (getActivity(), R.layout.row_games, null, mGamesColumns, mGamesFields, 0);
        setListAdapter(mSimpleCursorAdapter);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(mLoaderId, null, this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mRefreshButton = menu.findItem(R.id.menu_refresh);
        setRefreshActionButtonState();
    }

    //starting spicemanager, and checking for pending request
    @Override
    public void onStart() {
        super.onStart();
        mSpiceManager.start(getActivity());
        mSpiceManager.addListenerIfPending
                (List.class, GamesTable.TABLE, new GamesListRequestListener());
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll
                    (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("Dree","scroll "+firstVisibleItem+" "+visibleItemCount+" "+totalItemCount);
//                if (firstVisibleItem + visibleItemCount >= totalItemCount - 5&&!mSharedPreferences.getBoolean(REFRESH_STATE, false)) {
                if (JsoupHelper.getGamesPageCount()!=0
                        &&firstVisibleItem + visibleItemCount >= totalItemCount - 5&&!mIsRefreshing) {
                    refresh();
                }
            }
        });
    }
    //stopping spicemanager
    @Override
    public void onStop() {
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbThread.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbThread.quit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            if(isNetworkAvailable()){
                JsoupHelper.setGamesPageCount(0);
                refresh();
            } else {
                Toast.makeText(getActivity(), "Check network connection.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Create and execute request to the spiceservice
     */
    private boolean refresh() {
        if(!JsoupHelper.isLastGamesPage()){
        refreshAction(true);
        Sc2spiceRequest gms = new Sc2spiceRequest(GamesTable.TABLE);
        mSpiceManager.execute(gms, GamesTable.TABLE, DurationInMillis.ALWAYS_EXPIRED,
                new GamesListRequestListener());
            return true;
        }
        return false;
    }

    /**
     * On row click AlarmCreatorService is started by intent with data from the cursor.
     * It sets or cancels an alarm according to the Alarm column. After click row
     * is turning to set/unset state.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
          setAlarm(position);
    }

    private void setAlarm(int position) {
        Intent intent = new Intent(getActivity(), AlarmCreatorService.class);
        Cursor cursor = (Cursor) mSimpleCursorAdapter.getItem(position);
        //maybe i'm gonna change it to array or find other way
        intent.putExtra(AlarmCreatorService.ALARM_EVENT, AlarmCreatorService.CLICK);
        intent.putExtra(GamesTable._ID, cursor.getInt(0));
        intent.putExtra(GamesTable.TIME, cursor.getInt(5));
        intent.putExtra(GamesTable.ALARM, cursor.getString(6));
        getActivity().startService(intent);
    }


    //creating CursorLoader
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case mLoaderId:
                return new CursorLoader(getActivity(), Sc2provider.CONTENT_URI_GAMES, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d("Dree","loadf ");
        mSimpleCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSimpleCursorAdapter.swapCursor(null);
    }

    private final class GamesListRequestListener implements PendingRequestListener<List> {
        @Override
        public void onRequestFailure(SpiceException e) {
            Log.d("Dree","reqfa ");
            refreshAction(false);
            Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_LONG).show();
        }

        // deleting old data and saving new to db
        @Override
        public void onRequestSuccess(List contentValues) {
            if(contentValues.size()>0) {
                saveData(contentValues);
            }
            refreshAction(false);
        }

        private void saveData(List<ContentValues> contentValues) {
            Log.d("Dree","rewrite savdata");
            if(JsoupHelper.getGamesPageCount()==2){
                getActivity().getContentResolver().delete(Sc2provider.CONTENT_URI_GAMES, null, null);}
            Log.d("Dree","incr"+JsoupHelper.getGamesPageCount());

            getActivity().getContentResolver().bulkInsert
                        (Sc2provider.CONTENT_URI_GAMES, contentValues.toArray(new ContentValues[0]));
        }
        @Override
        public void onRequestNotFound() {
            refreshAction(false);
        }
    }

    private class GamesAdapter extends SimpleCursorAdapter {
        private GamesAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        // setting date from db - get, convert, set
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);
            paintRow(view, cursor);
        }

        private void paintRow(View view, Cursor cursor) {
            ViewHolder viewHolder;
            if(view.getTag() == null) {
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
    }

    static class ViewHolder {
        TextView text;
        ImageView logo1;
        ImageView logo2;
    }

    // checking internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void refreshAction(boolean state) {
//        setRefreshState(state);
        mIsRefreshing = state;
        setRefreshActionButtonState();
    }

//    private void setRefreshState(boolean state) {
//        mSharedPreferences.edit().putBoolean(REFRESH_STATE, state).commit();
//        Log.d("Dree","refrepr "+ state);
//    }
    // changing refresh button state
    private void setRefreshActionButtonState() {
        Log.d("DreeRef","setRf ");
        if (mRefreshButton != null) {
//            if (mSharedPreferences.getBoolean(REFRESH_STATE, false)) {
            if (mIsRefreshing) {
                mRefreshButton.setActionView(R.layout.actionbar_progress);
            } else {
                mRefreshButton.setActionView(null);
            }
        }
    }

}
