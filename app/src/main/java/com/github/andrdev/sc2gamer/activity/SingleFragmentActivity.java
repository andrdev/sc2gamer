package com.github.andrdev.sc2gamer.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;


public abstract class SingleFragmentActivity extends SherlockFragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(android.R.id.content);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }

    public abstract SherlockFragment createFragment();
}
