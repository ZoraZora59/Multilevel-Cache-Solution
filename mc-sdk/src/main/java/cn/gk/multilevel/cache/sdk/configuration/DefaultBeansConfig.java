package cn.gk.multilevel.cache.sdk.configuration;

import cn.gk.multilevel.cache.sdk.model.CacheConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.configuration</h4>
 * <p>默认配置</p>
 *
 * @author zora
 * @since 2020.07.16
 */
@Configuration
@Slf4j
public class DefaultBeansConfig {
    @Bean
    @ConditionalOnMissingBean
    public static CacheConfiguration defaultCache() {
        log.info("[Multilevel-Cache]----没有找到自定义的的配置方案，已采用默认的缓存配置进行加载");
        return CacheConfiguration.builder().build();
    }
}
