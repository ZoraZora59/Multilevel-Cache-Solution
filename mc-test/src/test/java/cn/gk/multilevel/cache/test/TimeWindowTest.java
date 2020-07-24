package cn.gk.multilevel.cache.test;

import cn.gk.multilevel.cache.sdk.service.ITimeWindowService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.test</h4>
 * <p>时间窗</p>
 *
 * @author zora
 * @since 2020.07.24
 */
@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class TimeWindowTest {
    @Autowired
    private ITimeWindowService timeWindowService;

    @Test
    public void increaseHot() {
        String key = "mydemo";
        timeWindowService.increaseCacheHot(key);
        List<Map<String, AtomicInteger>> windowList = timeWindowService.getCurrentWindowsMapDataList();
        Assert.assertTrue(checkWindowListToFindExistLevelOneHotKey(windowList, key));
        log.info("热度滑窗统计正常");
    }

    private boolean checkWindowListToFindExistLevelOneHotKey(List<Map<String, AtomicInteger>> windowList, String targetKey) {
        for (Map<String, AtomicInteger> keyMap : windowList) {
            if (keyMap.containsKey(targetKey)) {
                return keyMap.get(targetKey).get() == 1;
            }
        }
        return false;
    }
}
