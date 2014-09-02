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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.andrdev.sc2gamer.LogoDownloader;
import com.github.andrdev.sc2gamer.NetHelper;
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

import java.util.LinkedList;

/**
 * GameListFragment is a ListFragment that loads data from internet and db asynchronously.
 * Asynchronous work handled by ContentProvider + LoaderManager.LoaderCallbacks for db interactions,
 * and Robospice for loading data from the internet. In GameListFragments onCreateView
 * method loader creates CursorAdapter and populates ListView from db, if data exists.
 * <p/>
 * old data deleted
 * from the db and new is loaded. After load to the db loader is notified and repopulates
 * ListView. On fail - toast is displayed.
 * On row click intent is sent to the AlarmCreatorService with the data from the cursor.
 * It sets or cancels alarm. Also color of the row background is changed.
 */
public class GamesListFragment extends SherlockListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //fields for cursor adapter
    private final static int mLoaderId = 2;
    private final String[] mGamesColumns = {GamesTable.TEAM1_NAME, GamesTable.TEAM2_NAME};
    private final int[] mGamesFields = {R.id.team1_name, R.id.team2_name};
    //fields for RoboSpice
    private final SpiceManager mSpiceManager = new SpiceManager(UncachedSpiceService.class);
    private static final String GAMES_CACHE_REQUEST_ID = "gameLinks";

    private boolean mIsRefreshing = false;
    private GameRowAdapter mGameRowAdapter;
    private MenuItem mRefreshButton;
    private LogoDownloader mLogoDownloaderThread;
    private LinkedList<String> mGamesLinks = new LinkedList<String>();

    /**
     * LogoDownloader that performs team logo downloading and setting is started
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mLogoDownloaderThread = new LogoDownloader(new Handler(), getSherlockActivity().getCacheDir());
        mLogoDownloaderThread.start();
    }

    /**
     * Initiating Loader with mLoaderId. Creating and setting cursor adapter for list.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mGameRowAdapter = new GameRowAdapter
                (getSherlockActivity(), R.layout.row_game, null, mGamesColumns, mGamesFields, 0, mLogoDownloaderThread);
        setListAdapter(mGameRowAdapter);
        getSherlockActivity().getSupportLoaderManager().initLoader(mLoaderId, null, this);
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Finding refresh button view.
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mRefreshButton = menu.findItem(R.id.menu_refresh);
        setRefreshActionButtonState();
    }

    /**
     * SpiceManager starts, and perform check for pending intents. If mGamesLinks is not empty - it checks
     * for GameInfoRequest, and for GamesLinksRequest otherwise. setOnScrollListener is set.
     */
    @Override
    public void onStart() {
        super.onStart();
        mSpiceManager.start(getSherlockActivity());
        if (!mGamesLinks.isEmpty()) {
            mSpiceManager.addListenerIfPending
                    (ContentValues.class, GAMES_CACHE_REQUEST_ID + mGamesLinks.size(), new GameInfoRequestListener());
        } else {
            mSpiceManager.addListenerIfPending
                    (LinkedList.class, GamesTable.TABLE, new GamesLinksRequestListener());
        }
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            /**
             * Launches refresh only if refresh action button was clicked on this launch
             * or refresh is not taking place right now.
             */
            @Override
            public void onScroll
            (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (NetHelper.getGamesPageCount() != 0
                        && firstVisibleItem + visibleItemCount >= totalItemCount - 10
                        && !mIsRefreshing) {
                    refresh();
                }
            }
        });
    }


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
        mLogoDownloaderThread.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLogoDownloaderThread.quit();
    }

    /**
     * Handles refresh action button click. Checks network, and starts refresh method on positive response.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            if (isNetworkAvailable()) {
                NetHelper.setGamesPageCount(0);
                refresh();
            } else {
                Toast.makeText(getSherlockActivity(), "Check network connection.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks if there are some games pages left. Starts getGamesLinks method on positive.
     */
    private void refresh() {
        if (NetHelper.haveGamesPages()) {
            getGamesLinks();
        }
    }

    /**
     * Sets action state to true. Executes RoboSpice request to get links for each game.
     */
    private void getGamesLinks() {
        refreshAction(true);
        GameLinksRequest gms = new GameLinksRequest(LinkedList.class);
        mSpiceManager.execute(gms, GamesTable.TABLE, DurationInMillis.ALWAYS_EXPIRED,
                new GamesLinksRequestListener());
    }

    /**
     * On row click AlarmCreatorService is started by intent with data from the cursor.
     * It sets or cancels an alarm according to the Alarm column.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        setAlarm(position);
    }

    private void setAlarm(int position) {
        Intent intent = new Intent(getSherlockActivity(), AlarmCreatorService.class);
        Cursor cursor = (Cursor) mGameRowAdapter.getItem(position);
        intent.putExtra(AlarmCreatorService.ALARM_EVENT, AlarmCreatorService.CLICK);
        intent.putExtra(GamesTable._ID, cursor.getInt(0));
        intent.putExtra(GamesTable.TIME, cursor.getInt(5));
        intent.putExtra(GamesTable.ALARM, cursor.getString(6));
        getSherlockActivity().startService(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case mLoaderId:
                return new CursorLoader
                        (getSherlockActivity(), Sc2provider.CONTENT_URI_GAMES, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mGameRowAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGameRowAdapter.swapCursor(null);
    }

    /**
     * If list of mGamesLinks is not empty - GameInfoRequest executed, otherwise refreshAction
     * ends by calling refreshAction with false flag.
     */
    private void getGamesInfo() {
        if (!mGamesLinks.isEmpty()) {
            GameInfoRequest gms = new GameInfoRequest(ContentValues.class, mGamesLinks.removeFirst());
            mSpiceManager
                    .execute(gms, GAMES_CACHE_REQUEST_ID + mGamesLinks.size(), DurationInMillis.ALWAYS_EXPIRED,
                            new GameInfoRequestListener());
        } else {
            refreshAction(false);
        }
    }

    private final class GamesLinksRequestListener implements PendingRequestListener<LinkedList> {
        /**
         * Ends refreshAction
         */
        @Override
        public void onRequestFailure(SpiceException e) {
            refreshAction(false);
            Toast.makeText(getSherlockActivity(), "Failed to load", Toast.LENGTH_LONG).show();
        }

        /**
         * If there are game links - calls getGames, ends refreshAction on empty gamesLinks.
         *
         * @param gamesLinks list of links on the upcoming games
         */
        @Override
        public void onRequestSuccess(LinkedList gamesLinks) {
            if (!gamesLinks.isEmpty()) {
                getGames(gamesLinks);
            } else {
                refreshAction(false);
            }
        }

        /**
         * Passes the reference of a list of games to the mGamesLinks, clears db from games without
         * alarm. Calls getGameInfo.
         */
        private void getGames(LinkedList<String> gamesLinks) {
            mGamesLinks = gamesLinks;
            if (NetHelper.getGamesPageCount() == 2) {
                getSherlockActivity().getContentResolver().delete(Sc2provider.CONTENT_URI_GAMES, null, null);
            }
            getGamesInfo();
        }

        /**
         * Ends refreshAction
         */
        @Override
        public void onRequestNotFound() {
            refreshAction(false);
        }
    }

    private final class GameInfoRequestListener implements PendingRequestListener<ContentValues> {
        @Override
        public void onRequestFailure(SpiceException e) {
            getGamesInfo();
        }

        /**
         * Insert game into db and calls getGameInfo.
         */
        @Override
        public void onRequestSuccess(ContentValues contentValues) {
            getSherlockActivity().getContentResolver().insert
                    (Sc2provider.CONTENT_URI_GAMES, contentValues);
            getGamesInfo();
        }

        @Override
        public void onRequestNotFound() {
            getGamesInfo();
        }
    }

    /**
     * Checking network connection.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSherlockActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * Changes flag on refreshing state. Calls setRefreshActionButtonState.
     */
    private void refreshAction(boolean state) {
        mIsRefreshing = state;
        setRefreshActionButtonState();
    }

    /**
     * Reads refresh state flag and changes refresh action button state view.
     */
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
