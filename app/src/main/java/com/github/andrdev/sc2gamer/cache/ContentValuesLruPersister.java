package com.github.andrdev.sc2gamer.cache;

import android.content.ContentValues;

import com.octo.android.robospice.persistence.memory.LruCacheObjectPersister;


public class ContentValuesLruPersister extends LruCacheObjectPersister<ContentValues>  {
    public ContentValuesLruPersister(int size) {
        super(ContentValues.class, new ContentValuesLruCache(size));
    }
}
