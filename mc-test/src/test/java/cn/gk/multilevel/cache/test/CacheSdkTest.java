package cn.gk.multilevel.cache.test;

import cn.gk.multilevel.cache.sdk.api.McTemplate;
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

    @Test
    public void redisWriteAndGet() {
        multilevelCacheTemplate.putObjectIntoCache("测试key", new ArrayList<>());
        multilevelCacheTemplate.putObjectIntoCache("测试key2", "wafawefaw");
        System.out.println(multilevelCacheTemplate.tryGetValue("测试key2", String.class));
        System.out.println(multilevelCacheTemplate.tryGetValueArrays("测试key", List.class));
        multilevelCacheTemplate.cleanCacheByKey("测试key");
        multilevelCacheTemplate.cleanCacheByKey("测试key2");
    }
}