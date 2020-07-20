package cn.gk.multilevel.cache.test;

import cn.gk.multilevel.cache.sdk.api.McTemplate;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class CacheSdkTest {

    @Autowired
    private McTemplate multilevelCacheTemplate;

    class ActAsNormalUser implements Runnable {

        CountDownLatch countDownLatch;
        String keyPrefix;
        int counter;

        ActAsNormalUser(CountDownLatch countDownLatch, String keyPrefix, int counter) {
            this.countDownLatch = countDownLatch;
            this.keyPrefix = keyPrefix;
            this.counter = counter;
        }

        @Override
        public void run() {
            try {
                countDownLatch.await(); // 等待其它线程
                for (int i = 0; i < 99999; i++) {
                    String key = keyPrefix + (RandomUtil.randomInt(1,counter));
                    log.info("激活-{}", key);
                    multilevelCacheTemplate.tryGetValue(key, String.class);
                    try {
                        Thread.sleep(50);
                    }catch (Exception ignore){}
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void redisWriteAndGet() {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        int low = 99;
        int high = 5;
        String keyPrefix = "测试key";
        String valuePrefix = "测试";
        for (int i = 0; i < low; i++) {
            String key = keyPrefix + (i + 1);
            log.info("激活-{}", key);
            multilevelCacheTemplate.putObjectIntoCache(key, valuePrefix + (i + 1));
        }
        new Thread(new ActAsNormalUser(countDownLatch,keyPrefix,low)).start();
        countDownLatch.countDown();
        new Thread(new ActAsNormalUser(countDownLatch,keyPrefix,high)).start();
        countDownLatch.countDown();
        try {
            Thread.sleep(1000 * 5);
        } catch (Exception ignore) {
        }
        for (int i = 0; i < low; i++) {
            String key = keyPrefix + (i + 1);
            log.info("激活-{}", key);
            multilevelCacheTemplate.putObjectIntoCache(key, valuePrefix + (i + 1));
        }
        for (int i = 0; i < low; i++) {
            String key = keyPrefix + (i + 1);
            log.info("激活-{}", key);
            multilevelCacheTemplate.tryGetValue(key, String.class);
        }
        try {
            Thread.sleep(1000 * 5);
        } catch (Exception ignore) {
        }
        for (int i = 0; i < low; i++) {
            String key = keyPrefix + (i + 1);
            log.info("激活-{}", key);
            multilevelCacheTemplate.tryGetValue(key, String.class);
        }
        Assert.assertNull(multilevelCacheTemplate.tryGetValue(keyPrefix + (low + 1), String.class));
        log.info("通过LRU检验");
        Assert.assertEquals(multilevelCacheTemplate.tryGetValue(keyPrefix + 1, String.class), valuePrefix + 1);
        log.info("通过Get检验");
    }
}