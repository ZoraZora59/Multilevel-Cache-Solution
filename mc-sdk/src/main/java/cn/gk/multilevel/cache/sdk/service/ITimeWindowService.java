package cn.gk.multilevel.cache.sdk.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.service</h4>
 * <p>时间窗</p>
 *
 * @author zora
 * @since 2020.07.19
 */
public interface ITimeWindowService {

    /**
     * 为当前时间窗的某个缓存增加热度1点
     *
     * @param key 缓存key
     */
    void increaseCacheHot(String key);

    /**
     * 获取当前的时间窗缓存热度数据，放到list里
     *
     * @return 当前的n个时间窗的统计信息
     */
    List<Map<String, AtomicInteger>> getCurrentWindowsMapDataList();
}
