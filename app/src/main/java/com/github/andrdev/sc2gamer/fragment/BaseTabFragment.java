package com.github.andrdev.sc2gamer.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.github.andrdev.sc2gamer.JsoupHelper;
import com.github.andrdev.sc2gamer.R;
import com.github.andrdev.sc2gamer.request.Sc2spiceRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

import java.util.List;


abstract class BaseTabFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter mSimpleCursorAdapter = createCursorAdapter();
    private static final SpiceManager mSpiceManager = new SpiceManager(UncachedSpiceService.class);
    private boolean mIsRefreshing = false;
    private MenuItem refreshButton;
    private final int mLoaderId = getLoader();
    private final String mRequestId = getRequestId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setRetainInstance(true);
        setListAdapter(mSimpleCursorAdapter);
        getLoaderManager().initLoader(mLoaderId, null, this);
        mSimpleCursorAdapter = createCursorAdapter();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    abstract SimpleCursorAdapter createCursorAdapter();

    abstract int getLoader();

    abstract PendingRequestListener getRequestListener();

    abstract String getRequestId();

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        refreshButton = menu.findItem(R.id.menu_refresh);
        setRefreshActionButtonState();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onStart() {
        super.onStart();
        mSpiceManager.start(getActivity());
        mSpiceManager.addListenerIfPending(List.class, mRequestId, getRequestListener());
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll
                    (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount - 5 && !mIsRefreshing) {
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

    void refreshList(String cl, ListRequestListener lrl) {
        Sc2spiceRequest gms = new Sc2spiceRequest(cl);
        mSpiceManager.execute(gms, cl, DurationInMillis.ALWAYS_EXPIRED, lrl);
    }

    private boolean refresh() {
        if (!JsoupHelper.isLastGamesPage()) {
            refreshAction(true);
            Sc2spiceRequest gms = new Sc2spiceRequest(mRequestId);
            mSpiceManager.execute(gms, mRequestId, DurationInMillis.ALWAYS_EXPIRED,
                    getRequestListener());
            return true;
        }
        return false;
    }

    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case 1:
                return new CursorLoader(getActivity(), getUri(), null, null, null, null);
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

    public abstract class ListRequestListener implements PendingRequestListener<List> {
        @Override
        public void onRequestFailure(SpiceException e) {
            refreshAction(false);
        }

        public void onRequestSuccess(List contentValues) {
            if (contentValues.size() > 0) {
                saveData(contentValues);
            }
            refreshAction(false);
        }

        private void saveData(List<ContentValues> contentValues) {
            if (isFirstPage()) {
                getActivity().getContentResolver().delete(getUri(), null, null);
            }
            getActivity().getContentResolver().bulkInsert(getUri(), contentValues.toArray(new ContentValues[contentValues.size()]));
        }

        @Override
        public void onRequestNotFound() {
            refreshAction(false);
        }
    }

    abstract Uri getUri();

    abstract boolean isFirstPage();

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

    private void setRefreshActionButtonState() {
        if (refreshButton != null) {
            if (mIsRefreshing) {
                refreshButton.setActionView(R.layout.actionbar_progress);
            } else {
                refreshButton.setActionView(null);
            }
        }
    }
}
