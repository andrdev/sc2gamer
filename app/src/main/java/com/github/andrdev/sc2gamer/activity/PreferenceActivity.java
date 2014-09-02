package com.github.andrdev.sc2gamer.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.github.andrdev.sc2gamer.R;
import com.github.andrdev.sc2gamer.fragment.StockPreferenceFragment;

import java.util.List;


public class PreferenceActivity extends SherlockPreferenceActivity {
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            addPreferencesFromResource(R.xml.preference);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        if (StockPreferenceFragment.class.getName().equals(fragmentName)) {
            return true;
        }
        return false;
    }
}
