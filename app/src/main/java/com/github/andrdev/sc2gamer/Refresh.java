package com.github.andrdev.sc2gamer;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.andrdev.sc2gamer.database.GamesTable;
import com.github.andrdev.sc2gamer.database.Sc2Provider;
import com.github.andrdev.sc2gamer.network.NetHelper;
import com.github.andrdev.sc2gamer.request.GameInfoRequest;
import com.github.andrdev.sc2gamer.request.GameLinksRequest;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;

import java.util.LinkedList;

public class Refresh {

        private boolean mIsRefreshing = false;

    private MenuItem mRefreshButton;

    public void setmRefreshButton(MenuItem mRefreshButton) {
        this.mRefreshButton = mRefreshButton;
    }

        @Override
        public void onPrepareOptionsMenu(Menu menu) {
            super.onPrepareOptionsMenu(menu);
            mRefreshButton = menu.findItem(R.id.menu_refresh);
            setRefreshActionButtonState();
        }



                if (isNetworkAvailable()) {
                    NetHelper.setGamesPageCount(0);
                    refresh();

        private void refresh() {
            if (NetHelper.haveGamesPages()) {
                getGamesLinks();
            }
        }

        private void getGamesLinks() {
            refreshAction(true);
            GameLinksRequest gms = new GameLinksRequest(LinkedList.class);
            mSpiceManager.execute(gms, GamesTable.TABLE, DurationInMillis.ALWAYS_EXPIRED,
                    new GamesLinksRequestListener());
        }


        private void getGamesInfo() {
            if (!mGamesLinks.isEmpty()) {
                GameInfoRequest gms = new GameInfoRequest(ContentValues.class, mGamesLinks.removeFirst());
                mSpiceManager
                        .execute(gms, GAMES_CACHE_REQUEST_ID + mGamesLinks.size(),
                                DurationInMillis.ALWAYS_EXPIRED, new GameInfoRequestListener());
            } else {
                refreshAction(false);
            }
        }


            @Override
            public void onRequestSuccess(LinkedList gamesLinks) {
                if (gamesLinks != null) {
                    getGames(gamesLinks);
                } else {
                    refreshAction(false);
                }
            }

            private void getGames(LinkedList<String> gamesLinks) {
                mGamesLinks = gamesLinks;
                if (NetHelper.getGamesPageCount() == 2) {
                    getSherlockActivity().getContentResolver().delete(Sc2Provider.CONTENT_URI_GAMES, null, null);
                }
                getGamesInfo();
            }


            @Override
            public void onRequestFailure(SpiceException e) {
                getGamesInfo();
            }

            @Override
            public void onRequestSuccess(ContentValues contentValues) {
                getSherlockActivity().getContentResolver().insert
                        (Sc2Provider.CONTENT_URI_GAMES, contentValues);
                getGamesInfo();
            }


        private boolean isNetworkAvailable() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSherlockActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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

}
