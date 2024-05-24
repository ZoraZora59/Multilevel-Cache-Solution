//package cn.gk.multilevel.cache.sdk.service.caffeine;
//
//import cn.gk.multilevel.cache.sdk.service.ConfigCenter;
//import cn.gk.multilevel.cache.sdk.service.ICounterService;
//import cn.gk.multilevel.cache.sdk.service.IHotKeyManager;
//import cn.gk.multilevel.cache.sdk.service.ICacheService;
//import cn.gk.multilevel.cache.sdk.util.ThreadPoolUtils;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONException;
//import com.github.benmanes.caffeine.cache.Cache;
//import org.apache.logging.log4j.util.Strings;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import javax.annotation.PostConstruct;
//import java.util.List;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * <h3>multilevel-cache-solution</h3>
// * <h4>cn.gk.multilevel.cache.sdk.service.caffeine</h4>
// * <p>采用Object强转方式的实现</p>
// *
// * @author zora
// * @since 2020.07.21
// */
//@Service
//@Primary
//
//public class ObjectCacheServiceImpl implements ICacheService {
//    @Autowired
//    private ICounterService counterService;
//    @Autowired
//    private IHotKeyManager hotCacheManager;
//    private long redisTtl;
//    private volatile static Cache<String, Object> localCache;
//    private static StringRedisTemplate stringRedisTemplate;
//    private static final ScheduledThreadPoolExecutor SCHEDULE_EXECUTOR = ThreadPoolUtils.getScheduledThreadPool(1, "MC-Reporter");
//
//    @Autowired
//    @Qualifier("MultilevelCacheInRam")
//    public void initializeCache(Cache<String, String> cache) {
//        localCache = cache;
//        log.info("[Multilevel-Cache]----已加载缓存配置{}", cache.getClass());
//    }
//
//    @Autowired
//    public void initializeCache(StringRedisTemplate autowiredStringRedisTemplate) {
//        stringRedisTemplate = autowiredStringRedisTemplate;
//        log.info("[Multilevel-Cache]----已加载Redis配置{}", autowiredStringRedisTemplate.getClass());
//    }
//
//    @Autowired
//    public void initializeRedisTtl(ConfigCenter configCenter) {
//        redisTtl = configCenter.getRedisTtl();
//        log.info("[Multilevel-Cache]----已加载Redis过期时间配置为{}秒", redisTtl);
//    }
//
//    /**
//     * 定时报告缓存状态任务
//     * TODO:解决报告结果永远为0的问题
//     */
//    @PostConstruct
//    private void scheduleReporter() {
//        SCHEDULE_EXECUTOR.scheduleWithFixedDelay(() -> {
//            log.info("[Multilevel-Cache]----当前本地缓存报告：{}", localCache.stats().toString());
//        }, 15, 60, TimeUnit.SECONDS);
//    }
//
//    /**
//     * 尝试获取caffeine中的缓存数据
//     *
//     * @param key 缓存key
//     * @return json形式的value或null
//     */
//    private String tryGetFromRam(String key) {
//        return localCache.getIfPresent(key);
//    }
//
//    /**
//     * 尝试获取Redis中的缓存数据
//     *
//     * @param key 缓存key
//     * @return json形式的value或null
//     */
//    private String tryGetFromRedis(String key) {
//        return stringRedisTemplate.opsForValue().get(key);
//    }
//
//    /**
//     * 链式尝试获取数据，先Ram后Redis
//     *
//     * @param key 缓存key
//     * @return json形式的value或null
//     */
//    private String tryGetValueByChainTrace(String key) {
//        String targetValue;
//        targetValue = tryGetFromRam(key);
//        if (StringUtils.isEmpty(targetValue)) {
//            log.info("在Ram中获取失败，尝试从Redis获取");
//            targetValue = tryGetFromRedis(key);
//            writeIntoRamConditionOnHotManager(key, targetValue);
//        }
//        return targetValue;
//    }
//
//    private void writeIntoRamConditionOnHotManager(String key, String jsonString) {
//        if (Strings.isNotBlank(key) && hotCacheManager.isHotKey(key) && Strings.isNotBlank(jsonString)) {
//            localCache.put(key, jsonString);
//        }
//    }
//
//    /**
//     * 尝试获取缓存中的对象，可能会拿到null
//     *
//     * @param key   缓存的key
//     * @param clazz 反序列化到的类
//     * @return 对象或null
//     * @throws JSONException json序列化失败
//     */
//    @Override
//    public <V> V tryGetValue(String key, Class<V> clazz) throws JSONException {
//        counterService.asyncIncreaseCacheHot(key);
//        return JSON.parseObject(tryGetValueByChainTrace(key), clazz);
//    }
//
//    /**
//     * 尝试获取缓存中的列表对象，可能会拿到null
//     *
//     * @param key   缓存的key
//     * @param clazz 反序列化到的类
//     * @return 对象列表或null
//     * @throws JSONException json序列化失败
//     */
//    @Override
//    public <V> List<V> tryGetValueArrays(String key, Class<V> clazz) throws JSONException {
//        counterService.asyncIncreaseCacheHot(key);
//        return JSON.parseArray(tryGetValueByChainTrace(key), clazz);
//    }
//
//    /**
//     * 写入缓存
//     *
//     * @param key   缓存key
//     * @param value 缓存对象
//     */
//    @Override
//    public <T> void putObjectIntoCache(String key, T value) {
//        putObjectIntoCache(key, value, redisTtl);
//    }
//
//    /**
//     * 写入缓存
//     *
//     * @param key   缓存key
//     * @param value 缓存对象
//     * @param ttl   过期时间，单位秒
//     */
//    @Override
//    public <T> void putObjectIntoCache(String key, T value, long ttl) {
//        stringRedisTemplate.opsForValue().set(key, value.toString(), ttl, TimeUnit.SECONDS);
//        writeIntoRamConditionOnHotManager(key, value.toString());
//    }
//
//    /**
//     * 清理本地缓存空间
//     */
//    @Override
//    public void cleanUpRamCache() {
//        localCache.cleanUp();
//    }
//
//    /**
//     * 清理指定的key
//     */
//    @Override
//    public void cleanCacheByKey(String key) {
//        stringRedisTemplate.delete(key);
//        localCache.invalidate(key);
//    }
//}
