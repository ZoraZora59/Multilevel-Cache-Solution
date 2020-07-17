package cn.gk.multilevel.cache.test;

import cn.gk.multilevel.cache.sdk.api.McTemplate;
import cn.gk.multilevel.cache.sdk.service.TimeWindowService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class CacheSdkTest {

    @Autowired
    private McTemplate multilevelCacheTemplate;
    @Autowired
    private TimeWindowService timeWindowService;

    @Test
    public void redisWriteAndGet() {
        multilevelCacheTemplate.putObjectIntoCache("测试key", new ArrayList<>());
        multilevelCacheTemplate.putObjectIntoCache("测试key2", "wafawefaw");
        System.out.println(multilevelCacheTemplate.tryGetValue("测试key2", String.class));
        System.out.println(multilevelCacheTemplate.tryGetValueArrays("测试key", List.class));
        multilevelCacheTemplate.cleanCacheByKey("测试key");
        multilevelCacheTemplate.cleanCacheByKey("测试key2");
    }

    @Test
    public void cacheHot() {
        timeWindowService.increaseCacheHot("测试key");
        timeWindowService.increaseCacheHot("测试key1");
        timeWindowService.increaseCacheHot("测试key2");
        timeWindowService.increaseCacheHot("测试key3");
        timeWindowService.increaseCacheHot("测试key4");
        timeWindowService.increaseCacheHot("测试key5");
        timeWindowService.increaseCacheHot("测试key");
        timeWindowService.increaseCacheHot("测试key1");
        timeWindowService.increaseCacheHot("测试key6");
        timeWindowService.increaseCacheHot("测试key7");
        timeWindowService.increaseCacheHot("测试key8");
        timeWindowService.increaseCacheHot("测试key9");
        timeWindowService.increaseCacheHot("测试key");
        timeWindowService.increaseCacheHot("测试key1");
    }
}