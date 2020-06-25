package com.example.SimplCacheSpringDemo.InterfacesImpl;

import com.example.SimplCacheSpringDemo.Models.CacheModel;
import com.example.SimplCacheSpringDemo.Models.Car;
import com.example.SimplCacheSpringDemo.Repository.CacheRepository;
import com.SimplCache.DatabaseInterfaces.CacheDB;
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
public class CacheDBImpl implements CacheDB<Car> {

@Autowired
    CacheRepository cacheRepository;

    public CacheDBImpl(CacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }


    @Override
    public void putValueInCacheDB(String key, Car car) {

        //System.out.println(key+":"+car);
        cacheRepository.save(new CacheModel(car,key));
    }

    @Override
    public void removeValueFromCacheDB(String key) {

        Optional<CacheModel> cacheModel=cacheRepository.findById(key);
        cacheRepository.delete(cacheModel.get());

    }

    @Override
    public void clearCache() {

        cacheRepository.deleteAll();
    }

    @Override
    public Car getValueFromCacheDB(String key) {

        Optional<CacheModel> cacheModel=cacheRepository.findById(key);
        return cacheModel.get().getCar();
    }

    @Override
    public void closeDB() {
        //No implementation
    }
}
