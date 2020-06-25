package com.example.SimplCacheSpringDemo.Configuration;

import com.SimplCache.Models.SimplCache;
import com.example.SimplCacheSpringDemo.Models.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
@Configuration
public class SimplCacheConfiguration {
    @Autowired
    SimplCache.SimplCacheBuilder<Car> simplCacheBuilder;

    @Bean
    public SimplCache getSimplCache()
    {
        return simplCacheBuilder
                .setCacheType(SimplCache.CACHE_TYPES.WRITE_THROUGH)
                .setEvictionPolicy(SimplCache.EVICTION_TYPES.LRU_EVICTION)
                .build();
    }
}
