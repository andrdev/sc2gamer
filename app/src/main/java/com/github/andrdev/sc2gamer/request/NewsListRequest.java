package com.github.andrdev.sc2gamer.request;

import com.github.andrdev.sc2gamer.network.NetHelper;

import java.util.LinkedList;


public class NewsListRequest extends BaseListRequest {

    public NewsListRequest(Class<LinkedList> clazz) {
        super(clazz);
    }

    @Override
    public LinkedList loadDataFromNetwork() throws Exception {
        LinkedList contentValues = NetHelper.getNews();
        return contentValues;
    }
}
