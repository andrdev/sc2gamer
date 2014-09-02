package com.github.andrdev.sc2gamer.fragment;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockFragment;


abstract class WebViewFragment extends SherlockFragment {
    private WebView mWebView;
    private boolean mIsAvailable = false;

    WebViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mWebView != null) {
            mWebView.destroy();
        }
        mWebView = new WebView(getSherlockActivity());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        mIsAvailable = true;
        mWebView.loadUrl(getPage());
        return mWebView;
    }

    @Override
    public void onPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        mIsAvailable = false;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        mIsAvailable = false;
        super.onDestroy();
    }

    public WebView getWebView() {
        return mIsAvailable ? mWebView : null;
    }

    abstract String getPage();
}
