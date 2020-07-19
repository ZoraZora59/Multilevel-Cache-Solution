package cn.gk.multilevel.cache.sdk.service;

import cn.gk.multilevel.cache.sdk.constants.TimeWindowConstants;
import cn.gk.multilevel.cache.sdk.model.LruHashMap;
import cn.gk.multilevel.cache.sdk.util.ThreadPoolUtils;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
    /**
     * 尝试获取的用户配置
     * 单个时间窗内统计的最大缓存数
     */
    @Value("${multilevelCache.singleTimeWindow.keyCount}")
    private String userKeyCountValue;
    /**
     * 单个时间窗内统计的最大缓存数
     * 最终采用的数字会覆盖给这个变量，在PostConstruct全部结束后，这个变量读到的就会是最终生效的配置
     */
    private int keyCount = 0;
    /**
     * 时间窗更新锁
     */
    private final ReentrantLock timeWindowMapUpdateLock = new ReentrantLock();
    /**
     * 时间窗表： [时间字符串：(缓存key：热度)]
     */
    private final Map<Integer, Map<String, AtomicInteger>> timeWindowsMap = new LruHashMap<>(TimeWindowConstants.TIME_WINDOW_COUNT, 1F, false, TimeWindowConstants.TIME_WINDOW_COUNT);
    /**
     * 定时检查更新时间窗
     */
    private final static ScheduledThreadPoolExecutor SCHEDULED_SLIDE_EXECUTOR = ThreadPoolUtils.getScheduledThreadPool(1, "MC-TimeWindow");

    /**
     * 初始化时间窗map
     * 读取配置文件等，初始化指定个数的时间窗（会包括时间已经过去的几个）
     */
    @PostConstruct
    private void initTimeWindowMap() {
        LocalDateTime currentTime = LocalDateTime.now();
        for (int i = TimeWindowConstants.TIME_WINDOW_COUNT - 1; i >= 0; i--) {
            int currentWindow = currentTime.plusMinutes(-i).getMinute();
            putNewSingleTimeWindowMap(currentWindow);
        }
        log.info("[Multilevel-Cache]----时间窗共计{}个已初始化完毕", timeWindowsMap.size());
        SCHEDULED_SLIDE_EXECUTOR.scheduleWithFixedDelay(() -> {
            try {
                LocalDateTime currentTimeFlag = LocalDateTime.now();
                for (int i = 0; i <= 1; i++) {
                    int scheduleCurrentTime = currentTimeFlag.plusMinutes(i).getMinute();
                    if (!timeWindowsMap.containsKey(scheduleCurrentTime)) {
                        putNewSingleTimeWindowMap(scheduleCurrentTime);
                    }
                }
            } finally {
                log.debug("[Multilevel-Cache]----目前时间窗状态【时间窗个数={}, 时间窗分别为[{}]】", timeWindowsMap.size(), timeWindowsMap.keySet());
            }
        }, 0, 25, TimeUnit.SECONDS);
    }

    /**
     * 初始化配置信息，读取用户配置的单时间窗统计的最大key个数
     * 若未读取到则会采用TimeWindowsConstants中的DEFAULT_KEY_COUNT_IN_SINGLE_WINDOW
     *
     * @see cn.gk.multilevel.cache.sdk.constants.TimeWindowConstants
     */
    @PostConstruct
    private void initKeyCount() {
        try {
            keyCount = Integer.parseInt(userKeyCountValue);
            keyCount = keyCount <= 0 ? TimeWindowConstants.DEFAULT_KEY_COUNT_IN_SINGLE_WINDOW : keyCount;
        } catch (NumberFormatException numberFormatException) {
            keyCount = TimeWindowConstants.DEFAULT_KEY_COUNT_IN_SINGLE_WINDOW;
        }
        log.info("[Multilevel-Cache]----配置的单窗口最大统计key数为{}个.", keyCount);
    }

    /**
     * 向时间窗map添加一个新的窗口
     *
     * @param minuteFlag 时间窗key
     */
    private void putNewSingleTimeWindowMap(int minuteFlag) {
        if (!timeWindowsMap.containsKey(minuteFlag)) {
            timeWindowMapUpdateLock.lock();
            try {
                if (!timeWindowsMap.containsKey(minuteFlag)) {
                    timeWindowsMap.put(minuteFlag, new ConcurrentLinkedHashMap.Builder<String, AtomicInteger>()
                            .maximumWeightedCapacity(keyCount)
                            .weigher(Weighers.singleton())
                            .build());
                }
            } finally {
                timeWindowMapUpdateLock.unlock();
            }
        }
    }

    /**
     * 为指定时间窗的某个key增加热度
     *
     * @param key           缓存key
     * @param currentWindow 时间窗key
     */
    private void tryIncreaseCount(String key, int currentWindow) {
        Map<String, AtomicInteger> currentTimeWindowMap = timeWindowsMap.get(currentWindow);
        if (currentTimeWindowMap.containsKey(key)) {
            currentTimeWindowMap.get(key).incrementAndGet();
        } else {
            currentTimeWindowMap.put(key, new AtomicInteger(1));
        }
    }

    /**
     * 为当前时间窗的某个缓存增加热度1点
     *
     * @param key 缓存key
     */
    public void increaseCacheHot(String key) {
        int currentWindow = LocalDateTime.now(ZoneId.systemDefault()).getMinute();
        if (!timeWindowsMap.containsKey(currentWindow)) {
            putNewSingleTimeWindowMap(currentWindow);
        }
        tryIncreaseCount(key, currentWindow);
    }

    /**
     * 获取当前的时间窗缓存热度数据，放到list里
     *
     * @return 当前的n个时间窗的统计信息
     */
    public List<Map<String, AtomicInteger>> getCurrentWindowsMapDataList() {
        return new ArrayList<>(timeWindowsMap.values());
    }
}
