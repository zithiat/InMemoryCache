import java.util.ArrayList;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.LRUMap;
 
/**
 * @author quandoanh
 * This is a simple InMemory cache library.
 * - Items expires based on a time to live period.
 * - Keep most recently used items
 * - If add more items. itemNumber is needed.
 * - Expiration  uses timestamp in other thread, which reduces memore pressure in accessing the cached objects.
 */
 
public class InMemoryCache<K, T> {
 
    private long ttl; // Time to live
	// Considering to use ConcurrentHashMap
	// which will not need synchronized call, performance will be better
	// but we need to manage the auto-cleanup for the least accessed items in the map.
    private LRUMap cacheMap; // Cached map from Apache LRUMap, which removes the least used items
 
    protected class CacheObject {
        public long lastTime = System.currentTimeMillis();
        public T value;
 
        protected CacheObject(T value) {
            this.value = value;
        }
    }

	/**
	 * ttl: input time to live
	 * timeInterval: input time interval to check
	 * itemNum: maximum items can be stored in the map
	 */
 
    public InMemoryCache(long ttl, final long timeInterval, int itemNum) {
        this.ttl = ttl * 1000;
 
        cachedMap = new LRUMap(itemNum);
 
        if (this.ttl > 0 && timeInterval > 0) {
             Thread t = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(timeInterval * 1000);
                        } catch (InterruptedException ex) {
                        }
                        cleanup();
                    }
                }
            });
 
            t.setDaemon(true);
            t.start();
        }
    }
 
    public void put(K key, T value) {
        synchronized (cachedMap) {
            cachedMap.put(key, new CachedObject(value));
        }
    }
 
    @SuppressWarnings("unchecked")
    public T get(K key) {
        synchronized (cachedMap) {
            CachedObject c = (CachedObject) cachedMap.get(key);
 
            if (c == null)
                return null;
            else {
                c.lastAccessed = System.currentTimeMillis();
                return c.value;
            }
        }
    }
 
    public void remove(K key) {
        synchronized (cachedMap) {
            cachedMap.remove(key);
        }
    }
 
    public int size() {
        synchronized (cachedMap) {
            return cachedMap.size();
        }
    }
 
    @SuppressWarnings("unchecked")
    public void cleanup() {
 
        long now = System.currentTimeMillis();
        ArrayList<K> deleteKey = null;
 
        synchronized (cachedMap) {
            MapIterator itr = cachedMap.mapIterator();
 
            deleteKey = new ArrayList<K>((cachedMap.size() / 2) + 1);
            K key = null;
            CachedObject c = null;
 
            while (itr.hasNext()) {
                key = (K) itr.next();
                c = (CachedObject) itr.getValue();
 
                if (c != null && (now > (ttl + c.lastAccessed))) {
                    deleteKey.add(key);
                }
            }
        }
 
        for (K key : deleteKey) {
            synchronized (cachedMap) {
                cachedMap.remove(key);
            }
 
            Thread.yield();
        }
    }
}