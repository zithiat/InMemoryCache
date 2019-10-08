public class InMemoryCacheTest {
 
    public static void main(String[] args) throws InterruptedException {
 
        InMemoryCacheTest testCached = new InMemoryCacheTest();
 
        System.out.println("AddRemoveObjects ===");
        testCached.addRemoveObjects();
		
        System.out.println("ExpiredCacheObjects ===");
        testCached.expiredCacheObjects();
        
		System.out.println("CleanupTime ===");
        testCached.cleanupTime();
    }
 
    private void addRemoveObjects() {
        // ttl = 100 seconds
        // custInterval = 200 seconds
        // maxItems = 10
        InMemoryCache<String, String> cache = new InMemoryCache<String, String>(100, 200, 10);
 
        cache.put("1", "abc 1");
        cache.put("2", "abc 2");
		cache.put("3", "abc 3");
		cache.put("4", "abc 4");
		cache.put("5", "abc 5");
		cache.put("6", "abc 6");
		cache.put("7", "abc 7");
		cache.put("8", "abc 8");
		cache.put("9", "abc 9");
		cache.put("10", "abc 10"); 
        System.out.println("Added 10 items, cache.size(): " + cache.size());
		
        cache.remove("10");
        System.out.println("One object removed.. cache.size(): " + cache.size());
 
        cache.put("11", "abc 11");
        cache.put("12", "abc 12");
        System.out.println("Added 2 new item, and maxItems.. cache.size(): " + cache.size()); 
    }
 
    private void expiredCacheObjects() throws InterruptedException {
        // ttl = 1 second
        // custInterval = 1 second
        // maxItems = 10
        InMemoryCache<String, String> cache = new InMemoryCache<String, String>(1, 1, 10);
        cache.put("1", "abc 1");
        cache.put("2", "abc 2");
        System.out.println("wait for 3 seconds, the cached objects will be expired and auto removed.");
		Thread.sleep(3000);
 
        System.out.println("Cache.size(): " + cache.size()); 
    }
 
    private void cleanupTime() throws InterruptedException {
        int size = 100000;
        // ttl = 100 seconds
        // timeInterval = 100 seconds
        // itemNum = 100000
        InMemoryCache<String, String> cache = new InMemoryCache<String, String>(100, 100, 100000);
 
        for (int i = 0; i < size; i++) {
            String value = Integer.toString(i);
            cache.put(value, value);
        } 
        Thread.sleep(200);
 
        long start = System.currentTimeMillis();
        cache.cleanup();
 
        System.out.println("Cleanup for " + size + ": " + (System.currentTimeMillis() - start) + " ms");
    }
}