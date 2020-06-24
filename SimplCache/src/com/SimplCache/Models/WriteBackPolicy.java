package com.SimplCache.Models;

import com.SimplCache.DatabaseInterfaces.CacheDB;
import com.SimplCache.DatabaseInterfaces.PersistentDB;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
class WriteBackPolicy<T> {

    Long writeBackInterval;
    CacheDB<T> cacheDB;
    PersistentDB<T> persistentDB;
    CacheQueue cacheQueue;
    ScheduledExecutorService scheduledExecutorService;

    WriteBackPolicy(Long writeBackInterval, CacheDB<T> cacheDB, PersistentDB<T> persistentDB, CacheQueue cacheQueue) {
        this.writeBackInterval = writeBackInterval;
        this.cacheDB = cacheDB;
        this.persistentDB = persistentDB;
        this.cacheQueue = cacheQueue;

        startThread();
    }


    synchronized private void startThread() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        Runnable writeback = () -> write_back();
        scheduledExecutorService.scheduleAtFixedRate(writeback, writeBackInterval, writeBackInterval, TimeUnit.SECONDS);

    }

    synchronized private void write_back() {
        CacheObject cacheObject;
        List<String> keys = cacheQueue.getCurrentKeys();
        for (String key : keys) {
            cacheObject = cacheQueue.getObject(key);
            if (cacheObject.getDirtyBit()) {
                persistentDB.putValueinPersistentDB(key, cacheDB.getValueFromCacheDB(key));
            }
        }


    }

    synchronized void close() {
        scheduledExecutorService.shutdown();
    }
}
