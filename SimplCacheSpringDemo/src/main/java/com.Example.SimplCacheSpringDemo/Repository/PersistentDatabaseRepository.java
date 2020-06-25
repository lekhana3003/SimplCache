package com.example.SimplCacheSpringDemo.Repository;
import com.example.SimplCacheSpringDemo.Models.PersistentDBModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
@Repository
public interface PersistentDatabaseRepository extends MongoRepository<PersistentDBModel, String> {

}
