package com.SimplCache.Models;

import com.SimplCache.Exceptions.SaveStateException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.SimplCache.DatabaseInterfaces.CacheDB;
import com.SimplCache.DatabaseInterfaces.PersistentDB;

import java.util.HashMap;

/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
class SimplCacheAdapter<T> {
    static Gson gson;

    static <T> SimplCache<T> fetchSimplCache(String saveStateString, CacheDB<T> cacheDB, PersistentDB<T> persistentDB) {
        if (gson == null) {
            gson = new Gson();
        }
        try {
            JsonObject body = gson.fromJson(saveStateString, JsonObject.class);
            if (body == null) {
                throw new SaveStateException("String Error");
            }
            SimplCache<T> simplCache = createSimplCacheObject(body.get("SimplCache").getAsJsonObject(), cacheDB, persistentDB);
            if (simplCache == null) {
                throw new SaveStateException("Simpl Cache Parse error");
            }
            return buildQueue(simplCache, body.get("map").getAsJsonObject(), persistentDB);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static <T> SimplCache<T> createSimplCacheObject(JsonObject simplCacheObject, CacheDB<T> cacheDB, PersistentDB<T> persistentDB) {

        SimplCache.SimplCacheBuilder<T> builder = new SimplCache.SimplCacheBuilder<T>(cacheDB, persistentDB);
        builder.setPersistentDBType(SimplCache.PERSISTENT_DB_VALUE.PERSISTENTDB);
        if ("WRITE_THROUGH".equals(simplCacheObject.get("cacheType").getAsString())) {
            builder.setCacheTypeBuilder(SimplCache.CACHE_TYPES.WRITE_THROUGH);
        } else {
            builder.setCacheTypeBuilder(SimplCache.CACHE_TYPES.WRITE_BACK);
            builder.setWriteBackIntervalBuilder(Long.parseLong(simplCacheObject.get("writeBackInterval").getAsString()));

        }
        if ("NO_AUTO".equals(simplCacheObject.get("writeBackType").getAsString()))
            builder.setWriteBackType(SimplCache.WRITE_BACK_TYPE.NO_AUTO);
        else
            builder.setWriteBackType(SimplCache.WRITE_BACK_TYPE.AUTO);
        if ("TIME_EVICTION".equals(simplCacheObject.get("evictionType").getAsString())) {
            builder.setEvictionType(SimplCache.EVICTION_TYPES.TIME_EVICTION);
            builder.setCacheTimeLimit(Long.parseLong(simplCacheObject.get("cacheTimeLimit").getAsString()));

        } else {
            builder.setEvictionType(SimplCache.EVICTION_TYPES.LRU_EVICTION);
        }
        if ("OBJECTS_COUNT".equals(simplCacheObject.get("cacheMemoryType").getAsString())) {
            builder.setCacheMemoryType(SimplCache.MEMORY_TYPES.OBJECTS_COUNT);

        } else {
            builder.setCacheMemoryType(SimplCache.MEMORY_TYPES.OBJECTS_SIZE);
        }
        builder.setCacheSize(Long.parseLong(simplCacheObject.get("cacheSize").getAsString()));
        return builder.build();
    }

    synchronized private static <T> SimplCache<T> buildQueue(SimplCache<T> simplCache, JsonObject map, PersistentDB<T> persistentDB) {

        HashMap<String, CacheObject> mapObject = gson.fromJson(map, HashMap.class);
        T t;
        try {
            for (String key : mapObject.keySet()) {
                t = persistentDB.getValueFromPersistentDB(key);
                if (t == null) {
                    throw new SaveStateException("Persistent DB error");
                }
                simplCache.put(key, t, SimplCache.POLICY_CONTROL.WITHOUT_POLICY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return simplCache;

    }
}
