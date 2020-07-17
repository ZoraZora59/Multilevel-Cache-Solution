package cn.gk.multilevel.cache.sdk.service;

import cn.gk.multilevel.cache.sdk.api.McTemplate;
import com.alibaba.fastjson.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.service</h4>
 * <p>多级缓存暴露api服务</p>
 *
 * @author zora
 * @since 2020.07.17
 */
@Service
public class McTemplateService implements McTemplate {
    @Autowired
    private CacheService cacheService;

    /**
     * 尝试获取缓存中的对象，可能会拿到null
     *
     * @param key   缓存的key
     * @param clazz 反序列化到的类
     * @return 对象或null
     * @throws JSONException json序列化失败
     */
    @Override
    public <V> V tryGetValue(@NonNull String key, Class<V> clazz) throws JSONException {
        return cacheService.tryGetValue(key, clazz);
    }

    /**
     * 尝试获取缓存中的列表对象，可能会拿到null
     *
     * @param key   缓存的key
     * @param clazz 反序列化到的类
     * @return 对象列表或null
     * @throws JSONException json序列化失败
     */
    @Override
    public <V> List<V> tryGetValueArrays(@NonNull String key, Class<V> clazz) throws JSONException {
        return cacheService.tryGetValueArrays(key, clazz);
    }

    /**
     * 写入缓存
     *
     * @param key   缓存key
     * @param value 缓存对象
     */
    @Override
    public <T> void putObjectIntoCache(@NonNull String key, T value) {
        cacheService.putObjectIntoCache(key, value);
    }

    /**
     * 写入缓存
     *
     * @param key   缓存key
     * @param value 缓存对象
     * @param ttl   过期时间，单位秒
     */
    @Override
    public <T> void putObjectIntoCache(@NonNull String key, T value, long ttl) {
        cacheService.putObjectIntoCache(key, value, ttl);
    }

    /**
     * 清理本地缓存空间
     */
    @Override
    public void cleanUpRamCache() {
        cacheService.cleanUpRamCache();
    }

    /**
     * 清理本地缓存空间
     */
    @Override
    public void cleanCacheByKey(@NonNull String key) {
        cacheService.cleanCacheByKey(key);
    }
}
