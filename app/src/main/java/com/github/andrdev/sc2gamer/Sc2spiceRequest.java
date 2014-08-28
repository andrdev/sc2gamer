package com.github.andrdev.sc2gamer;


import android.content.ContentValues;
import com.octo.android.robospice.request.SpiceRequest;
import java.util.List;


public class Sc2spiceRequest extends SpiceRequest<List> {
    private final String mAction;

    public Sc2spiceRequest(String choose) {
        super(List.class);
        mAction = choose;
    }

    @Override
    public List<ContentValues> loadDataFromNetwork() {
        List<ContentValues> contentValues;
        if (mAction.equals(GamesTable.TABLE)) {
            contentValues = JsoupHelper.getGames();

        } else if (mAction.equals(NewsTable.TABLE)) {
            contentValues = JsoupHelper.getNews();
        } else {
            contentValues = JsoupHelper.getArticle(mAction);
        }
        return contentValues;
    }
}

