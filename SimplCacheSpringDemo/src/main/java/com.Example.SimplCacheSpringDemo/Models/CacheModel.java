package com.example.SimplCacheSpringDemo.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
@Document(collection = "CacheModel")
public class CacheModel {
    public CacheModel(Car car, String key) {
        this.car = car;
        this.key = key;
    }

    Car car;

    @Override
    public String toString() {
        return "CacheModel{" +
                "car=" + car +
                ", key='" + key + '\'' +
                '}';
    }

    @Id
    String key;

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
