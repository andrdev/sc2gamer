package com.github.andrdev.sc2gamer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.andrdev.sc2gamer.GamesListFragment;
import com.github.andrdev.sc2gamer.NewsListFragment;


public class MainActivity extends SherlockFragmentActivity implements
        TabsFragment.TabsCallbacks, NewsListFragment.NewsCallbacks{
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
            bundle.putString("Link", articleLink);
            fragment = new ArticleFragment();
            fragment.setArguments(bundle);
            mFragmentManager.beginTransaction()
                    .replace(R.id.fragmentDetail, fragment, tag).commit();
        }

    }

    public static final String APP_PREFRENCES = "sc2gamerPrefs";

    private ViewPager mPager;
    private FragmentManager mFragmentManager;
     static final String REFRESH_STATE = "refreshGamesState";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mFragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_info);
        if (mFragmentManager.findFragmentById(R.id.fragmentMain) == null) {
            if (findViewById(R.id.fragmentDetail) == null) {
                mPager = (ViewPager) findViewById(R.id.pager);
                mPager.setAdapter(new TabsAdapter(mFragmentManager));
            } else {
                mFragmentManager.beginTransaction()
                        .add(R.id.fragmentMain, new TabsFragment())
                        .commit();
                if(mFragmentManager.findFragmentById(R.id.fragmentDetail)==null){
                    loadDetailsFragment(0);}
            }
            Log.d("dree act", "cr");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void loadDetailsFragment(int position) {
        SherlockListFragment fragment;
        String tag;
        if(position==0) {
            fragment = new GamesListFragment();
            tag="Games";
        } else {
            fragment = new NewsListFragment();
            tag="News";
        }
        mFragmentManager.beginTransaction().replace(R.id.fragmentDetail, fragment, tag).commit();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.menu_preference) {
            Intent intent = new Intent(this, PreferenceActivity.class);
            startActivity(intent);
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**TabsAdapter is a PagerAdapter that hosts GamesListFragment and
     * NewsListFragment by default, and an ArticleFragment after clickф on a row in NewsListFragment.
     * */
    static private class TabsAdapter extends FragmentPagerAdapter {
        private SherlockListFragment[] mListFragments = {new GamesListFragment(), new NewsListFragment()};
        private static final String TAB_TITLES[] = {"Games", "News"};

        public TabsAdapter(FragmentManager fm) {
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

//    @Override
//    public void onBackPressed() {
//        mFm = getSupportFragmentManager();
//        if (mFm.getBackStackEntryCount() > 0) {
//            mFm.popBackStack();
//        } else {
//            super.onBackPressed();
//        }
//    }
}
