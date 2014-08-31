package com.github.andrdev.sc2gamer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.actionbarsherlock.app.SherlockListFragment;
import com.github.andrdev.sc2gamer.fragment.GamesListFragment;
import com.github.andrdev.sc2gamer.fragment.NewsListFragment;


public class PagerTabAdapter extends FragmentPagerAdapter {
    private SherlockListFragment[] mListFragments = {new GamesListFragment(), new NewsListFragment()};
    private static final String TAB_TITLES[] = {"Games", "News"};

    public PagerTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return mListFragments[i];
    }

    @Override
    public int getCount() {
        return mListFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }
}
