package cn.gk.multilevel.cache.sdk.service;

/**
 * <h3>multilevel-cache-solution</h3>
 * <h4>cn.gk.multilevel.cache.sdk.service</h4>
 * <p>热数据发现中心</p>
 *
 * @author zora
 * @since 2020.07.19
 */
public interface IHotKeyManager {
    /**
     * 判断该key是否属于当前的热key
     *
     * @param key 键名
     * @return 热key？
     */
    boolean isHotKey(String key);
}
