package com.github.andrdev.sc2gamer.request;

import android.util.Log;

import com.github.andrdev.sc2gamer.JsoupHelper;

import java.util.LinkedList;

/**
 * Created by taiyokaze on 8/31/14.
 */
public class NewsListRequest extends BaseListRequest {

    public NewsListRequest(Class<LinkedList> clazz) {
        super(clazz);
    }

    @Override
    public LinkedList loadDataFromNetwork() throws Exception {
        LinkedList contentValues = JsoupHelper.getNews();
        return contentValues;
    }
}
