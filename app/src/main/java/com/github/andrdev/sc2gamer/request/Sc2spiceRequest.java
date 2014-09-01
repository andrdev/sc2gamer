package com.github.andrdev.sc2gamer.request;


import com.github.andrdev.sc2gamer.JsoupHelper;
import com.github.andrdev.sc2gamer.database.GamesTable;
import com.octo.android.robospice.request.SpiceRequest;

import java.util.List;


public class Sc2spiceRequest extends SpiceRequest<List> {
    private final String mAction;

    public Sc2spiceRequest(String action) {
        super(List.class);
        mAction = action;
    }

    @Override
    public List loadDataFromNetwork() {
        List contentValues;
        if (mAction.equals(GamesTable.TABLE)) {
            contentValues = JsoupHelper.getGamesLinks();

        } else {
            contentValues = JsoupHelper.getNews();
        }
        return contentValues;
    }
}

