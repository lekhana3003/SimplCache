package com.SimplCache.Models;

import com.SimplCache.DatabaseInterfaces.CacheDB;
import com.SimplCache.DatabaseInterfaces.PersistentDB;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
 class LRUEviction<T> {

    CacheQueue cacheQueue;
    CacheDB<T> cacheDB;
    PersistentDB<T> persistentDB;
    LRUEviction(CacheQueue cacheQueue,CacheDB<T> cacheDB,PersistentDB<T> persistentDB)
    {
        this.cacheQueue=cacheQueue;
        this.cacheDB=cacheDB;
        this.persistentDB=persistentDB;
    }

    void notFull(CacheObject cacheObject,T t)
    {
        cacheQueue.remove(cacheObject.getKey());
        cacheQueue.enqueue(cacheObject);
        cacheDB.putValueInCacheDB(cacheObject.getKey(),t);
    }

   String full (CacheObject cacheObject,T t)
    {
        String oldkey=cacheQueue.dequeue();

        persistentDB.putValueInPersistentDB(oldkey, cacheDB.getValueFromCacheDB(oldkey));
        cacheDB.removeValueFromCacheDB(oldkey);
        cacheQueue.enqueue(cacheObject);
        cacheDB.putValueInCacheDB(cacheObject.getKey(),t);
        return oldkey;
    }

 void getLRU(String key)
    {
        CacheObject cacheObject=cacheQueue.getObject(key);
        cacheQueue.remove(cacheObject.getKey());
        cacheQueue.enqueue(cacheObject);
    }
}
