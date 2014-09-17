package com.github.andrdev.sc2gamer.request;

import com.github.andrdev.sc2gamer.network.NetHelper;
import com.octo.android.robospice.request.SpiceRequest;

import java.util.ArrayList;


public class ArticleTextRequest extends SpiceRequest<ArrayList> {
    String mUrl;
    public ArticleTextRequest(Class<ArrayList> clazz, String url) {
        super(clazz);
        mUrl = url;
    }
    @Override
    public ArrayList loadDataFromNetwork() throws Exception {
        return NetHelper.getArticle(mUrl);
    }
}
