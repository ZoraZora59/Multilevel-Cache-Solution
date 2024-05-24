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
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
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

    /**
     * 表达式解析器，用于获取key
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    @Pointcut("@annotation(cn.gk.multilevel.cache.sdk.api.MultilevelCacheable)")
    public void pointCut() {
    }
    // TODO 添加另一个注解，用于删除或更新缓存

    @Around("pointCut()")
    @SuppressWarnings({"unchecked","rawtypes"})
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = ((MethodSignature) pjp.getSignature());
        MultilevelCacheable mcAnnotation = signature.getMethod().getAnnotation(MultilevelCacheable.class);
        // 设置解析上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 设置上下文的变量为方法的参数
        String[] paramNames = signature.getParameterNames();
        Object[] args = pjp.getArgs();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        // 解析表达式
        String keyExpression = mcAnnotation.cacheName();
        String cacheName = parser.parseExpression(keyExpression).getValue(context, String.class);
        String key = keyGenerator.generate(signature, cacheName, signature.getParameterNames(), pjp.getArgs());
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
