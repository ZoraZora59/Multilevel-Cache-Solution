package cn.gk.multilevel.cache.sdk.service.hot;

import cn.gk.multilevel.cache.sdk.service.ConfigCenter;
import cn.gk.multilevel.cache.sdk.service.IHotKeyManager;
import cn.gk.multilevel.cache.sdk.service.ITimeWindowService;
import cn.gk.multilevel.cache.sdk.util.ThreadPoolUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.service</h4>
 * <p>热数据发现处理器</p>
 *
 * @author zora
 * @since 2020.07.16
 */
@Service
@Slf4j
class HotCacheManager implements IHotKeyManager {
    @Autowired
    private ITimeWindowService timeWindowService;
    @Autowired
    private ConfigCenter configCenter;
    private static final ScheduledThreadPoolExecutor SCHEDULE_HOT_KEY_FINDER_EXECUTOR = ThreadPoolUtils.getScheduledThreadPool(1,"MC-HotKeyFinder");
    private volatile Set<String> hotKeySet = new HashSet<>(1);

    /**
     * 判断该key是否属于当前的热key
     *
     * @param key 键名
     * @return 热key？
     */
    @Override
    public boolean isHotKey(String key) {
        return hotKeySet.contains(key);
    }

    /**
     * 加载类后初始化定时任务
     */
    @PostConstruct
    private void scheduleSetting() {
        SCHEDULE_HOT_KEY_FINDER_EXECUTOR.scheduleWithFixedDelay(() -> {
            try {
                List<Map<String, AtomicInteger>> currentWindowsDataList = timeWindowService.getCurrentWindowsMapDataList();
                Map<String, Integer> counterMap = new HashMap<>(64);
                for (Map<String, AtomicInteger> singleWindowDataMap : currentWindowsDataList) {
                    Set<String> keySet = new HashSet<>(singleWindowDataMap.keySet());
                    for (String key : keySet) {
                        if (counterMap.containsKey(key)) {
                            counterMap.put(key, counterMap.get(key) + singleWindowDataMap.get(key).get());
                        } else {
                            counterMap.put(key, singleWindowDataMap.get(key).get());
                        }
                    }
                }
                hotKeySet = counterMap.keySet().stream().sorted(Comparator.comparingInt(counterMap::get).reversed()).limit(configCenter.getLocalCacheSize()).collect(Collectors.toSet());
                log.info("[Multilevel-Cache]----目前的热Key配置为{}", hotKeySet);
            } catch (Throwable throwable) {
                log.error("[Multilevel-Cache]----生成当前热key失败", throwable);
            }
        }, 3, 3, TimeUnit.SECONDS);
    }

}
