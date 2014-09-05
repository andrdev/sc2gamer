package com.github.andrdev.sc2gamer.request;

import com.github.andrdev.sc2gamer.network.NetHelper;
import com.octo.android.robospice.request.SpiceRequest;

import java.util.LinkedList;


public class GameLinksRequest extends SpiceRequest<LinkedList> {
    public GameLinksRequest(Class<LinkedList> clazz) {
        super(clazz);
    }

    @Override
    public LinkedList<String> loadDataFromNetwork() {
        LinkedList<String> gameLinks = NetHelper.getGamesLinks();
        return gameLinks;
    }
}
