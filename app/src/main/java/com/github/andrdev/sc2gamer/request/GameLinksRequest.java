package com.github.andrdev.sc2gamer.request;

import com.github.andrdev.sc2gamer.NetHelper;

import java.util.LinkedList;


public class GameLinksRequest extends BaseListRequest {

    public GameLinksRequest(Class<LinkedList> clazz) {
        super(clazz);
    }

    @Override
    public LinkedList<String> loadDataFromNetwork() {
        LinkedList<String> gameLinks = NetHelper.getGamesLinks();
        return gameLinks;
    }
}
