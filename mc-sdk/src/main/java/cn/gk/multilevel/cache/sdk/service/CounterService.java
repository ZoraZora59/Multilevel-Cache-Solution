package cn.gk.multilevel.cache.sdk.service;

import cn.gk.multilevel.cache.sdk.util.ThreadPoolUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.service</h4>
 * <p>计数器处理</p>
 *
 * @author zora
 * @since 2020.07.15
 */
@Service
class CounterService {
    private static final ThreadPoolExecutor counterExecutor= ThreadPoolUtils.getThreadPool(2,4,60,256,"MC-Counter-%d");

}
