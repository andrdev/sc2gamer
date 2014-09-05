package com.github.andrdev.sc2gamer.request;

import android.content.ContentValues;

import com.github.andrdev.sc2gamer.network.NetHelper;
import com.octo.android.robospice.request.SpiceRequest;


public class GameInfoRequest extends SpiceRequest<ContentValues> {
    private final String mUrl;

    public GameInfoRequest(Class<ContentValues> clazz, String url) {
        super(clazz);
        mUrl = url;
    }

    @Override
    public ContentValues loadDataFromNetwork() throws Exception {
        ContentValues cv = NetHelper.getGameInfo(mUrl);
        return cv;
    }
}
