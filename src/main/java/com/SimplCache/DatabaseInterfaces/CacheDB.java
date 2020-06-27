package com.SimplCache.DatabaseInterfaces;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
public interface
CacheDB<T> {
    void putValueInCacheDB(String key, T value);

    void removeValueFromCacheDB(String key);

    void clearCache();

    T getValueFromCacheDB(String key);

    void closeDB();
}
