package com.example.SimplCacheSpringDemo.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
@Document(collection = "PersistentDBModel")
public class PersistentDBModel {
    public PersistentDBModel(Car car, String key) {
        this.car = car;
        this.key = key;
    }

    @Override
    public String toString() {
        return "PersistentDBModel{" +
                "car=" + car +
                ", key='" + key + '\'' +
                '}';
    }

    Car car;
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
