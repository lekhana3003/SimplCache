package com.SimplCache.Models;

import java.util.*;
import java.util.stream.Collectors;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
class CacheQueueImpl implements CacheQueue {
    private Queue<String> queue = new LinkedList<>();
    private HashMap<String, CacheObject> map = new HashMap<>();

    @Override
    public void remove(String key) {
        queue.remove(key);
        map.remove(key);
    }

    @Override
    public List<String> getCurrentKeys() {
        return queue.stream().collect(Collectors.toList());
    }

    public Map<String, CacheObject> map() {
        return map;
    }


    @Override
    public String dequeue() {
        String key = queue.poll();
        map.remove(key);
        return key;
    }

    @Override
    public void enqueue(CacheObject cacheObject) {
        queue.add(cacheObject.getKey());
        map.put(cacheObject.getKey(), cacheObject);
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public void clear() {
        queue.clear();
        map.clear();
    }

    @Override
    public CacheObject getObject(String key) {
        return map.get(key);
    }
}
