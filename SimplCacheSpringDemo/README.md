# SpringBoot Example of SimplCache
In this example, MongoDB is used as both persistent and cache database.<br><br>
A [CacheModel]() and [PersistentDB model]() have been created to store [Car]() model seperately in cache and persistentdb.
The database interface implementation is as follows,
* [PersistentDBImpl]()
* [CacheDBImpl]()

Configuration of SimplCache is done through,<br>
* [SimplCacheBuilderConfiguration]()
* [SimplCacheConfiguration]()


