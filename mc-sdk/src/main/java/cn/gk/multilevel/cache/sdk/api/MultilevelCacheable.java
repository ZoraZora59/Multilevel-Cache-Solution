package cn.gk.multilevel.cache.sdk.api;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.api</h4>
 * <p>多级缓存注解</p>
 *
 * @author zora
 * @since 2020.07.21
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultilevelCacheable {
    /**
     * 缓存key
     */
    @AliasFor(value = "key")
    String cacheName() default "";

    /**
     * 缓存key
     */
    @AliasFor(value = "cacheName")
    String key() default "";

    /**
     * 过期时间
     */
    long ttl() default 0;

    /**
     * 是否集合类型
     */
    boolean isCollection() default false;
}
