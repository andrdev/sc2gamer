package com.github.andrdev.sc2gamer.request;

import android.content.ContentValues;
import android.util.Log;

import com.github.andrdev.sc2gamer.JsoupHelper;
import com.octo.android.robospice.request.SpiceRequest;

/**
 * Created by taiyokaze on 8/31/14.
 */
public class GameInfoRequest extends SpiceRequest<ContentValues> {
    private final String mUrl;

    public GameInfoRequest(Class<ContentValues> clazz, String url) {
        super(clazz);
        mUrl = url;
    }

    @Override
    public ContentValues loadDataFromNetwork() throws Exception {
        ContentValues cv = JsoupHelper.getGameInfo(mUrl);
        return cv;
    }
}
