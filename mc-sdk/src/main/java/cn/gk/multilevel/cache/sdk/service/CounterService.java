package cn.gk.multilevel.cache.sdk.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.service</h4>
 * <p>计数器处理</p>
 *
 * @author zora
 * @since 2020.07.15
 */
@Service
public class CounterService {
    private static final ThreadPoolExecutor counterExecutor=new ThreadPoolExecutor(2,4,
            60, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(256),
            new ThreadFactoryBuilder().setNameFormat("MC-Counter-%d").build());

}
