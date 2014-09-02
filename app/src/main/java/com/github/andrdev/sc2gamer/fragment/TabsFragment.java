package com.github.andrdev.sc2gamer.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

import java.util.ArrayList;


public class TabsFragment extends SherlockListFragment {
    private TabsCallbacks mTabsCallbacks;
    private ArrayList<String> mFragmentsList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mFragmentsList.add("Games");
        mFragmentsList.add("News");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new ArrayAdapter(getSherlockActivity(), android.R.layout.simple_list_item_1, mFragmentsList);
        setListAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mTabsCallbacks = (TabsCallbacks) activity;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mTabsCallbacks.loadDetailsFragment(position);
    }

    public interface TabsCallbacks {
        public void loadDetailsFragment(int position);
    }
}
