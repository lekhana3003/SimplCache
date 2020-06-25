package com.example.SimplCacheSpringDemo.Configuration;

import com.SimplCache.Models.SimplCache;
import com.example.SimplCacheSpringDemo.InterfacesImpl.CacheDBImpl;
import com.example.SimplCacheSpringDemo.InterfacesImpl.PersistentDBImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
@Configuration
public class SimplCacheBuilderConfiguration {
    @Autowired
    CacheDBImpl cacheDB;
    @Autowired
    PersistentDBImpl persistentDB;

    @Bean
    public SimplCache.SimplCacheBuilder getSimplCacheBuilder()
    {
        return new SimplCache.SimplCacheBuilder(cacheDB,persistentDB);
    }
}
