package cn.gk.multilevel.cache.sdk.service.counter;

import cn.gk.multilevel.cache.sdk.service.ICounterService;
import cn.gk.multilevel.cache.sdk.service.ITimeWindowService;
import cn.gk.multilevel.cache.sdk.util.ThreadPoolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.service</h4>
 * <p>计数器处理</p>
 *
 * @author zora
 * @since 2020.07.15
 */
@Service
class CounterServiceImpl implements ICounterService {
    private static final ThreadPoolExecutor COUNTER_EXECUTOR = ThreadPoolUtils.getThreadPool(2, 4, 60, 256, "MC-Counter-%d");

    @Autowired
    private ITimeWindowService timeWindowService;

    /**
     * 为当前时间窗的某个缓存增加热度1点
     *
     * @param key 缓存key
     */
    @Override
    public void asyncIncreaseCacheHot(String key) {
        COUNTER_EXECUTOR.execute(() -> {
            timeWindowService.increaseCacheHot(key);
        });
    }
}
