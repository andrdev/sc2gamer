package com.github.andrdev.sc2gamer.request;

import com.github.andrdev.sc2gamer.JsoupHelper;
import com.github.andrdev.sc2gamer.database.GamesTable;
import com.github.andrdev.sc2gamer.database.NewsTable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by taiyokaze on 8/31/14.
 */
public class NewsListRequest extends BaseListRequest {

    public NewsListRequest(Class<LinkedList> clazz) {
        super(clazz);
    }

    @Override
    public LinkedList loadDataFromNetwork() throws Exception {
        List contentValues = JsoupHelper.getNews();
        return new LinkedList();
    }
}
