package com.github.andrdev.sc2gamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Created by taiyokaze on 8/19/14.
 */
public class ArticleActivity extends SingleFragmentActivity {


    @Override
    SherlockFragment createFragment() {
        return new ArticleFragment();
    }

}
