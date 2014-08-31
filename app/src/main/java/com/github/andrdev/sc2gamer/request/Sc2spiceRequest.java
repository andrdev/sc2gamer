package com.github.andrdev.sc2gamer.request;


import com.github.andrdev.sc2gamer.JsoupHelper;
import com.github.andrdev.sc2gamer.database.GamesTable;
import com.github.andrdev.sc2gamer.database.NewsTable;
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

        } else if (mAction.equals(NewsTable.TABLE)) {
            contentValues = JsoupHelper.getNews();
        } else {
            contentValues = JsoupHelper.getArticle(mAction);
        }
        return contentValues;
    }
}

