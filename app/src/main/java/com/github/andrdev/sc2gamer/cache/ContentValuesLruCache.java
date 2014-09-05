package com.github.andrdev.sc2gamer.cache;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.os.Build;

import com.octo.android.robospice.persistence.memory.CacheItem;
import com.octo.android.robospice.persistence.memory.LruCache;


public class ContentValuesLruCache extends LruCache<Object, CacheItem<ContentValues>> {
    public ContentValuesLruCache(int maxSize) {
        super(maxSize);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected int sizeOf(Object key, CacheItem<ContentValues> value) {
        return 1;
    }
}
