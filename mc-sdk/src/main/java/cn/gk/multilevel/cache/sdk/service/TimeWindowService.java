package cn.gk.multilevel.cache.sdk.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.service</h4>
 * <p>时间滑窗处理</p>
 *
 * @author zora
 * @since 2020.07.15
 */
@Slf4j
@Service
class TimeWindowService {
    @Value("${multilevelCache.singleTimeWindow.keyCount}")
    private String userKeyCountValue;
    private int keyCount = 0;
    public static final int DEFAULT_KEY_COUNT_IN_SINGLE_WINDOW = 128;
    public static final int TIME_WINDOW_COUNT = 4;
    private final ReentrantLock lock = new ReentrantLock();
    /**
     * 时间窗表： [时间字符串：(缓存key：热度)]
     */
    private final static Map<Integer, Map<String, AtomicInteger>> timeWindowsMap = new LruHashMap<>(TIME_WINDOW_COUNT, 1F, false, TIME_WINDOW_COUNT);
    private final static ScheduledThreadPoolExecutor scheduledSlideExecutor = new ScheduledThreadPoolExecutor(1,
            new ThreadFactoryBuilder().setNameFormat("MC-TimeWindow").build());

    @PostConstruct
    private void initTimeWindowMapAndQueue() {
        LocalDateTime currentTime = LocalDateTime.now();
        for (int i = TIME_WINDOW_COUNT - 1; i >= 0; i--) {
            int currentWindow = currentTime.plusMinutes(-i).getMinute();
            putNewSingleTimeWindowMap(currentWindow);
        }
        log.info("[Multilevel-Cache]----时间窗共计{}个已初始化完毕", timeWindowsMap.size());
        scheduledSlideExecutor.scheduleWithFixedDelay(() -> {
            try {
                LocalDateTime currentTimeFlag = LocalDateTime.now();
                for (int i = 0; i <= 1; i++) {
                    int scheduleCurrentTime = currentTimeFlag.plusMinutes(i).getMinute();
                    if (!timeWindowsMap.containsKey(scheduleCurrentTime)) {
                        putNewSingleTimeWindowMap(scheduleCurrentTime);
                    }
                }
            } finally {
                log.info("[Multilevel-Cache]----目前时间窗状态【时间窗个数={}, 时间窗分别为[{}]】", timeWindowsMap.size(), timeWindowsMap.keySet());
            }
        }, 0, 25, TimeUnit.SECONDS);
    }

    @PostConstruct
    private void initKeyCount() {
        try {
            keyCount = Integer.parseInt(userKeyCountValue);
            keyCount = keyCount <= 0 ? DEFAULT_KEY_COUNT_IN_SINGLE_WINDOW : keyCount;
        } catch (NumberFormatException numberFormatException) {
            keyCount = DEFAULT_KEY_COUNT_IN_SINGLE_WINDOW;
        }
        log.info("[Multilevel-Cache]----配置的单窗口最大统计key数为{}个.", keyCount);
    }

    private void putNewSingleTimeWindowMap(int minuteFlag) {
        if (!timeWindowsMap.containsKey(minuteFlag)) {
            lock.lock();
            try {
                if (!timeWindowsMap.containsKey(minuteFlag)) {
                    timeWindowsMap.put(minuteFlag, new LruHashMap<>(keyCount, 1F, true, keyCount));
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private void tryIncreaseCount(String key, int currentWindow) {
        Map<String, AtomicInteger> currentTimeWindowMap = timeWindowsMap.get(currentWindow);
        if (currentTimeWindowMap.containsKey(key)) {
            currentTimeWindowMap.get(key).incrementAndGet();
        } else {
            currentTimeWindowMap.put(key, new AtomicInteger(1));
        }
    }

    public void increaseCacheHot(String key) {
        int currentWindow = LocalDateTime.now(ZoneId.systemDefault()).getMinute();
        if (!timeWindowsMap.containsKey(currentWindow)) {
            putNewSingleTimeWindowMap(currentWindow);
        }
        tryIncreaseCount(key, currentWindow);
    }

    public List<Map<String,AtomicInteger>> getCurrentWindowsMapDataList(){
        return new ArrayList<>(timeWindowsMap.values());
    }

    /**
     * <h4>multilevel-cache-solution</h4>
     * <h5>cn.gk.multilevel.cache.sdk.model</h5>
     * <p>添加了LRU淘汰模式的LinkedHashMap</p>
     *
     * @author zora
     * @since 2020.07.16
     */
    private static class LruHashMap<K, V> extends LinkedHashMap<K, V> {
        private int maxSize = -1;

        public LruHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {
            super(initialCapacity, loadFactor, accessOrder);
        }

        public LruHashMap(int initialCapacity, float loadFactor, boolean accessOrder, int maxSize) {
            super(initialCapacity, loadFactor, accessOrder);
            this.maxSize = maxSize;
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
}
