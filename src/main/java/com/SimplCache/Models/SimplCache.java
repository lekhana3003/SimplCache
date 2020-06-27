package com.SimplCache.Models;

import com.SimplCache.DatabaseInterfaces.CacheDB;
import com.SimplCache.DatabaseInterfaces.PersistentDB;
import com.SimplCache.Utilities.SizeOfObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
public class SimplCache<T> {
    enum PERSISTENT_DB_VALUE {
        PERSISTENTDB,
        NO_PERSISTENTDB
    }

    public enum CACHE_TYPES {
        WRITE_THROUGH,
        WRITE_BACK
    }

    public enum EVICTION_TYPES {
        TIME_EVICTION,
        LRU_EVICTION,
    }

    public enum MEMORY_TYPES {
        OBJECTS_COUNT,
        OBJECTS_SIZE
    }

    public enum WRITE_BACK_TYPE {
        AUTO,
        NO_AUTO
    }

    public enum WRITEBACKPARAMETER {
        WITH_WRITE_BACK,
        WITHOUT_WRITE_BACK
    }

    public enum POLICY_CONTROL {
        WITH_POLICY,
        WITHOUT_POLICY
    }

    private PERSISTENT_DB_VALUE persistentDBType;
    private CACHE_TYPES cacheType;
    private EVICTION_TYPES evictionType;
    private MEMORY_TYPES cacheMemoryType;
    private WRITE_BACK_TYPE writeBackType;

    private transient PersistentDB<T> persistentDB;
    private transient CacheDB<T> cacheDB;
    private Long cacheSize;

    private Long cacheTimeLimit;
    private transient WriteBackPolicy<T> writeBackPolicy;
    private transient final CacheQueue cacheQueue;
    private transient LRUEviction<T> lruEviction;
    private transient WriteThroughPolicy<T> writeThroughPolicy;
    private transient TimeEviction<T> timeEviction;
    private Long objectSize=-1L;
    private Long writeBackInterval;




    /**
     * This method return the type of cache set to the simplcache object
     * @see CACHE_TYPES
     * @return
     */
    public CACHE_TYPES getCacheType() {
        return cacheType;
    }




    /**
     * This method return the type of eviction policy set to the simplcache object
     * @see EVICTION_TYPES
     * @return
     */
    public EVICTION_TYPES getEvictionType() {
        return evictionType;
    }




    /**
     * This methods return the type of cache memory is used to maintain the cache.
     * @see MEMORY_TYPES
     * @return
     */
    public MEMORY_TYPES getCacheMemoryType() {
        return cacheMemoryType;
    }





    /**
     * This method return the maximum cache memory size set to the simplcache object.
     * It returns count if memory type is OBJECT_COUNT and Kilobytes if memory type is OBJECT_SIZE
     * @return
     */
    public Long getCacheSize() {
        return cacheSize;
    }





    /**
     * This method return the current size of the cache.
     * For the object size option of memory type the javaagent vm option should be enabled
     * @return long size in term of count or Kilo Bytes
     */
    public Long getCurrentCacheSize()
    {


        if(cacheMemoryType==MEMORY_TYPES.OBJECTS_COUNT)
        return Integer.toUnsignedLong(cacheQueue.size());
        else{
            if(objectSize==-1)
            {
                return -1L;
            }
            else
            {
                return (this.objectSize*cacheQueue.size())/1000;
            }
        }

    }






    /**
     * This method return the time limit set on each cache item when the time eviction policy is enabled.
     * @return long time limit
     */
    public Long getCacheTimeLimit() {
        return cacheTimeLimit;
    }






    /**
     * This method is used to build the SimplCache object from the string returned by the save state function.
     * It is used to store the state of cache whenever required. It uses a basic encryptor which is the default encryptor
     * Note: Default encryptor does not guarantee any security.
     * @param saveStateString
     * @param cacheDB
     * @param persistentDB
     * @param <T>
     * @return
     */
    public static <T> SimplCache<T> buildFromSaveState(String saveStateString, CacheDB<T> cacheDB, PersistentDB<T> persistentDB) {
        SimplCacheEncryptor simplCacheEncryptor=null;
        try {
            simplCacheEncryptor= new SimplCacheEncryptorImpl();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        try {
            return SimplCacheAdapter.fetchSimplCache(simplCacheEncryptor.decrypt(saveStateString), cacheDB, persistentDB);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }










    /**
     * This method is used to build the SimplCache object from the string returned by the save state function.
     * It is used to store the state of cache whenever required.
     * This method accepts an implementation of SimplCacheEncryptor which has to overriden to provide encryption and decryption
     * @see SimplCacheEncryptor
     * @param saveStateString
     * @param cacheDB
     * @param persistentDB
     * @param <T>
     * @return
     */
    public static <T> SimplCache<T> buildFromSaveState(String saveStateString, CacheDB<T> cacheDB, PersistentDB<T> persistentDB, SimplCacheEncryptor simplCacheEncryptor) {

        return SimplCacheAdapter.fetchSimplCache(simplCacheEncryptor.decrypt(saveStateString), cacheDB, persistentDB);

    }


    private SimplCache(PERSISTENT_DB_VALUE persistentDBType, CACHE_TYPES cacheType, EVICTION_TYPES evictionType, MEMORY_TYPES cacheMemoryType, PersistentDB<T> persistentDB, CacheDB<T> cacheDB, Long cacheSize, Long cacheTimeLimit, Long writeBackInterval, WRITE_BACK_TYPE writeBackType) {
        this.persistentDBType = persistentDBType;
        this.cacheType = cacheType;
        this.evictionType = evictionType;
        this.cacheMemoryType = cacheMemoryType;
        this.persistentDB = persistentDB;
        this.cacheDB = cacheDB;
        this.cacheSize = cacheSize;
        this.cacheTimeLimit = cacheTimeLimit;
        this.writeBackType = writeBackType;
        this.writeBackInterval=writeBackInterval;

        /* Intailizations for the object*/
        this.cacheQueue = new CacheQueueImpl();
        lruEviction = new LRUEviction(cacheQueue, cacheDB, persistentDB);

        if (cacheType == CACHE_TYPES.WRITE_THROUGH && persistentDBType == PERSISTENT_DB_VALUE.PERSISTENTDB) {
            this.writeThroughPolicy = new WriteThroughPolicy<>(persistentDB);
        }
        if (cacheType == CACHE_TYPES.WRITE_BACK && writeBackType == WRITE_BACK_TYPE.AUTO) {
            this.writeBackPolicy = new WriteBackPolicy<>(writeBackInterval, cacheDB, persistentDB, cacheQueue);
        }
        if (evictionType == EVICTION_TYPES.TIME_EVICTION) {
            this.timeEviction = new TimeEviction<T>(persistentDB, cacheDB, cacheTimeLimit, cacheQueue);
        }
    }

    public static class SimplCacheBuilder<T> {
        private PERSISTENT_DB_VALUE persistentDBType = PERSISTENT_DB_VALUE.NO_PERSISTENTDB;
        private CACHE_TYPES cacheType = CACHE_TYPES.WRITE_THROUGH;
        private EVICTION_TYPES evictionType = EVICTION_TYPES.LRU_EVICTION;
        private MEMORY_TYPES cacheMemoryType = MEMORY_TYPES.OBJECTS_COUNT;
        private WRITE_BACK_TYPE writeBackType = WRITE_BACK_TYPE.NO_AUTO;
        private PersistentDB<T> persistentDB;
        private CacheDB<T> cacheDB;
        private Long cacheSize = 50L;
        private Long cacheTimeLimit=600L;
        private Long writeBackInterval = 300L;
        private int cacheFlag=0;
        private int evictionFlag=0;

        void setPersistentDBType(PERSISTENT_DB_VALUE persistentDBType) {
            this.persistentDBType = persistentDBType;
        }

        void setCacheTypeBuilder(CACHE_TYPES cacheType) {
            this.cacheType = cacheType;
        }

        void setEvictionType(EVICTION_TYPES evictionType) {
            this.evictionType = evictionType;
        }

        void setCacheMemoryType(MEMORY_TYPES cacheMemoryType) {
            this.cacheMemoryType = cacheMemoryType;
        }

        void setWriteBackType(WRITE_BACK_TYPE writeBackType) {
            this.writeBackType = writeBackType;
        }


        void setCacheSize(Long cacheSize) {
            this.cacheSize = cacheSize;
        }

        void setCacheTimeLimit(Long cacheTimeLimit) {
            this.cacheTimeLimit = cacheTimeLimit;
        }

        void setWriteBackIntervalBuilder(Long writeBackInterval) {
            this.writeBackInterval = writeBackInterval;
        }

        @Override
        public String toString() {
            return "SimplCacheBuilder{" +
                    "persistentDBType=" + persistentDBType +
                    ", cacheType=" + cacheType +
                    ", evictionType=" + evictionType +
                    ", cacheMemoryType=" + cacheMemoryType +
                    ", writeBackType=" + writeBackType +
                    ", persistentDB=" + persistentDB +
                    ", cacheDB=" + cacheDB +
                    ", cacheSize=" + cacheSize +
                    ", cacheTimeLimit=" + cacheTimeLimit +
                    ", writeBackInterval=" + writeBackInterval +
                    ", cacheFlag=" + cacheFlag +
                    ", evictionFlag=" + evictionFlag +
                    '}';
        }

        /**
         * This method is used set the cache memory type.
         * Accepts Memory types Enum
         * @see MEMORY_TYPES
         * There are two memory types:
         *  OBJECT_COUNT: Cache Size is maintained in terms of the number of objects in the cache.
         *  OBJECT_SIZE: Cache Size is maintained in terms of the size of objects occupied in memory.
         * @param memory
         * @param cacheSize (object count or size in kilobytes)
         * @return
         */
        public SimplCacheBuilder<T> setCacheMemoryProperties(MEMORY_TYPES memory, long cacheSize) {

            this.cacheMemoryType = memory;

            if (cacheSize < 0) {
                throw new IllegalArgumentException(Long.toString(cacheSize));
            }

            if (memory == MEMORY_TYPES.OBJECTS_SIZE) {

                cacheSize = cacheSize * 1000;
            }
            this.cacheSize = cacheSize;
            return this;
        }








        /**
         * This methode is used to set the type of cache.
         * There are two types:
         * WRITE_THROUGH: The data is simultaneously updated to cache and memory.
         * WRITE_BACK:The data is updated only in the cache and updated into the memory in later time.
         * @see CACHE_TYPES
         * @param cacheType
         * @return
         */
        public SimplCacheBuilder<T> setCacheType(CACHE_TYPES cacheType)
        {

            cacheFlag=1;
         if(cacheType==CACHE_TYPES.WRITE_BACK)
         {
             this.writeBackType = WRITE_BACK_TYPE.NO_AUTO;
             this.cacheType = CACHE_TYPES.WRITE_BACK;
         }
         else{
             this.cacheType=CACHE_TYPES.WRITE_THROUGH;
         }
         return this;
        }






        /**
         * This method is used to set the interval at which automated write back occurs.
         * By setting write back interval, the data will be written into persistent DB after the given interval regularly.
         * @param duration
         * @param timeUnit
         * @return
         */
        public SimplCacheBuilder<T> setWriteBackInterval(long duration, TimeUnit timeUnit) {
            if((cacheFlag==1 && this.cacheType==CACHE_TYPES.WRITE_BACK)||cacheFlag==0) {
                if (duration < 0) {
                    throw new IllegalArgumentException(Long.toString(duration));
                }
                this.writeBackType = WRITE_BACK_TYPE.AUTO;
                this.writeBackInterval = timeUnit.toSeconds(duration);
                this.cacheType = CACHE_TYPES.WRITE_BACK;
            }
            return this;
        }







        /**
         * This method is used to set the eviction policy
         * There are two types of policy:
         * LRU_EVICTION: Least recently used cache object is evicted
         * TIME_EVICTION: The cache object is evicted after given time limit.
         * @see EVICTION_TYPES
         * @param evictionPolicy
         * @return
         */
        public SimplCacheBuilder<T> setEvictionPolicy(EVICTION_TYPES evictionPolicy)
        {
            evictionFlag=1;
            this.evictionType=evictionPolicy;
            return this;
        }








        /**
         * This method is used to set time interval after which cache object is evicted from the cache.
         * @param duration
         * @param timeUnit
         * @return
         */
        public SimplCacheBuilder<T> setTimeEvictionInterval(long duration, TimeUnit timeUnit) {
            if ((evictionFlag == 1 && this.evictionType == EVICTION_TYPES.TIME_EVICTION) || evictionFlag == 0) {
                if (duration < 0) {
                    throw new IllegalArgumentException(Long.toString(duration));
                }
                this.evictionType = EVICTION_TYPES.TIME_EVICTION;
                this.cacheTimeLimit = timeUnit.toSeconds(duration);
        }
            return this;
        }







        /**
         * This method is used to build the SimplCache object.
         * SimplCache object requires CacheDB and PersistentDB implemented objects.
         * @see CacheDB
         * @see PersistentDB
         * @param cacheDB
         * @param persistentDB
         */
        public SimplCacheBuilder(CacheDB<T> cacheDB, PersistentDB<T> persistentDB) {
            this.cacheDB = cacheDB;
            this.persistentDBType = PERSISTENT_DB_VALUE.PERSISTENTDB;
            this.persistentDB = persistentDB;

        }







        /**
         * This method builds and return SimplCache Object
         * @return
         */
        public SimplCache<T> build() {
            return new SimplCache<T>(this.persistentDBType, this.cacheType, this.evictionType, this.cacheMemoryType, this.persistentDB, this.cacheDB, this.cacheSize, this.cacheTimeLimit, this.writeBackInterval, this.writeBackType);
        }
    }








    /**
     * This method is used to put the object in cache according to the cache type and eviction policies.
     * @param key
     * @param object
     */
    synchronized public void put(String key, T object) {
        POLICY_CONTROL policy_control = POLICY_CONTROL.WITH_POLICY;
        put(key, object, policy_control);
    }







    /**
     * This method is used to put the object in cache and also into the persistentDB according to the cache type and eviction policies.
     * @param key
     * @param object
     * @param policy_control
     */
    synchronized public void put(String key, T object, POLICY_CONTROL policy_control) {
        CacheObject cacheObject;
        String oldkey;
        try {
            if (cacheQueue != null)  {
                synchronized(cacheQueue) {
                    if (cacheDB != null) {
                        if (persistentDB != null) {

                            if (cacheQueue.getCurrentKeys().contains(key)) {
                                cacheObject = cacheQueue.getObject(key);
                                if (policy_control == POLICY_CONTROL.WITH_POLICY) {
                                    cacheObject.setDirtyBit(true);
                                }

                                    lruEviction.notFull(cacheObject, object);
                                    if (evictionType == EVICTION_TYPES.TIME_EVICTION) {
                                        timeEviction.updateThread(key,object);
                                    }


                            } else {
                                if (policy_control == POLICY_CONTROL.WITH_POLICY) {
                                    cacheObject = new CacheObject(key, true);
                                } else {
                                    cacheObject = new CacheObject(key, false);
                                }
                                if (checkSize(object)) {
                                    lruEviction.notFull(cacheObject, object);
                                    if (evictionType == EVICTION_TYPES.TIME_EVICTION) {
                                        timeEviction.addtoThread(key, object);
                                    }
                                } else {
                                    oldkey = lruEviction.full(cacheObject, object);
                                    if (evictionType == EVICTION_TYPES.TIME_EVICTION) {
                                        timeEviction.addtoThread(key, object);
                                        timeEviction.remove(oldkey);
                                    }
                                }
                            }
                            if (persistentDBType == PERSISTENT_DB_VALUE.PERSISTENTDB && persistentDB != null) {
                                if (policy_control == POLICY_CONTROL.WITH_POLICY) {
                                    if (cacheType == CACHE_TYPES.WRITE_THROUGH) {
                                        writeThroughPolicy.putWriteThrough(key, object);
                                    }
                                }
                            }

                        } else {
                            throw new Exception("Persistent DB error");
                        }
                    } else {
                        throw new Exception("Cache DB error");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }












    /**
     * This method is used to check the cache size.
     * @param t
     * @return
     */
    private boolean checkSize(T t) {

        int size = cacheQueue.size();
        if (cacheMemoryType == MEMORY_TYPES.OBJECTS_SIZE) {
            if(this.objectSize==-1)
            {
                objectSize= SizeOfObject.sizeof(t);
            }
            return (objectSize * (size+1)) <= cacheSize;
        } else if (cacheMemoryType == MEMORY_TYPES.OBJECTS_COUNT) {
            return (1 + size) <= cacheSize;
        }
        return false;
    }









    /**
     * This method is used fetch the cache object from cache.
     * If the object is not present the object is fetched from persistentDB
     * @param key
     * @return
     */
    public T get(String key) throws Exception {
        T t = null;
        if (cacheDB != null) {
            if (cacheQueue.getCurrentKeys().contains(key)) {
                t = cacheDB.getValueFromCacheDB(key);
                if (evictionType == EVICTION_TYPES.LRU_EVICTION) {
                    lruEviction.getLRU(key);
                }
            } else {


                    t = persistentDB.getValueFromPersistentDB(key);
                    if (t == null) {
                        throw new Exception("Object not found in Persistent DB");

                    }
                    this.put(key, t, POLICY_CONTROL.WITHOUT_POLICY);


            }
        }

        return t;
    }









    /**
     * This method is used to write the cache items into persistentDB whenever required.
     */
    public void writeBack() {


        if (cacheType == CACHE_TYPES.WRITE_BACK) {
            CacheObject cacheObject;
            List<String> keys = cacheQueue.getCurrentKeys();
            for (String key : keys) {
                cacheObject = cacheQueue.getObject(key);
                if (cacheObject.getDirtyBit()) {
                    persistentDB.putValueinPersistentDB(key, cacheDB.getValueFromCacheDB(key));
                }
            }
        }

    }










    /**
     * This method is used to empty the cache.
     * It takes write back parameter which as two options,
     * WITH_WRITEBACK: this option writes into persistentdb before flushing the cache
     * WITHOUT_WRITEBACK: this option does not write into persistentdb before flushing the cache
     * @see WRITEBACKPARAMETER
     * @param writebackparameter
     */
    synchronized public void flush(WRITEBACKPARAMETER writebackparameter) {
        CacheObject cacheObject;
        if (writebackparameter == WRITEBACKPARAMETER.WITH_WRITE_BACK) {
            List<String> keys = cacheQueue.getCurrentKeys();
            for (String key : keys) {

                cacheObject = cacheQueue.getObject(key);
                if (cacheObject.getDirtyBit()) {

                    persistentDB.putValueinPersistentDB(key, cacheDB.getValueFromCacheDB(key));

                }
            }

        }
        cacheDB.clearCache();
        cacheQueue.clear();
    }







    /**
     * This method is used to close all the threads started by the SimplCache object.
     * This methods also clears the cache and closes it and the Database connections can also be closed if the methods are overwritten in the CacheDB
     * and PersistentDB  interface.It takes write back parameter which as two options,
     * WITH_WRITEBACK: this option writes into persistentdb before flushing the cache
     * WITHOUT_WRITEBACK: this option does not write into persistentdb before flushing the cache
     * @see WRITEBACKPARAMETER
     * @param writebackparameter
     */

    synchronized public void close(WRITEBACKPARAMETER writebackparameter) {
        CacheObject cacheObject;
        if (writebackparameter == WRITEBACKPARAMETER.WITH_WRITE_BACK) {
            List<String> keys = cacheQueue.getCurrentKeys();
            for (String key : keys) {

                cacheObject = cacheQueue.getObject(key);
                if (cacheObject.getDirtyBit()) {

                    persistentDB.putValueinPersistentDB(key, cacheDB.getValueFromCacheDB(key));

                }
            }

        }
        if (evictionType == EVICTION_TYPES.TIME_EVICTION) {
            timeEviction.close();
        }
        if (cacheType == CACHE_TYPES.WRITE_BACK) {
            writeBackPolicy.close();
        }
        cacheDB.clearCache();
        cacheDB.closeDB();
        cacheQueue.clear();
        persistentDB.closeDB();

    }





    /**
     * This method is used to save the state of the keys present in the cache.
     * Only the keys and properties of the cache object are stored.
     * After rebuilding the SimplCache object the actual cache objects are fetched automatically from persistentDB
     * A default encryptor is used to encrypt the data.
     * Note: Encryptor is very simple and does not guarantee any security.
     * @see  #buildFromSaveState(String, CacheDB, PersistentDB) is used to build the state of cache back.
     * @return
     */
    public synchronized String saveState() {
        SimplCacheEncryptor simplCacheEncryptor=null;
        try {
             simplCacheEncryptor= new SimplCacheEncryptorImpl();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return saveState(simplCacheEncryptor);
    }





    /**
     * This method is used to save the state of the keys present in the cache.
     * Only the keys and properties of the cache object are stored.
     * After rebuilding the SimplCache object the actual cache objects are fetched automatically from persistentDB
     * This method accepts an implementation of SimplCacheEncryptor which has to overriden to provide encryption and decryption
     * @see SimplCacheEncryptor
     * Note: Encryptor is very simple and does not guarantee any security.
     * @see  #buildFromSaveState(String, CacheDB, PersistentDB) is used to build the state of cache back.
     * @return
     */
    public synchronized String saveState(SimplCacheEncryptor simplCacheEncryptor) {
        Gson gson = new Gson();
        String message=null;
        JsonObject result = new JsonObject();
        JsonElement simplCache = gson.toJsonTree(this, SimplCache.class);
        result.add("SimplCache", simplCache);
        JsonElement map = gson.toJsonTree(cacheQueue.map());
        result.add("map", map);
        try {
            message=simplCacheEncryptor.encrypt(result.toString());
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return message ;
    }
}
