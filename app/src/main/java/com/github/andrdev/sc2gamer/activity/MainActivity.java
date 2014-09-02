package com.github.andrdev.sc2gamer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.andrdev.sc2gamer.R;
import com.github.andrdev.sc2gamer.adapter.PagerTabAdapter;
import com.github.andrdev.sc2gamer.database.GamesTable;
import com.github.andrdev.sc2gamer.database.NewsTable;
import com.github.andrdev.sc2gamer.fragment.ArticleFragment;
import com.github.andrdev.sc2gamer.fragment.GamesListFragment;
import com.github.andrdev.sc2gamer.fragment.NewsListFragment;
import com.github.andrdev.sc2gamer.fragment.TabsFragment;


public class MainActivity extends SherlockFragmentActivity implements
        TabsFragment.TabsCallbacks, NewsListFragment.NewsCallbacks {

    private ViewPager mPager;
    private FragmentManager mFragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mFragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_info);
        if (mFragmentManager.findFragmentById(R.id.fragmentMain) == null) {
            loadMainFragment();
        }
    }

    //loads pager for >600 dp and two-pane layout for 600+
    private void loadMainFragment() {
        if (findViewById(R.id.fragmentDetail) == null) {
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(new PagerTabAdapter(mFragmentManager));
        } else {
            mFragmentManager.beginTransaction()
                    .add(R.id.fragmentMain, new TabsFragment())
                    .commit();
            if (mFragmentManager.findFragmentById(R.id.fragmentDetail) == null) {
                loadDetailsFragment(0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.menu_preference) {
            Intent intent = new Intent(this, PreferenceActivity.class);
            startActivity(intent);
        }
        return super.onMenuItemSelected(featureId, item);
    }

    //Callback method from TabsFragment.
    @Override
    public void loadDetailsFragment(int position) {
        SherlockListFragment fragment;
        String tag;
        if (position == 0) {
            fragment = new GamesListFragment();
            tag = GamesTable.TABLE;
        } else {
            fragment = new NewsListFragment();
            tag = NewsTable.TABLE;
        }
        mFragmentManager.beginTransaction().replace(R.id.fragmentDetail, fragment, tag).commit();
    }

    //Callback method from NewsFragment.
    @Override
    public void loadArticle(String articleLink) {
        String tag = "Article";
        SherlockFragment fragment;
        if (findViewById(R.id.fragmentDetail) == null) {
            Intent i = new Intent(this, ArticleActivity.class);
            i.putExtra(NewsTable.LINK, articleLink);
            startActivity(i);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(NewsTable.LINK, articleLink);
            fragment = new ArticleFragment();
            fragment.setArguments(bundle);
            mFragmentManager.beginTransaction()
                    .replace(R.id.fragmentDetail, fragment, tag).commit();
        }
    }
}
