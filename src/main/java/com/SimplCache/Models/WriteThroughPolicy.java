package com.SimplCache.Models;

import com.SimplCache.DatabaseInterfaces.PersistentDB;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
public class WriteThroughPolicy<T> {
    PersistentDB<T> persistentDB;

    public WriteThroughPolicy(PersistentDB<T> persistentDB) {
        this.persistentDB = persistentDB;
    }

    public void putWriteThrough(String key, T t) {
        persistentDB.putValueinPersistentDB(key, t);

    }
}
