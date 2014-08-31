package com.github.andrdev.sc2gamer.activity;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.andrdev.sc2gamer.fragment.ArticleFragment;


public class ArticleActivity extends SingleFragmentActivity {

    @Override
    public SherlockFragment createFragment() {
        return new ArticleFragment();
    }
}
