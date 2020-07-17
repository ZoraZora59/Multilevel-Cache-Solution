package cn.gk.multilevel.cache.sdk.service;

import cn.gk.multilevel.cache.sdk.model.CacheConfiguration;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.service</h4>
 * <p>配置中心</p>
 *
 * @author zora
 * @since 2020.07.17
 */
@Service
public class ConfigCenter {
    private CacheConfiguration configuration;

    @Autowired
    public void setConfiguration(CacheConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean(name = "MultilevelCacheInRam")
    public Cache<String, String> produceCaffeineCache() {
        return MultilevelCacheBuilder.buildWithConfiguration(configuration);
    }

    /**
     * 获取本地缓存的最大数量
     */
    public long getLocalCacheSize() {
        return configuration.getLocalCacheCountSize();
    }

}
