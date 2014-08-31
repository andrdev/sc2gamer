package com.github.andrdev.sc2gamer.request;

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

        return new LinkedList();
    }
}
