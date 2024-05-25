package cn.gk.multilevel.cache.sdk.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * RedisTemplateConfig
 *
 * @author 阿左
 * @since 2024/05/25
 */
@Slf4j
@Configuration
public class RedisTemplateConfig {

    @Value("${mlc.host:null}")
    private String host;
    @Value("${mlc.port:0}")
    private int port;
    @Value("${mlc.connectionTimeout:0}")
    private int connectionTimeout;
    @Value("${mlc.password:#{null}}")
    private String password;


    @Bean(name = "MultilevelCacheRedisTemplate")
    @ConditionalOnMissingBean
    public StringRedisTemplate autowiredStringRedisTemplate() {
        if(StringUtils.isEmpty(host) || port == 0) {
            log.warn("[Multilevel-Cache]----Redis配置获取失败，无法加载RedisTemplate");
            return null;
        }

        JedisClientConfiguration clientConfiguration= JedisClientConfiguration.builder()
                .connectTimeout(Duration.ofMillis(connectionTimeout))
                .usePooling()
                .build();
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        if(!StringUtils.isEmpty(password)) {
            standaloneConfiguration.setPassword(password);
        }
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(standaloneConfiguration,clientConfiguration);
        jedisConnectionFactory.afterPropertiesSet();

        return getStringRedisTemplate(jedisConnectionFactory);
    }

    private static StringRedisTemplate getStringRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);

        // 设置键（key）的序列化采用StringRedisSerializer。
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // 设置值（value）的序列化采用GenericJackson2JsonRedisSerializer。
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
