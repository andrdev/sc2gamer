package com.github.andrdev.sc2gamer.fragment;


import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;

import com.github.andrdev.sc2gamer.R;
import com.github.andrdev.sc2gamer.database.GamesTable;
import com.github.andrdev.sc2gamer.database.NewsTable;
import com.github.andrdev.sc2gamer.network.NetHelper;
import com.github.andrdev.sc2gamer.request.ArticleTextRequest;
import com.github.andrdev.sc2gamer.request.GameInfoRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Fragment that shows article, after click on a row from NewsListFragment.
 */
public class ArticleFragment extends WebViewFragment {
    private String mLink;
    private final SpiceManager mSpiceManager = new SpiceManager(UncachedSpiceService.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mLink = getSherlockActivity().getIntent().getStringExtra(NewsTable.LINK);
        if (mLink == null) {
            mLink = getArguments().getString(NewsTable.LINK);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mSpiceManager.start(getSherlockActivity());
        mSpiceManager.addListenerIfPending
                (ArrayList.class, "Article", new ArticleRequestListener());
    }

    @Override
    public void onStop() {
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    void getHtml() {
        ArticleTextRequest gms = new ArticleTextRequest(ArrayList.class, mLink);
        mSpiceManager
                .execute(gms, mLink,
                        DurationInMillis.ALWAYS_EXPIRED, new ArticleRequestListener());
    }

    private final class ArticleRequestListener implements PendingRequestListener<ArrayList> {
        @Override
        public void onRequestNotFound() {

        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {

        }

        @Override
        public void onRequestSuccess(ArrayList s) {
            ArrayList<String> ss = s;
            String text = "";
            try {
                 text = generateHtmlPage(ss.get(0), ss.get(1));
            }catch (IOException ioe){                Log.d("DREE", "" + ioe.toString());            }
            Log.d("DREE", "" + text.length());
            getWebView().loadData(text, "text/html","UTF-8");
        }
        private String generateHtmlPage(String headerCss, String newsBody) throws IOException {
            Log.d("DREE", "" + 1);
            InputStreamReader fis = new InputStreamReader(getActivity().getAssets().open("templ.html"));
            BufferedReader htmlTemplate = new BufferedReader(fis);
            StringBuilder sb = new StringBuilder();
            String temp = null;
            while ((temp = htmlTemplate.readLine()) != null) {
                sb.append(temp);
                Log.d("DREE", "" + 1);
            }
            String finalPage = sb.toString();
            finalPage = finalPage.replace("xxxHEADxxx", headerCss);
            finalPage = finalPage.replace("xxxBODYxxx", newsBody);
            return finalPage;
        }
    }
}
