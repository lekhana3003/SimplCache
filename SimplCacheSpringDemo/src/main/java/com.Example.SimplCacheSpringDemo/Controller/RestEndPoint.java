package com.example.SimplCacheSpringDemo.Controller;

import com.SimplCache.Models.SimplCache;
import com.example.SimplCacheSpringDemo.InterfacesImpl.CacheDBImpl;
import com.example.SimplCacheSpringDemo.InterfacesImpl.PersistentDBImpl;
import com.example.SimplCacheSpringDemo.Models.Car;

import com.example.SimplCacheSpringDemo.Repository.PersistentDatabaseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
@Component
@RestController
public class RestEndPoint {


   @Autowired
    PersistentDatabaseRepository persistentDatabaseRepository;

   @Autowired
    SimplCache<Car> simplCache;

    @GetMapping("/put/{key}/{modelName}/{year}")
    public String put(@PathVariable String key,@PathVariable String modelName,@PathVariable String year)
   {
            simplCache.put(key,new Car(key,modelName,year));
            return "Success";
    }

    @GetMapping("/get/{key}")
    public String get(@PathVariable String key)
    {
        Car car;
        try {
             car= simplCache.get(key);
        }
       catch (Exception e)
       {
           return "Object not found";
       }
        return car.toString();
    }

}
