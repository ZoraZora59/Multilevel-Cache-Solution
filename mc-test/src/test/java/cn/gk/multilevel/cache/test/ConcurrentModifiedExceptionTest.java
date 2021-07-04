package cn.gk.multilevel.cache.test;

import cn.gk.multilevel.cache.sdk.model.LruHashMap;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.test</h4>
 * <p></p>
 *
 * @author zora
 * @since 2020.07.17
 */
public class ConcurrentModifiedExceptionTest {
    public static void main(String[] args) {
        Demo demo = new Demo();
        demo.test();
        System.exit(0);
    }
}

class Demo {

    private final static Map<Integer, Map<String, AtomicInteger>> timeWindowsMap = new LruHashMap<>(4,1F,false,4);
    private final static ScheduledExecutorService scheduledSlideExecutor = Executors.newSingleThreadScheduledExecutor();

    public void test() {
        LocalDateTime time = LocalDateTime.now();
        for (int i = 3; i >= 0; i--) {
            timeWindowsMap.put(time.plusMinutes(-i).getMinute(), new ConcurrentLinkedHashMap.Builder<String, AtomicInteger>()
                    .maximumWeightedCapacity(3).weigher(Weighers.singleton()).build());
        }
        new Thread(() -> {
            int i = 0;
            while (i < 10000) {
                timeWindowsMap.get(LocalDateTime.now().getMinute()).put(UUID.randomUUID().toString(), new AtomicInteger(1));
                i++;
            }
        }).start();
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
                System.out.println("[Multilevel-Cache]----目前时间窗状态【时间窗个数=" + timeWindowsMap.size() + ", 时间窗分别为" + timeWindowsMap.keySet());
            }
        }, 0, 25, TimeUnit.SECONDS);
        try {
            Thread.sleep(50);
            List<Map<String, AtomicInteger>> currentWindowsDataList = new ArrayList<>(timeWindowsMap.values());
            Map<String, Integer> counterMap = new HashMap<>(64);
            for (Map<String, AtomicInteger> singleWindowDataMap : currentWindowsDataList) {
                Set<String> keySet = new HashSet<>(singleWindowDataMap.keySet());
                for (String key : keySet) {
                    AtomicInteger currentCount = singleWindowDataMap.get(key);
                    if (counterMap.containsKey(key)) {
                        counterMap.put(key, counterMap.get(key) + (currentCount==null?0:currentCount.get()));
                    } else {
                        counterMap.put(key, (currentCount==null?0:currentCount.get()));
                    }
                }
            }
            System.out.println("成功");
        } catch (Throwable throwable) {
            System.out.println("sb");
            throwable.printStackTrace();
        }
    }

    private void putNewSingleTimeWindowMap(int minuteFlag) {
        if (!timeWindowsMap.containsKey(minuteFlag)) {
            if (!timeWindowsMap.containsKey(minuteFlag)) {
                timeWindowsMap.put(minuteFlag, new ConcurrentLinkedHashMap.Builder<String, AtomicInteger>()
                        .maximumWeightedCapacity(3).weigher(Weighers.singleton()).build());
            }
        }
    }
}