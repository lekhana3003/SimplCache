package com.example.SimplCacheSpringDemo.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
public class Car {
    @Id
    String id;
    String year;
    String modelName;

    @Override
    public String toString() {
        return "Car{" +
                "id='" + id + '\'' +
                ", year='" + year + '\'' +
                ", modelName='" + modelName + '\'' +
                '}';
    }

    public Car(String id, String year, String modelName) {
        this.id = id;
        this.year = year;
        this.modelName = modelName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }



}
