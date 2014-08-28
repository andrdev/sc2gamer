package com.github.andrdev.sc2gamer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

import java.util.List;

/** NewsListFragment
 * */
public class NewsListFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int mLoaderId = 1;
    private final String[] mNewsColumns = {NewsTable.TITLE};
    private final int[] mNewsFields = {R.id.news_title};

    private SimpleCursorAdapter mSimpleCursorAdapter;
    private final SpiceManager mSpiceManager = new SpiceManager(UncachedSpiceService.class);
    private MenuItem mRefreshButton;
    private boolean mIsRefreshing = false;
    private NewsCallbacks mNewsCallbacks;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSimpleCursorAdapter = new SimpleCursorAdapter
                (getActivity(), R.layout.row_news, null, mNewsColumns, mNewsFields, 0);
        setListAdapter(mSimpleCursorAdapter);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(mLoaderId, null, this);
        setRetainInstance(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mNewsCallbacks = (NewsCallbacks)activity;
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
        mSpiceManager.addListenerIfPending
                (List.class, NewsTable.TABLE, new NewsListRequestListener());
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll
                    (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("Dree", "scroll " + firstVisibleItem + " " + visibleItemCount + " " + totalItemCount);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            if (isNetworkAvailable()) {
                JsoupHelper.setNewsPageCount(0);
                refresh();
            } else {
                Toast.makeText(getActivity(), "Check network connection.", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean refresh() {
        if(!JsoupHelper.isLastNewsPage()) {
            refreshAction(true);
            Sc2spiceRequest gms = new Sc2spiceRequest(NewsTable.TABLE);
            mSpiceManager.execute(gms, NewsTable.TABLE, DurationInMillis.ALWAYS_EXPIRED, new NewsListRequestListener());
            return true;
        }
        return false;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (isNetworkAvailable()) {
            loadArticle(position);
        } else {
            Toast.makeText(getActivity(), "Check network connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadArticle(int position) {
        Cursor cursor = (Cursor) mSimpleCursorAdapter.getItem(position);
        String articleLink = cursor.getString(2);
        mNewsCallbacks.loadArticle(articleLink);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case mLoaderId:
                return new CursorLoader(getActivity(), Sc2provider.CONTENT_URI_NEWS, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mSimpleCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mSimpleCursorAdapter.swapCursor(null);
    }

    private class NewsListRequestListener implements PendingRequestListener<List> {
        @Override
        public void onRequestFailure(SpiceException e) {
            refreshAction(false);
        }

        @Override
        public void onRequestSuccess(List contentValues) {
            if (contentValues.size() > 0) {
                saveData(contentValues);
            }
            refreshAction(false);
        }

        private void saveData(List<ContentValues> contentValues) {
            if (JsoupHelper.getNewsPageCount() == 0) {
                getActivity().getContentResolver().delete(Sc2provider.CONTENT_URI_NEWS, null, null);
            }
            getActivity().getContentResolver().bulkInsert
                    (Sc2provider.CONTENT_URI_NEWS, contentValues.toArray(new ContentValues[0]));
        }

        @Override
        public void onRequestNotFound() {
            refreshAction(false);
        }
    }

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
        if (mRefreshButton != null) {
            if (mIsRefreshing) {
                mRefreshButton.setActionView(R.layout.actionbar_progress);
            } else {
                mRefreshButton.setActionView(null);
            }
        }
    }

    interface NewsCallbacks {
        public void loadArticle(String news);
    }
}
