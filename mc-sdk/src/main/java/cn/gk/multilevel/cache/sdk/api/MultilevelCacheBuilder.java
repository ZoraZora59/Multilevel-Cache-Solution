package cn.gk.multilevel.cache.sdk.api;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Weigher;

import java.util.Objects;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.api</h4>
 * <p>多级缓存-缓存生成器</p>
 *
 * @author zora
 * @since 2020.07.15
 */
public class MultilevelCacheBuilder {
    private static final long DEFAULT_MAXIMUM_MEMORY_USAGE_SIZE = 128 * 1024 * 1024;
    private static final long DEFAULT_MAXIMUM_CACHE_SIZE = 256;
    private long memoryUsageSize = 0L;
    private long cacheCountSize = 0L;
    private static final Weigher<String, String> DEFAULT_WEIGHER = (key, value) -> key.length() + value.length();
    private Weigher<String, String> weigher;

    public Cache<String, String> buildByWeightStrategy() {
        return Caffeine.newBuilder()
                .maximumWeight(memoryUsageSize == 0 ? DEFAULT_MAXIMUM_MEMORY_USAGE_SIZE : memoryUsageSize)
                .weigher(Objects.isNull(weigher) ? DEFAULT_WEIGHER : weigher)
                .softValues()
                .build();
    }

    public Cache<String, String> buildByCacheSizeStrategy() {
        return Caffeine.newBuilder()
                .maximumSize(cacheCountSize == 0 ? DEFAULT_MAXIMUM_CACHE_SIZE : cacheCountSize)
                .softValues()
                .build();
    }

    public MultilevelCacheBuilder withCacheCount(long size) {
        cacheCountSize = size > 0 ? size : DEFAULT_MAXIMUM_MEMORY_USAGE_SIZE;
        return this;
    }
    public MultilevelCacheBuilder withMemoryUsage(long size) {
        memoryUsageSize = size > 0 ? size : DEFAULT_MAXIMUM_MEMORY_USAGE_SIZE;
        return this;
    }

    public MultilevelCacheBuilder withMaximumWeight(long size) {
        memoryUsageSize = size > 0 ? size : DEFAULT_MAXIMUM_MEMORY_USAGE_SIZE;
        return this;
    }

    public MultilevelCacheBuilder withWeigher(Weigher<String, String> userDesignedWeigher) {
        weigher = Objects.isNull(weigher) ? DEFAULT_WEIGHER : userDesignedWeigher;
        return this;
    }
}
