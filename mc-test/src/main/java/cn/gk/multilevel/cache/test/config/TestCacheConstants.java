package cn.gk.multilevel.cache.test.config;

import cn.gk.multilevel.cache.sdk.model.BaseCacheConstants;
import org.springframework.context.annotation.Configuration;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.test.config</h4>
 * <p></p>
 *
 * @author zora
 * @since 2020.07.23
 */
@Configuration
public class TestCacheConstants extends BaseCacheConstants {
    /*
     * Cache配置格式：  CACHE_NAME_PREFIX为cache name的公共前缀，反射会扫描这个前缀
     * public static final String CACHE_NAME_DEMO = "Bla_Bla_Bla_Bla";
     *
     * Cache配置格式：  CACHE_TTL0_PREFIX为缓存过期时间的公共前缀，单位是int的秒，反射会扫描这个前缀
     * public static final int CACHE_TTL_DEMO = 100;
     */
    /* Cache Config Start */
    public static final String CACHE_NAME_ES_BASIC_SEARCH = "Es_Basic_Search";
    public static final int CACHE_TTL_ES_BASIC_SEARCH = 600;
    public static final String CACHE_NAME_ES_REPOSITORY_SEARCH = "Es_Repository_Search";
    public static final int CACHE_TTL_ES_REPOSITORY_SEARCH = 600;
    public static final String CACHE_NAME_ES_GAME_RECOMMEND_POOL_SEARCH = "Es_Recommend_Pool_Search";
    public static final int CACHE_TTL_ES_GAME_RECOMMEND_POOL_SEARCH = 600;
    /* Cache Config End */
}
