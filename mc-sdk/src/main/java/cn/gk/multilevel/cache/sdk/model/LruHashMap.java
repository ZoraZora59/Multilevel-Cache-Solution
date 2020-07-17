package cn.gk.multilevel.cache.sdk.model;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.model</h4>
 * <p>LRU淘汰模式的ConcurrentHashMap</p>
 *
 * @author zora
 * @since 2020.07.16
 */
public class LruHashMap<K, V> extends LinkedHashMap<K, V> {
    private int maxSize = -1;

    public LruHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public LruHashMap(int initialCapacity, float loadFactor, boolean accessOrder,int maxSize) {
        super(initialCapacity, loadFactor, accessOrder);
        this.maxSize=maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (maxSize > 0) {
            return size() > maxSize;
        } else {
            return false;
        }
    }
}
