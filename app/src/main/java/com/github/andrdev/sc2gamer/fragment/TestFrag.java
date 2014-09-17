package com.github.andrdev.sc2gamer.fragment;

import android.net.Uri;
import android.support.v4.widget.SimpleCursorAdapter;

import com.github.andrdev.sc2gamer.request.BaseListRequest;

public class TestFrag extends BaseTabFragment {
    @Override
    SimpleCursorAdapter createCursorAdapter() {
        return null;
    }

    @Override
    String getSpiceRequestId() {
        return null;
    }

    @Override
    BaseListRequest getRequest() {
        return null;
    }

    @Override
    Uri getUri() {
        return null;
    }

    @Override
    boolean isFirstPage() {
        return false;
    }
}
