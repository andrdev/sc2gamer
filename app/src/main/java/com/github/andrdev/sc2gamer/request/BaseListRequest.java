package com.github.andrdev.sc2gamer.request;

import com.octo.android.robospice.request.SpiceRequest;

import java.util.LinkedList;


abstract class BaseListRequest extends SpiceRequest<LinkedList> {
    BaseListRequest(Class<LinkedList> clazz) {
        super(clazz);
    }
}
