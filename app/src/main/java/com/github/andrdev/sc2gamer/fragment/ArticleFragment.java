package com.github.andrdev.sc2gamer.fragment;


import android.os.Bundle;

import com.github.andrdev.sc2gamer.JsoupHelper;
import com.github.andrdev.sc2gamer.database.NewsTable;

/**
 * Fragment that shows article, after click on a row from NewsListFragment.
 */
public class ArticleFragment extends WebViewFragment {
    private String mArticleText;
    private String mLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mLink = getActivity().getIntent().getStringExtra(NewsTable.LINK);
        if (mLink == null) {
            mLink = getArguments().getString("Link");
        }
    }

    @Override
    String getPage() {
        return JsoupHelper.GAME_SITE + mLink;
    }
}
