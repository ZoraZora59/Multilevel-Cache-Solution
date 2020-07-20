package cn.gk.multilevel.cache.sdk.model;

import com.github.benmanes.caffeine.cache.Weigher;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.model</h4>
 * <p>缓存配置类</p>
 *
 * @author zora
 * @since 2020.07.17
 */
@Builder
public class CacheConfiguration implements Serializable {

    /**
     * 默认配置内存用量 128*1024*1024 Bytes = 128 MB（以权重配置为限制的话）
     */
    private static final long DEFAULT_MAXIMUM_MEMORY_USAGE_SIZE = 128 * 1024 * 1024;
    /**
     * 默认权重公式：Key的Bytes+Value的Bytes
     */
    private static final Weigher<String, String> DEFAULT_WEIGHER = (key, value) -> key.getBytes().length + value.getBytes().length;
    /**
     * 默认最大本地缓存个数（以缓存个数为限制的话）
     */
    private static final long DEFAULT_LOCAL_MEMORY_MAXIMUM_CACHE_KEY_SIZE = 256;
    /**
     * 默认Redis缓存失效时间：3*60 秒 = 3 分钟
     */
    private static final long DEFAULT_REDIS_TTL = 3 * 60;
    /**
     * 默认单窗口统计的Key个数
     */
    private static final int DEFAULT_KEY_COUNT_IN_SINGLE_WINDOW = 256;

    /**
     * 获取权重计算公式
     *
     * @return 权重公式
     */
    public Weigher<String, String> getWeigher() {
        return weigher == null ? DEFAULT_WEIGHER : weigher;
    }

    /**
     * 获取redis过期时间配置
     *
     * @return 权重公式
     */
    public long getRedisTtl() {
        return redisTtl == 0 ? DEFAULT_REDIS_TTL : redisTtl;
    }

    /**
     * 是否采用权重方式进行限制
     *
     * @return 若缓存个数没配置的话，返回true
     */
    public boolean isLimitByWeight() {
        return localCacheCountSize <= 0;
    }

    /**
     * 获取权重限制值
     *
     * @return 权重限制值
     */
    public long getMemoryUsageSize() {
        return memoryUsageSize > 0 ? memoryUsageSize : DEFAULT_MAXIMUM_MEMORY_USAGE_SIZE;
    }

    /**
     * 获取内存缓存个数
     *
     * @return 内存缓存个数
     */
    public long getLocalCacheCountSize() {
        return localCacheCountSize > 0 ? localCacheCountSize : DEFAULT_LOCAL_MEMORY_MAXIMUM_CACHE_KEY_SIZE;
    }

    /**
     * 获取单个时间窗内统计的最大key个数
     *
     * @return 内存缓存个数
     */
    public int getSingleWindowMaximumKeyCount() {
        return singleWindowMaximumKeyCount > 0 ? singleWindowMaximumKeyCount : DEFAULT_KEY_COUNT_IN_SINGLE_WINDOW;
    }

    /**
     * 权重配置
     */
    private final Weigher<String, String> weigher;
    /**
     * 基于权重的最大用量
     */
    private final long memoryUsageSize;
    /**
     * redis的key过期时间
     */
    private final long redisTtl;
    /**
     * 本地缓存最大个数配置
     * 该参数即保存在本地缓存中的返回结果个数，当返回结果较长时，该参数会显著影响缓存的内存占用率。
     * 建议配置在服务内存足够承载的范围内，或不要对该值进行修改，去配置自定义权重计算公式和权重最大值，可以获得更大的自由度。
     */
    private final long localCacheCountSize;
    /**
     * 单个时间窗内统计的最大key个数
     * 该参数对内存占用的影响相比本地缓存最大个数（localCacheCountSize）要低，因此建议适当调高以获得更高的内存缓存命中效率
     * 建议将该值设置大于服务中使用MC的方法个数，在不超过预期总返回结果数的情况下，提高该值可以获得更高的内存命中率.
     */
    private final int singleWindowMaximumKeyCount;

    /**
     * value软引用
     * 配置内存缓存中的数据是否可以以软引用的方式存在，降低内存顶满的风险
     */
    @Getter
    private final boolean softValues;
}
