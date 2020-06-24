#SimplCache - An Easy Java Cache Library
SimplCache library can be used to implement cache instantly.<br/>
The library takes implementations of two databases,
* Cache Database
* Persistent Database<br>

After which it provides all the features of cache seamlessly.

## 1. Features

The features provided by the library are,
* Easy Implementation<br>
To implement the cache the user has to implement only two interfaces (i.e [CacheDB]() and [PersistentDB]()).
These databases can be of any type.(Relational or Non-Relational database) and rest is taken care by the SimplCache.
* Least Recently Used (LRU) Eviction<br>
Least recently used cache object is evicted when cache reaches maximum limit.
* Timed Eviction<br>
Objects are removed from the cache after given interval time.
* Write Through<br>
The data is simultaneously updated to cache and persistent database.
* Write Back<br>
The data is updated only in the cache and updated into the persistent database in later time.
* Automated Write Back<br>
The data in the cache is regularly updated in the persistent database at the interval given by the user.
* Cache Size<br>
The cache size can be given either as count of the objects to be stored in cache or the actual memory occupied by the objects in the cache (i.e Kilobytes).
* Save State<br>
Save the state of the cache at any point in time by calling a simple method and rebuild the same cache at any point by calling simple method.

## 2. Setup
Maven:
```
repositories {
    mavenCentral()
   //maven { url "https://oss.sonatype.org/content/repositories/snapshots" } //for snashot builds

}
```
Gradle:
```
dependencies {
    compile ''
}
```                                                       
<table>
<tbody>
<tr>
<td><b>IMPORTANT</b></td>
<td>To set the cache size in terms of object memory VM options have to be enabled while executing the main class of your project.<br>
<ul>
<li>Download the  <a href="">JAR</a><br></li>
<li>Execute java function by enabling VM options as: java -javaagent:"path to downloaded jar"<br>
Ex:java -javaagent:"../SimplCache.jar"</li>
</ul>
For Reference:<br>
<a href="https://stackoverflow.com/questions/10639322/how-can-i-specify-the-default-jvm-arguments-for-programs-i-run-from-eclipse">VM Options in Eclipse</a><br>
<a href="https://stackoverflow.com/questions/45115208/how-to-set-javaagent-in-intellij-idea-vm-options">VM Options in Intellij</a><br>
<a href="https://web.archive.org/web/20141014195801/http://dhruba.name/2010/02/07/creation-dynamic-loading-and-instrumentation-with-javaagents/">Dynamically Enabling VM options</a>
 </td>
</tr>
</tbody>
</table>

## 3. Usage

### 3.1 Getting Started
To implement cache using the library, it requires some methods to be implemented which are present in the interfaces
[CacheDB]() and [PersistentDB](). The SimplCache object is built using the SimplCache Builder.
The Constructor of the builder takes two objects of CacheDB and PersistentDB.The model of the object that has to be stored in the cache has to be provided while implementing the interfaces.<br>
Example:<br>
The object to be stored in the cache is Car Model.<br>
For CacheDB:
```java
public class CacheDBImpl implements CacheDB<Car> {
...
}
```
For PersistentDB:
```java
public class PersistentDBImpl implements PersistentDB<Car> {
...
}
```

To build the SimplCache Object:

```java
CacheDBImpl cacheDB=new CacheDBImpl();
PersistentDBImpl persistentDB= new PersistentDBImpl();
SimplCache<Car> simplCache=new SimplCache.SimplCacheBuilder<Car>(cacheDB,persistentDB).build()
```
---
**NOTE:**

By default, the eviction policy is set to LRU and cache type is set as Write through.

---
### 3.2 Setting Cache Properties
#### 3.2.1 Eviction Policy
<b>setEvictionPolicy</b> is used to set the eviction which accepts an enum of <b>EVICTION_TYPES</B>.
There are two types of eviction policies provided currently,
* LRU Eviction<br>
Whenever a new object is put into the cache the least recently used object is evicted from cache after writing the object into cache.
    ```java
    SimplCache<Car> simplCache=new SimplCache.SimplCacheBuilder<Car>(cacheDB,persistentDB)
            .setEvictionPolicy(SimplCache.EVICTION_TYPES.LRU_EVICTION)
            .build();
    ```
* Timed Eviction<br>
Time eviction is used when the cache objects have to remain in cache only for certain amount of time.
By setting this property, a new single thread runs parallel to the main thread which is responsible for removing the object from the cache after the time out.
Before evicting the object from the cache the current data is automatically written into the persistent database. <br><bR>
<b>setTimeEvictionInterval()</b> method is used to set the interval after which the object in cache expires.It accepts time integer and the unit of time as TimeUnit Object.If time eviction interval is not set default value of 10 minutes is set. <br>
    ```java
    SimplCache<Car> simplCache=new SimplCache.SimplCacheBuilder<Car>(cacheDB,persistentDB)
            .setEvictionPolicy(SimplCache.EVICTION_TYPES.TIME_EVICTION)
            .setTimeEvictionInterval(30,TimeUnit.SECONDS)
            .build();
    ```
---
**NOTE:**

The implemeneted  time eviction follows a combination of LRU and Time eviction i.e. if the cache is full at any point and no object in cache has timed out then the least recently object is removed from the cache to add the new object. <bR><br>

---
#### 3.2.2 Cache Type properties
<b>setCacheType()</b> method is used to set the cache type of the cache. It accepts an enum <b>CACHE_TYPES</b>.There are two types of cache available currently,
* Write-Through<br>
The object is immediately written into persistent database after written in cache data base.
    ```java
    SimplCache<Car> simplCache=new SimplCache.SimplCacheBuilder<Car>(cacheDB,persistentDB)
            .setCacheType(SimplCache.CACHE_TYPES.WRITE_THROUGH)
            .build();
    ```
* Write-Back<br>
The object in cache is not immediately updated in cache but later in time.
There are two types of write-back options available,
    * NO_AUTO<bR>
    The write back into the persistent database from cache database happens only when write_back() method is called or when the object is evicted from cache. The write back does not happen automatically.
        ```java
          SimplCache<Car> simplCache=new SimplCache.SimplCacheBuilder<Car>(cacheDB,persistentDB)
                            .setCacheType(SimplCache.CACHE_TYPES.WRITE_BACK)
                            .build();
         ```
  * AUTO<br>
  In this variation write back occurs at regular interval which is given by the user. A seperate new single thread is created and this thread is responsible for writing back only object which has been modified.
  <b>setWriteBackInterval()</b> takes two parameters time integer and the unit of time as TimeUnit Object. 
      ```java
    SimplCache<Car> simplCache=new SimplCache.SimplCacheBuilder<Car>(cacheDB,persistentDB)
                        .setCacheType(SimplCache.CACHE_TYPES.WRITE_BACK)
                        .setWriteBackInterval(30,TimeUnit.SECONDS)
                        .build();
      ```
#### 3.2.3 Cache Memory Types
The library provides two types of cache size options, i.e. depending on Objects size and Objects count.<bR>
To set the memory properties, <b>setCacheMemoryProperties()</b> is used. It takes two parameters,
* MEMORY_TYPES enum which has OBJECTS_SIZE and OBJECTS_COUNT
* Maximum size of the cache, the size of cache is in KiloBytes(KB) if memory type is given as OBJECTS_SIZE and count if the OBJECTS_COUNT is given as memory type.<br>

The default value of MEMORY_TYPES is OBJECT_COUNT and the default size is 50.
```java
SimplCache<Car> simplCache=new SimplCache.SimplCacheBuilder<Car>(cacheDB,persistentDB)
        .setCacheMemoryProperties(SimplCache.MEMORY_TYPES.OBJECTS_COUNT,10)
        .build();
```

### 3.3 Put Method

This is an important method which is used to put object inside the cache. If the the cache is full, this method is responsible for performing the eviction according to eviction policy selected by the user.
This method takes two parameters Key(String) and the Object.
```java
Car car=new Car("2","Car Model","2020");
simplCache.put("2",car);
```
Another variation of this method,
If the user requires the object only to be added into cache but not into persistent database i.e if the user wants the put method not to perform write-through or write-back
the user can use this variation.
```java
Car car=new Car("key","Car Model","2020");
simplCache.put("key",car, SimplCache.POLICY_CONTROL.WITHOUT_POLICY);
```
### 3.4 Get Method
This method gets the object from the cache. If the object is not present in the cache, this method fetches the object from persistent database and also adds this object into the cache.
This method requires the key of the object.
```java
Car car = simplCache.get("key");
```
### 3.5 Write-Back Method
This method is responsible for writing the cache objects into persistent database. This method writes only the object which have been modified.
This can be used whenver user wants to manually write the modified objects to the persistent database.

```java
simplCache.writeBack();
```
### 3.6 Flush Method
This method is used to clear all the objects in cache. It accepts a single parameter of <b>WRITEBACKPARAMETER</b> enum.
This parameter given by the user decides if the modified objects should be written into the persistent database before flushing.
* WITH_WRITE_BACK
    ```java
    simplCache.flush(SimplCache.WRITEBACKPARAMETER.WITH_WRITE_BACK);
    ```
* WITHOUT_WRITE_BACK
    ```java
    simplCache.flush(SimplCache.WRITEBACKPARAMETER.WITHOUT_WRITE_BACK);
    ```
### 3.7 Close Method
This method is used to close all the resources that have been opened by the cache object.
The database connections of CacheDB and Persistent can be closed using this function. The user has option to override if he chooses to.
<br> The close method takes a single parameter.
This parameter given by the user decides if the modified objects should be written into the persistent database before flushing.
* WITH_WRITE_BACK
    ```java
    simplCache.close(SimplCache.WRITEBACKPARAMETER.WITH_WRITE_BACK);
    ```
* WITHOUT_WRITE_BACK
    ```java
    simplCache.close(SimplCache.WRITEBACKPARAMETER.WITHOUT_WRITE_BACK);
    ```
### 3.8 Save State

##### Saving:
This method is used to save the state of cache at any given time. This method returns a string which is encrypted with default encryption algorithm.
If the user wants to implement any other encryption mechanism, the user is required to pass an object of class which has implemented [SimplCacheEncryptor](). The cache objects are written back into persistent database before saving the state,
This method returns only the keys of cache object and also stores the properties which are set for the cache in the SimplCache object. It does not store the entire object which is stored in the cache. 
The two variations as follows,<br>
* With default encryptor,
    ```java
    String state = simplCache.saveState();
    ```
* With implemenation of SimplCacheEncryptor<br>
    Implemenation class
    ```java
    public class SimplCacheEncryptorImpl implements SimplCacheEncryptor {
        ...
    }
    ```
    ```java
    SimplCacheEncryptorImpl simplCacheEncryptorImpl=new SimplCacheEncryptorImpl();
    String state = simplCache.saveState(simplCacheEncryptorImpl);
    ```
##### Building from save state:
 This method is used to build back the cache from the string which is returned by the <b>saveState()</b> method.
 This method restores cache objects from persistent database.
 This method takes cacheDB and persistenseDB implemenation objects again long with optional [SimplCacheEncryptor]() object.  
* With default encryptor,
    ```java
    SimplCache<Car> simplCache1=SimplCache.buildFromSaveState(state,cacheDB,persistentDB);
    ```
* With implemenation of SimplCacheEncryptor<br>
    ```java
    SimplCache<Car> simplCache1=SimplCache.buildFromSaveState(state,cacheDB,persistentDB,simplCacheEncryptorImpl);
    ```
---
**NOTE:**

The default encryptor does not guarantee any security.

---

## Example
[Spring Boot implemenation of SimplCache Library]()

## License
[Apache 2.0 License for SimplCache]()
```
   Copyright 2020 Lekhana Ganji

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

## Author
* Author: Lekhana Ganji
* Email: <a href="mailto:lekhanag.3003@gmail.com">lekhanag.3003@gmail.com </a>