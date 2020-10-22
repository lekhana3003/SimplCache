package com.SimplCache.Models;

/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
class CacheObject {

    String key;
    boolean dirtyBit;

    String getKey() {
        return key;
    }

    boolean getDirtyBit() {
        return dirtyBit;
    }

    void setDirtyBit(boolean dirtyBit) {
        this.dirtyBit = dirtyBit;
    }

    CacheObject(String key, boolean dirtyBit) {
        this.key = key;
        this.dirtyBit = dirtyBit;
    }
}
