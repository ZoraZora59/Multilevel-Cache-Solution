package cn.gk.multilevel.cache.sdk.service;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.service</h4>
 * <p>异步计数器</p>
 *
 * @author zora
 * @since 2020.07.21
 */
public interface ICounterService {

    /**
     * 为当前时间窗的某个缓存增加热度1点
     *
     * @param key 缓存key
     */
    void asyncIncreaseCacheHot(String key);
}
