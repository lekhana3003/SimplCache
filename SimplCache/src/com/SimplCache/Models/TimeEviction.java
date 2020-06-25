package com.SimplCache.Models;

import com.SimplCache.DatabaseInterfaces.CacheDB;
import com.SimplCache.DatabaseInterfaces.PersistentDB;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
class TimeEviction<T> {


    public TimeEviction(PersistentDB<T> persistentDB, CacheDB<T> cacheDB, Long cacheTimeLimit, CacheQueue cacheQueue) {
        this.persistentDB = persistentDB;
        this.cacheDB = cacheDB;
        this.cacheTimeLimit = cacheTimeLimit;
        this.cacheQueue = cacheQueue;
        startThread();
    }

    PersistentDB<T> persistentDB;
    private CacheDB<T> cacheDB;
    private Long cacheTimeLimit;
    private final CacheQueue cacheQueue;
    private ScheduledExecutorService scheduledExecutorService;
    private HashMap<String, ScheduledFuture<String>> tasks;

    void startThread() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        tasks = new HashMap<>();
    }

    synchronized void addtoThread(String key, T t) {
        cacheDB.putValueInCacheDB(key, t);
        ScheduledFuture<String> result = scheduledExecutorService.schedule(new Task(key), cacheTimeLimit, TimeUnit.SECONDS);
        tasks.put(key, result);
    }

    void remove(String key) {
        ScheduledFuture<String> result = tasks.get(key);
        if (!(result.isDone())) {
            result.cancel(true);
            tasks.remove(key);
        }


    }

    void close() {
        for (Map.Entry<String, ScheduledFuture<String>> mapEntry : tasks.entrySet()) {
            if (!(mapEntry.getValue().isDone())) {
                mapEntry.getValue().cancel(true);
            }
        }
        scheduledExecutorService.shutdown();

    }

    void updateThread(String key, T t1) {
        remove(key);
        addtoThread(key, t1);
    }

    private class Task implements Callable<String> {
        String key;

        Task(String key) {
            this.key = key;
        }

        @Override
        public String call() {
            synchronized (cacheQueue) {
                T t1 = cacheDB.getValueFromCacheDB(key);
                if (t1 != null) {
                    if (cacheQueue.getCurrentKeys().contains(key)) {
                        if (cacheQueue.getObject(key).getDirtyBit()) {
                            persistentDB.putValueinPersistentDB(key, t1);
                            cacheDB.removeValueFromCacheDB(key);
                            cacheQueue.remove(key);
                        }
                    }
                }
                return "";
            }
        }
    }
}

