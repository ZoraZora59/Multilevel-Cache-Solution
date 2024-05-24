package cn.gk.multilevel.cache.sdk.service;

import com.alibaba.fastjson.JSONException;

import java.util.List;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.service.local</h4>
 * <p>本地缓存接口</p>
 *
 * @author zora
 * @since 2020.07.19
 */
public interface ICacheService {

    /**
     * 尝试获取缓存中的对象，可能会拿到null
     *
     * @param key   缓存的key
     * @param clazz 反序列化到的类
     * @return 对象或null
     * @throws JSONException json序列化失败
     */
    <V> V tryGetValue(String key, Class<V> clazz) throws JSONException;

    /**
     * 尝试获取缓存中的列表对象，可能会拿到null
     *
     * @param key   缓存的key
     * @param clazz 反序列化到的类
     * @return 对象列表或null
     * @throws JSONException json序列化失败
     */
    <V> List<V> tryGetValueArrays(String key, Class<V> clazz) throws JSONException;

    /**
     * 写入缓存
     *
     * @param key   缓存key
     * @param value 缓存对象
     */
    <T> void putObjectIntoCache(String key, T value);

    /**
     * 写入缓存
     *
     * @param key   缓存key
     * @param value 缓存对象
     * @param ttl   过期时间，单位秒
     */
    <T> void putObjectIntoCache(String key, T value, long ttl);

    /**
     * 清理本地缓存空间
     */
    void cleanUpRamCache();

    /**
     * 清理指定的key
     *
     * @param key 缓存key
     */
    void cleanCacheByKey(String key);
}
