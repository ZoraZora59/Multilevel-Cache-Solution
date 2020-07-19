package cn.gk.multilevel.cache.test;

import cn.gk.multilevel.cache.sdk.api.McTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class CacheSdkTest {

    @Autowired
    private McTemplate multilevelCacheTemplate;

    @Test
    public void redisWriteAndGet() {
        String keyPrefix = "测试key";
        String valuePrefix = "测试";
        for (int i=0;i<7;i++){
            String key = keyPrefix+(i+1);
            log.info("激活-{}",key);
            multilevelCacheTemplate.putObjectIntoCache(key, valuePrefix+(i+1));
        }
        for (int i=0;i<7;i++){
            String key = keyPrefix+(i+1);
            log.info("激活-{}",key);
            multilevelCacheTemplate.tryGetValue(key, String.class);
        }
        for (int i=0;i<3;i++){
            String key = keyPrefix+(i+1);
            log.info("激活-{}",key);
            multilevelCacheTemplate.tryGetValue(key, String.class);
        }
        try {
            Thread.sleep(1000*5);
        }catch (Exception ignore){
        }
        for (int i=0;i<7;i++){
            String key = keyPrefix+(i+1);
            log.info("激活-{}",key);
            multilevelCacheTemplate.putObjectIntoCache(key, valuePrefix+(i+1));
        }
        for (int i=0;i<7;i++){
            String key = keyPrefix+(i+1);
            log.info("激活-{}",key);
            multilevelCacheTemplate.tryGetValue(key, String.class);
        }
        try {
            Thread.sleep(1000*5);
        }catch (Exception ignore){
        }
        for (int i=0;i<7;i++){
            String key = keyPrefix+(i+1);
            log.info("激活-{}",key);
            multilevelCacheTemplate.tryGetValue(key, String.class);
        }
        Assert.assertNull(multilevelCacheTemplate.tryGetValue(keyPrefix+"6",String.class));
        log.info("通过LRU检验");
        Assert.assertEquals(multilevelCacheTemplate.tryGetValue(keyPrefix+"1",String.class),valuePrefix+"1");
        log.info("通过Get检验");
    }
}