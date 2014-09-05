package com.github.andrdev.sc2gamer.service;

import android.app.Application;

import com.github.andrdev.sc2gamer.cache.ContentValuesLruPersister;
import com.github.andrdev.sc2gamer.cache.LinkedListLruPersister;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;


public class SglSpiceService extends SpiceService {
    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();
        ContentValuesLruPersister contentValuesPersister = new ContentValuesLruPersister(5);
        LinkedListLruPersister linkedListLruPersister = new LinkedListLruPersister(70);
        cacheManager.addPersister(linkedListLruPersister);
        cacheManager.addPersister(contentValuesPersister);
        return cacheManager;
    }
}
