package cn.gk.multilevel.cache.sdk.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.util</h4>
 * <p>基本线程池工具</p>
 * 主要用于简化业务代码中关于新建线程池的相关代码
 *
 * @author zora
 * @since 2020.07.19
 */
public class ThreadPoolUtils {
    /**
     * 默认的超量线程存活时间，单位秒
     */
    private static final int DEFAULT_ALIVE_SECONDS = 60;

    /**
     * 获取固定大小的线程池
     *
     * @param threadCount   线程数
     * @param queueSize     任务队列长度
     * @param nameFormatter 名称格式化字符串，如"my-single-thread-pool", "my-demo-pool-%d"
     * @return 线程数量固定的线程池
     */
    public static ThreadPoolExecutor getFixedThreadPool(int threadCount, int queueSize, String nameFormatter) {
        return getThreadPool(threadCount, threadCount, 0, queueSize, nameFormatter);
    }

    /**
     * 获取线程池
     * 默认的超量线程存活时间为60秒
     *
     * @param base          基础线程数
     * @param max           最大线程数
     * @param queueSize     任务队列长度
     * @param nameFormatter 名称格式化字符串，如"my-single-thread-pool", "my-demo-pool-%d"
     * @return 生成后的线程池
     */
    public static ThreadPoolExecutor getThreadPool(int base, int max, int queueSize, String nameFormatter) {
        return getThreadPool(base, max, DEFAULT_ALIVE_SECONDS, queueSize, nameFormatter);
    }

    /**
     * 获取有着最大任务队列长度的线程池
     * 默认的超量线程存活时间为60秒
     *
     * @param base          基础线程数
     * @param max           最大线程数
     * @param nameFormatter 名称格式化字符串，如"my-single-thread-pool", "my-demo-pool-%d"
     * @return 生成后的线程池
     */
    public static ThreadPoolExecutor getThreadPoolWithMaxMissionQueue(int base, int max, String nameFormatter) {
        return getThreadPool(base, max, DEFAULT_ALIVE_SECONDS, Integer.MAX_VALUE, nameFormatter);
    }

    /**
     * 获取有着最大任务队列长度的线程池
     * 默认的超量线程存活时间为60秒
     *
     * @param base          基础线程数
     * @param max           最大线程数
     * @param nameFormatter 名称格式化字符串，如"my-single-thread-pool", "my-demo-pool-%d"
     * @param handler       任务拒绝策略
     * @return 生成后的线程池
     */
    public static ThreadPoolExecutor getThreadPoolWithMaxMissionQueue(int base, int max, String nameFormatter, RejectedExecutionHandler handler) {
        return getThreadPool(base, max, DEFAULT_ALIVE_SECONDS, Integer.MAX_VALUE, nameFormatter, handler);
    }


    /**
     * 获取线程池
     *
     * @param base          基础线程数
     * @param max           最大线程数
     * @param aliveSeconds  超量线程存活时间
     * @param queueSize     任务队列长度
     * @param nameFormatter 名称格式化字符串，如"my-single-thread-pool", "my-demo-pool-%d"
     * @return 生成后的线程池
     */
    public static ThreadPoolExecutor getThreadPool(int base, int max, int aliveSeconds, int queueSize, String nameFormatter) {
        return new ThreadPoolExecutor(base, max, aliveSeconds, TimeUnit.SECONDS, new LinkedBlockingDeque<>(queueSize), new ThreadFactoryBuilder().setNameFormat(nameFormatter).build());
    }

    /**
     * 获取线程池
     *
     * @param base          基础线程数
     * @param max           最大线程数
     * @param aliveSeconds  超量线程存活时间
     * @param queueSize     任务队列长度
     * @param nameFormatter 名称格式化字符串，如"my-single-thread-pool", "my-demo-pool-%d"
     * @param handler       任务拒绝策略
     * @return 生成后的线程池
     */
    public static ThreadPoolExecutor getThreadPool(int base, int max, int aliveSeconds, int queueSize, String nameFormatter, RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(base, max, aliveSeconds, TimeUnit.SECONDS, new LinkedBlockingDeque<>(queueSize), new ThreadFactoryBuilder().setNameFormat(nameFormatter).build(), handler);
    }

    /**
     * 获取定时任务线程池
     *
     * @param threadCount   核心线程数
     * @param nameFormatter 名称格式化字符串，如"my-single-thread-pool", "my-demo-pool-%d"
     * @return 生成后的定时任务线程池
     */
    public static ScheduledThreadPoolExecutor getScheduledThreadPool(int threadCount, String nameFormatter) {
        return new ScheduledThreadPoolExecutor(threadCount, new ThreadFactoryBuilder().setNameFormat(nameFormatter).build());
    }
}
