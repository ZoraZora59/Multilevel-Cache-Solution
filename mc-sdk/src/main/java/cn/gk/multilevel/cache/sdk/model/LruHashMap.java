package cn.gk.multilevel.cache.sdk.model;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.model</h4>
 * <p></p>
 *
 * @author zora
 * @since 2020.07.17
 */

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <h4>multilevel-cache-solution</h4>
 * <h5>cn.gk.multilevel.cache.sdk.model</h5>
 * <p>添加了LRU淘汰模式的LinkedHashMap</p>
 *
 * @author zora
 * @since 2020.07.16
 */
public class LruHashMap<K, V> extends LinkedHashMap<K, V> {
    private int maxSize = -1;

    public LruHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public LruHashMap(int initialCapacity, float loadFactor, boolean accessOrder, int maxSize) {
        super(initialCapacity, loadFactor, accessOrder);
        this.maxSize = maxSize;
    }

    @Override
    public V put(K key, V value) {
        V v;
        if (super.containsKey(key)) {
            synchronized (super.get(key)) {
                v = super.put(key, value);
            }
        } else {
            v = super.put(key, value);
        }
        return v;
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