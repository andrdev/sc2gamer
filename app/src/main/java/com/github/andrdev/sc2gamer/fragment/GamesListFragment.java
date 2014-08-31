package com.github.andrdev.sc2gamer.fragment;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.andrdev.sc2gamer.JsoupHelper;
import com.github.andrdev.sc2gamer.LogosDownloader;
import com.github.andrdev.sc2gamer.R;
import com.github.andrdev.sc2gamer.adapter.GameRowAdapter;
import com.github.andrdev.sc2gamer.database.GamesTable;
import com.github.andrdev.sc2gamer.database.Sc2provider;
import com.github.andrdev.sc2gamer.request.GameInfoRequest;
import com.github.andrdev.sc2gamer.request.GameLinksRequest;
import com.github.andrdev.sc2gamer.service.AlarmCreatorService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
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
    private GameRowAdapter mSimpleCursorAdapter;
    private MenuItem mRefreshButton;
    private LogosDownloader mThumbThread;
    private LinkedList<String> mGamesLinks = new LinkedList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mSimpleDateFormat.setTimeZone(TimeZone.getDefault());
        mThumbThread = new LogosDownloader(new Handler(), getActivity().getCacheDir());
        mThumbThread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSimpleCursorAdapter = new GameRowAdapter
                (getActivity(), R.layout.row_game, null, mGamesColumns, mGamesFields, 0, mThumbThread);
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

    @Override
    public void onStart() {
        super.onStart();
        mSpiceManager.start(getActivity());
        if (!mGamesLinks.isEmpty()) {
            mSpiceManager.addListenerIfPending
                    (ContentValues.class, "gameLinks " + mGamesLinks.size(), new GameInfoRequestListener());
        } else {
            mSpiceManager.addListenerIfPending
                    (LinkedList.class, GamesTable.TABLE, new GamesLinksRequestListener());
        }
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll
                    (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("Dree", "scroll " + firstVisibleItem + " " + visibleItemCount + " " + totalItemCount);
                if (JsoupHelper.getGamesPageCount() != 0
                        && firstVisibleItem + visibleItemCount >= totalItemCount - 5
                        && !mIsRefreshing) {
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
            if (isNetworkAvailable()) {
                JsoupHelper.setGamesPageCount(0);
                refresh();
            } else {
                Toast.makeText(getActivity(), "Check network connection.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean refresh() {
        if (!JsoupHelper.isLastGamesPage()) {
            getGames();
            return true;
        }
        return false;
    }

    private void getGames() {
        refreshAction(true);
        GameLinksRequest gms = new GameLinksRequest(LinkedList.class);
        mSpiceManager.execute(gms, GamesTable.TABLE, DurationInMillis.ALWAYS_EXPIRED,
                new GamesLinksRequestListener());
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
        intent.putExtra(AlarmCreatorService.ALARM_EVENT, AlarmCreatorService.CLICK);
        intent.putExtra(GamesTable._ID, cursor.getInt(0));
        intent.putExtra(GamesTable.TIME, cursor.getInt(5));
        intent.putExtra(GamesTable.ALARM, cursor.getString(6));
        getActivity().startService(intent);
    }

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
        mSimpleCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSimpleCursorAdapter.swapCursor(null);
    }

    private void getGamesInfo() {
        if (!mGamesLinks.isEmpty()) {
            GameInfoRequest gms = new GameInfoRequest(ContentValues.class, mGamesLinks.removeFirst());
            mSpiceManager.execute(gms, "gameLinks " + mGamesLinks.size(), DurationInMillis.ALWAYS_EXPIRED,
                    new GameInfoRequestListener());
        } else {
            refreshAction(false);
        }
    }

    private final class GamesLinksRequestListener implements PendingRequestListener<LinkedList> {
        @Override
        public void onRequestFailure(SpiceException e) {
            refreshAction(false);
            Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestSuccess(LinkedList gamesLinks) {
            if (!gamesLinks.isEmpty()) {
                saveData(gamesLinks);
            }
        }

        private void saveData(LinkedList<String> gamesLinks) {
            mGamesLinks = gamesLinks;
            if (JsoupHelper.getGamesPageCount() == 2) {
                getActivity().getContentResolver().delete(Sc2provider.CONTENT_URI_GAMES, null, null);
            }
            getGamesInfo();
        }

        public void onRequestNotFound() {
            refreshAction(false);
        }
    }

    private final class GameInfoRequestListener implements PendingRequestListener<ContentValues> {
        @Override
        public void onRequestFailure(SpiceException e) {
            getGamesInfo();
        }

        @Override
        public void onRequestSuccess(ContentValues contentValues) {
            getActivity().getContentResolver().insert
                    (Sc2provider.CONTENT_URI_GAMES, contentValues);
            getGamesInfo();
        }

        @Override
        public void onRequestNotFound() {
            getGamesInfo();
        }
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
        mIsRefreshing = state;
        setRefreshActionButtonState();
    }

    // changing refresh button state
    private void setRefreshActionButtonState() {
        if (mRefreshButton != null) {
            if (mIsRefreshing) {
                mRefreshButton.setActionView(R.layout.actionbar_progress);
            } else {
                mRefreshButton.setActionView(null);
            }
        }
    }
}
