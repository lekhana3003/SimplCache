package com.example.SimplCacheSpringDemo.Repository;

import com.example.SimplCacheSpringDemo.Models.CacheModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
@Repository
public interface CacheRepository extends MongoRepository<CacheModel, String> {
}
