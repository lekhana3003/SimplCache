package com.example.SimplCacheSpringDemo.Configuration;

import com.example.SimplCacheSpringDemo.Models.CacheModel;
import com.example.SimplCacheSpringDemo.Models.Car;
import com.example.SimplCacheSpringDemo.Models.PersistentDBModel;
import com.example.SimplCacheSpringDemo.Repository.PersistentDatabaseRepository;
import com.example.SimplCacheSpringDemo.Repository.CacheRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
@EnableMongoRepositories(basePackageClasses = {PersistentDatabaseRepository.class, CacheRepository.class})
@Configuration
public class MongoDBConfiguration {



    @Bean
    CommandLineRunner commandLineRunner(PersistentDatabaseRepository persistentDatabaseRepository,CacheRepository cacheRepository) {
        return strings ->{
            persistentDatabaseRepository.save(new PersistentDBModel(new Car("1","2009","Maruthi"),"1"));
            persistentDatabaseRepository.save(new PersistentDBModel(new Car("2","2013","Honda"),"2"));


        };
    }

}
