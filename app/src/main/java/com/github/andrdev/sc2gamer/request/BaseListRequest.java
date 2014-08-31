package com.github.andrdev.sc2gamer.request;

import com.octo.android.robospice.request.SpiceRequest;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by taiyokaze on 8/31/14.
 */
abstract public class BaseListRequest extends SpiceRequest<LinkedList> {
    public BaseListRequest(Class<LinkedList> clazz) {
        super(clazz);
    }


}
