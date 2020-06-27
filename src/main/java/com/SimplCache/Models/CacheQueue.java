package com.SimplCache.Models;

import java.util.List;
import java.util.Map;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */

interface CacheQueue {
    void remove(String key);

    List<String> getCurrentKeys();

    String dequeue();

    void enqueue(CacheObject cacheObject);

    int size();

    void clear();

    CacheObject getObject(String key);

    Map<String, CacheObject> map();


}
