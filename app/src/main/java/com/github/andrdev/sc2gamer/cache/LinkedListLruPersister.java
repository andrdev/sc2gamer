package com.github.andrdev.sc2gamer.cache;

import com.octo.android.robospice.persistence.memory.LruCacheObjectPersister;

import java.util.LinkedList;


public class LinkedListLruPersister extends LruCacheObjectPersister<LinkedList> {
    public LinkedListLruPersister(int size) {
        super(LinkedList.class, new LinkedListLruCache(size));
    }
}
