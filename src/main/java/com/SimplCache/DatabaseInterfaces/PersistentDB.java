package com.SimplCache.DatabaseInterfaces;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
public interface PersistentDB<T> {
    void putValueInPersistentDB(String key, T t);

    T getValueFromPersistentDB(String key);

    void closeDB();
}
