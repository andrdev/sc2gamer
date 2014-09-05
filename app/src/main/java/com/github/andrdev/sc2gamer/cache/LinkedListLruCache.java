package com.github.andrdev.sc2gamer.cache;


import android.annotation.TargetApi;
import android.os.Build;

import com.octo.android.robospice.persistence.memory.CacheItem;
import com.octo.android.robospice.persistence.memory.LruCache;

import java.util.LinkedList;


public class LinkedListLruCache extends LruCache<Object, CacheItem<LinkedList>> {
    public LinkedListLruCache(int maxSize) {
        super(maxSize);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected int sizeOf(Object key, CacheItem<LinkedList> value) {
        LinkedList data = value.getData();
        return data.size();
    }
}
