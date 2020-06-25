package com.example.SimplCacheSpringDemo.InterfacesImpl;

import com.example.SimplCacheSpringDemo.Models.CacheModel;
import com.example.SimplCacheSpringDemo.Models.Car;
import com.example.SimplCacheSpringDemo.Models.PersistentDBModel;
import com.example.SimplCacheSpringDemo.Repository.PersistentDatabaseRepository;
import com.SimplCache.DatabaseInterfaces.PersistentDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
@Component
public class PersistentDBImpl implements PersistentDB<Car> {
   @Autowired
    PersistentDatabaseRepository persistentDatabaseRepository;

    public PersistentDBImpl(PersistentDatabaseRepository persistentDatabaseRepository) {
        this.persistentDatabaseRepository = persistentDatabaseRepository;
    }

    @Override
    public void putValueinPersistentDB(String key, Car car) {


        persistentDatabaseRepository.save(new PersistentDBModel(car,key));
    }

    @Override
    public Car getValueFromPersistentDB(String key) {


        Optional<PersistentDBModel> persistentDBModel=persistentDatabaseRepository.findById(key);
        return persistentDBModel.get().getCar();
    }

    @Override
    public void closeDB() {

    }
}
