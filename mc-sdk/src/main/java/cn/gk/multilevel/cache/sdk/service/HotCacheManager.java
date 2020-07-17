package cn.gk.multilevel.cache.sdk.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
class HotCacheManager {
    @Autowired
    private TimeWindowService timeWindowService;
    @Autowired
    private ConfigCenter configCenter;
    private static final ScheduledThreadPoolExecutor scheduleHotKeyFinderExecutor = new ScheduledThreadPoolExecutor(1,
            new ThreadFactoryBuilder().setNameFormat("MC-HotKeyFinder").build());
    private volatile Set<String> hotKeySet = new HashSet<>(1);

    public boolean isHotKey(String key) {
        return hotKeySet.contains(key);
    }

    @PostConstruct
    private void scheduleSetting() {
        scheduleHotKeyFinderExecutor.scheduleWithFixedDelay(() -> {
            List<Map<String, AtomicInteger>> currentWindowsDataList = timeWindowService.getCurrentWindowsMapDataList();
            Map<String, Integer> counterMap = new HashMap<>(64);
            for (Map<String, AtomicInteger> singleWindowDataMap : currentWindowsDataList) {
                for (String key : singleWindowDataMap.keySet()) {
                    if (counterMap.containsKey(key)) {
                        counterMap.put(key, counterMap.get(key) + singleWindowDataMap.get(key).get());
                    } else {
                        counterMap.put(key, singleWindowDataMap.get(key).get());
                    }
                }
            }
            hotKeySet = counterMap.keySet().stream().sorted(Comparator.comparingInt(counterMap::get).reversed()).limit(configCenter.getLocalCacheSize()).collect(Collectors.toSet());
        }, 30, 15, TimeUnit.SECONDS);
    }
}
