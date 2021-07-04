package cn.gk.multilevel.cache.sdk.aop;

import cn.gk.multilevel.cache.sdk.api.McTemplate;
import cn.gk.multilevel.cache.sdk.api.MultilevelCacheKeyGenerator;
import cn.gk.multilevel.cache.sdk.api.MultilevelCacheable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.aop</h4>
 * <p>多级缓存写入</p>
 *
 * @author zora
 * @since 2020.07.21
 */
@Aspect
@Component
public class MultilevelCacheableAop {
    @Autowired
    private McTemplate mcTemplate;
    @Autowired
    private MultilevelCacheKeyGenerator keyGenerator;

    @Pointcut("@annotation(cn.gk.multilevel.cache.sdk.api.MultilevelCacheable)")
    public void pointCut() {
    }

    @Around("pointCut()")
    @SuppressWarnings({"unchecked","rawtypes"})
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = ((MethodSignature) pjp.getSignature());
        MultilevelCacheable mcAnnotation = signature.getMethod().getAnnotation(MultilevelCacheable.class);
        String key = keyGenerator.generate(signature, mcAnnotation.cacheName(), signature.getParameterNames(), pjp.getArgs());
        Class clz = signature.getReturnType();
        if (mcAnnotation.isCollection()
                || clz.isAssignableFrom(Collection.class)
                || clz.isAssignableFrom(List.class)
                || clz.isAssignableFrom(Map.class)
                || clz.isAssignableFrom(Queue.class)) {
            List res = mcTemplate.tryGetValueArrays(key, clz);
            if (!CollectionUtils.isEmpty(res)){
                return res;
            }
        }else {
            Object res = mcTemplate.tryGetValue(key,clz);
            if (Objects.nonNull(res)){
                return res;
            }
        }
        Object res = pjp.proceed();
        if (mcAnnotation.ttl()>0){
            mcTemplate.putObjectIntoCache(key,res,mcAnnotation.ttl());
        }else {
            mcTemplate.putObjectIntoCache(key,res);
        }
        return res;
    }
}
