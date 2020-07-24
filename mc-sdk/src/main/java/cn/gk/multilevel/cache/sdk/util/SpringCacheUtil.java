package cn.gk.multilevel.cache.sdk.util;

import cn.gk.multilevel.cache.sdk.model.BaseCacheConstants;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * <h3>metaapp-gamex</h3>
 * <h4>com.metaapp.cloud.gamex.config</h4>
 * <p>SpringCache配置</p>
 *
 * @author Yuhan.Ji
 * @since 2020.03.23
 */
@Slf4j
@Configuration
public class SpringCacheUtil {

    @Value("${spring.application.name}")
//    private static String APP_NAME="Default";
    private String APP_NAME="Default";
    /**
     * 当没有配置缓存失效时间情况下的默认ttl
     */
    public static final int DEFAULT_EXPIRE_SECOND = 180;

    @Bean
    @ConditionalOnBean(value = BaseCacheConstants.class)
    public RedisCacheManager builder(RedisConnectionFactory factory, @Autowired BaseCacheConstants clazz) {
        try {
            // 这里的参数填充对应的静态属性类
            Map<String, Integer> configMap = ReflectUtil.getCacheTtlConfigMap(clazz.getClass());
            RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
                    .fromConnectionFactory(factory);
            if (!configMap.isEmpty()) {
                Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
                for (String cacheName : configMap.keySet()) {
                    configurationMap.
                            put(cacheName, RedisCacheConfiguration
                                    .defaultCacheConfig()
                                    .computePrefixWith(name -> APP_NAME + ":" + name + ":")
                                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()))
                                    .entryTtl(Duration.ofSeconds(configMap.get(cacheName))));
                }
                builder.withInitialCacheConfigurations(configurationMap);
                log.info("=== MetaApp Spring Cache Redis : 缓存配置为：{} ===", configMap.toString());
            }
            return builder.build();
        } catch (IllegalAccessException illegalEx) {
            log.error("SpringCache RedisCacheManagerBuilderCustomizer配置失败，启动终止。请检查RedisCacheManagerBuilder类中ReflectUtil.getCacheTtlConfigMap()部分", illegalEx);
            System.exit(500);
            return null;
        }
    }

//    // 用于SpringBoot2.2以上的配置方式
//    @Bean
//    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
//        return builder -> {
//            try {
//                // 这里的参数填充对应的静态属性类
//                Map<String, Integer> configMap = ReflectUtil.getCacheTtlConfigMap(Constants.class);
//                if (!configMap.isEmpty()) {
//                    Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
//                    for (String cacheName : configMap.keySet()) {
//                        configurationMap.
//                                put(cacheName, RedisCacheConfiguration
//                                        .defaultCacheConfig()
//                                        .computePrefixWith(name -> appName + ":" + name + ":")
//                                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()))
//                                        .entryTtl(Duration.ofSeconds(configMap.get(cacheName))));
//                    }
//                    builder.withInitialCacheConfigurations(configurationMap);
//                    log.info("Redis缓存配置为：{}",configMap.toString());
//                }
//            } catch (IllegalAccessException illegalEx) {
//                log.error("SpringCache RedisCacheManagerBuilderCustomizer配置失败，启动终止。请检查RedisCacheManagerBuilder类中ReflectUtil.getCacheTtlConfigMap()部分", illegalEx);
//                System.exit(500);
//            }
//        };
//    }

    static class ReflectUtil {
        public static final String CACHE_NAME_PREFIX = "CACHE_NAME_";
        public static final String CACHE_TTL_PREFIX = "CACHE_TTL_";

        static <T> Map<String, Integer> getCacheTtlConfigMap(Class<T> clazz) throws IllegalAccessException {
            Map<String, Integer> cacheMapWithKeyTtl = new HashMap<>();
            Field[] fields = clazz.getFields();
            Map<String, String> cacheNames = new HashMap<>(fields.length);
            // 配置cacheName
            for (Field field : fields) {
                if (field.getName().contains(CACHE_NAME_PREFIX)) {
                    if (field.getType().equals(String.class)) {
                        String lookingForTtlName = field.getName().replace(CACHE_NAME_PREFIX, CACHE_TTL_PREFIX);
                        String value = (String) field.get(clazz);
                        cacheNames.put(lookingForTtlName, value);
                    } else {
                        throw new IllegalAccessException("In constants configuration, the type of prefix of cache must be String. Illegal in " + clazz.getName() + "." + field.getName() + ".");
                    }
                }
            }
            // 配置过期时间
            for (Field field : fields) {
                if (field.getName().contains(CACHE_TTL_PREFIX)) {
                    if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
                        if (cacheNames.containsKey(field.getName())) {
                            cacheMapWithKeyTtl.put(cacheNames.get(field.getName()), field.getInt(clazz));
                        }
                    } else {
                        throw new IllegalAccessException("In constants configuration, the type of prefix of ttl must be int or Integer. Illegal in " + clazz.getName() + "." + field.getName() + ".");
                    }
                }
            }
            // 配置默认过期时间
            for (String cacheNameKey : cacheNames.keySet()) {
                String cacheName = cacheNames.get(cacheNameKey);
                if (!cacheMapWithKeyTtl.containsKey(cacheName)) {
                    log.warn("===========================Spring Cache Config 配置信息错误=========================");
                    log.warn("Spring Cache Config 配置信息错误：{}文件中的缓存{}没有查询到对应失效时间的配置信息，将按默认值180秒执行。", clazz.getName(), cacheName);
                    log.warn("============================= MetaApp Cache Config ===============================");
                    cacheMapWithKeyTtl.put(cacheName, DEFAULT_EXPIRE_SECOND);
                }
            }
            return cacheMapWithKeyTtl;
        }
    }
}