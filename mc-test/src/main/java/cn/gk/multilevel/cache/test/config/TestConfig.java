package cn.gk.multilevel.cache.test.config;

import cn.gk.multilevel.cache.sdk.model.CacheConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.test.config</h4>
 * <p></p>
 *
 * @author zora
 * @since 2020.07.16
 */
@Configuration
public class TestConfig {

    @Bean
    public static CacheConfiguration defaultLocalCache() {
        System.out.println("测试类：启用自定义的本地缓存");
//        return new MultilevelCacheBuilder().withMemoryUsage(64*1024).buildByWeightStrategy();
        return CacheConfiguration.builder().singleWindowMaximumKeyCount(100).localCacheCountSize(10).redisTtl(3).build();
    }
}
