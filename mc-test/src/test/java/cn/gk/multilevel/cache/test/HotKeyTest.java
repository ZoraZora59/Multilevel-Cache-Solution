package cn.gk.multilevel.cache.test;

import cn.gk.multilevel.cache.sdk.model.CacheConfiguration;
import cn.gk.multilevel.cache.sdk.service.IHotKeyManager;
import cn.gk.multilevel.cache.sdk.service.ITimeWindowService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.test</h4>
 * <p>热key测试</p>
 *
 * @author zora
 * @since 2020.07.24
 */
@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class HotKeyTest {
    @Autowired
    private IHotKeyManager hotKeyManager;
    @Autowired
    private ITimeWindowService timeWindowService;

    @Bean
    @Primary
    public static CacheConfiguration defaultLocalCache() {
        System.out.println("测试类：启用自定义的本地缓存");
//        return new MultilevelCacheBuilder().withMemoryUsage(64*1024).buildByWeightStrategy();
        return CacheConfiguration.builder().singleWindowMaximumKeyCount(5).localCacheCountSize(3).redisTtl(1).build();
    }

    public static final String KEY_1 = "aaa";
    public static final String KEY_2 = "bbb";
    public static final String KEY_3 = "ccc";
    public static final String KEY_4 = "ddd";
    public static final String KEY_5 = "eee";

    @Before
    public void initTestData() {
        CountDownLatch countDownLatch = new CountDownLatch(5);
        new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignore) {
            }
            for (int i = 0; i < 200; i++) {
                timeWindowService.increaseCacheHot(KEY_1);
            }
        }).start();
        countDownLatch.countDown();
        new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignore) {
            }
            for (int i = 0; i < 180; i++) {
                timeWindowService.increaseCacheHot(KEY_2);
            }
        }).start();
        countDownLatch.countDown();
        new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignore) {
            }
            for (int i = 0; i < 160; i++) {
                timeWindowService.increaseCacheHot(KEY_3);
            }
        }).start();
        countDownLatch.countDown();
        new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignore) {
            }
            for (int i = 0; i < 140; i++) {
                timeWindowService.increaseCacheHot(KEY_4);
            }
        }).start();
        countDownLatch.countDown();
        new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignore) {
            }
            for (int i = 0; i < 100; i++) {
                timeWindowService.increaseCacheHot(KEY_5);
            }
        }).start();
        countDownLatch.countDown();
    }

    @Test
    public void hotKeyFind() {
        try {
            Thread.sleep(5000);
        }catch (InterruptedException ignore){
        }
        Assert.assertTrue(hotKeyManager.isHotKey(KEY_1));
        Assert.assertTrue(hotKeyManager.isHotKey(KEY_2));
        Assert.assertTrue(hotKeyManager.isHotKey(KEY_3));
        Assert.assertFalse(hotKeyManager.isHotKey(KEY_4));
        Assert.assertFalse(hotKeyManager.isHotKey(KEY_5));
    }

}
