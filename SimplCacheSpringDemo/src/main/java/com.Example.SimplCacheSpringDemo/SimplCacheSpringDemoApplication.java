package com.example.SimplCacheSpringDemo;

import com.example.SimplCacheSpringDemo.InterfacesImpl.CacheDBImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimplCacheSpringDemoApplication {
	@Autowired
	CacheDBImpl cacheDB;

	public static void main(String[] args) {
		SpringApplication.run(SimplCacheSpringDemoApplication.class, args);


//
	}

}
