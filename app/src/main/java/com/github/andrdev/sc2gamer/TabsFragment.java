package com.github.andrdev.sc2gamer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

import java.util.ArrayList;

/**
 * Created by taiyokaze on 8/25/14.
 */
public class TabsFragment extends SherlockListFragment {
    TabsCallbacks mTabsCallbacks;
    ArrayList<String> mFragmentsList = new ArrayList<String>();
    ArrayAdapter<String> adapter;
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
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mFragmentsList);
        setListAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mTabsCallbacks = (TabsCallbacks)activity;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mTabsCallbacks.loadDetailsFragment(position);
    }

    interface TabsCallbacks {
        public void loadDetailsFragment(int position);
    }
}