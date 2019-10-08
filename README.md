# InMemoryCache
A simple In-Memory cache

The idea is to apply the LRU Map to simulate in-memory cache, instead of using other existing libraries. Simple and easy to use in any application.

Notes:
- Still using Apache LRU Map, so we need to synchronize for multi-threading. If change to ConcurrentHashMap, it would be faster.
