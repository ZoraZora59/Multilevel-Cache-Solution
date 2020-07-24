package cn.gk.multilevel.cache.sdk.service.generator;

import cn.gk.multilevel.cache.sdk.api.MultilevelCacheKeyGenerator;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.service.generator</h4>
 * <p>默认的key生成器</p>
 *
 * @author zora
 * @since 2020.07.21
 */
@Component
//@ConditionalOnMissingBean(MultilevelCacheKeyGenerator.class)
public class DefaultKeyGenerator implements MultilevelCacheKeyGenerator {
    @Value("${spring.application.name}")
    private String keyPrefix = "default";


    /**
     * 生成缓存key
     *
     * @param signature      方法签名
     * @param cacheName      用户自定义的 cache name
     * @param parameterNames 参数名
     * @param paramValues    参数值
     * @return 生成的最终key
     */
    @Override
    public String generate(MethodSignature signature, String cacheName, String[] parameterNames, Object[] paramValues) {
        StringBuilder builder = new StringBuilder(keyPrefix).append(':');
        if (Strings.isNotBlank(cacheName)) {
            builder.append(cacheName);
        } else {
            builder.append(signature.getName());
        }
        builder.append(':');
        builder.append(buildParamString(parameterNames, paramValues).hashCode());
        return builder.toString();
    }

    private String buildParamString(String[] parameterNames, Object[] paramValues) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parameterNames.length; i++) {
            String name = parameterNames[i] == null ? " " : parameterNames[i];
            String value = paramValues[i] == null ? " " : paramValues[i].toString();
            builder.append(name).append(":").append(value);
        }
        return builder.toString();
    }
}
