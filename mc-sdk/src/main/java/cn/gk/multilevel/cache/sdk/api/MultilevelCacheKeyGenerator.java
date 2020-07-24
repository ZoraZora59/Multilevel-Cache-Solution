package cn.gk.multilevel.cache.sdk.api;

import org.aspectj.lang.reflect.MethodSignature;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.api</h4>
 * <p>多级缓存标记的key生成器接口</p>
 *
 * @author zora
 * @since 2020.07.21
 */
public interface MultilevelCacheKeyGenerator {
    /**
     * 生成缓存key
     *
     * @param signature      方法签名
     * @param cacheName      用户自定义的 cache name
     * @param parameterNames 参数名
     * @param paramValues    参数值
     * @return 生成的最终key
     */
    String generate(MethodSignature signature, String cacheName, String[] parameterNames, Object[] paramValues);
}
