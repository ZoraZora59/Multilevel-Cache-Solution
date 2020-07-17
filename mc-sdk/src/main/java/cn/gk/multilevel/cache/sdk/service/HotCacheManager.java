package cn.gk.multilevel.cache.sdk.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.service</h4>
 * <p>热数据发现处理器</p>
 *
 * @author zora
 * @since 2020.07.16
 */
@Service
class HotCacheManager {
    @Autowired
    private TimeWindowService timeWindowService;
    private static final ScheduledThreadPoolExecutor scheduleHotKeyFinderExecutor=new ScheduledThreadPoolExecutor(1,
            new ThreadFactoryBuilder().setNameFormat("MC-HotKeyFinder").build());
    @PostConstruct
    private void scheduleSetting(){
//        scheduleHotKeyFinderExecutor.scheduleWithFixedDelay(timeWindowService.)
    }
}
