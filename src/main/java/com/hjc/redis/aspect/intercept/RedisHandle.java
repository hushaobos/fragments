package com.hjc.redis.aspect.intercept;

import redis.clients.jedis.Jedis;

/**
 * @author 胡绍波
 * <p>redis处理类操作</p>
 * <ul>
 *     <li>handle()处理方法</li>
 *     <li>get()处理方法</li>
 *     <li>set()处理方法</li>
 *     <li>del()处理方法</li>
 * </ul>
 * @param <T>
 */
public interface RedisHandle<T> {

    /**
     * <p>handle()处理方法 , 内部采用CacheInfo的read字段做操作判别</p>
     * <p>操作枚举有:</p>
     * <ul>
     *     <li>READ</li>
     *     <li>WRITE</li>
     *     <li>DELTE</li>
     * </ul>
     * @param cacheInfo
     * @param jedis
     * @return
     */
    T handle(CacheInfo cacheInfo, Jedis jedis);

    /**
     * 获取缓存
     * @param cacheInfo
     * @param jedis
     * @return
     */
    T get(CacheInfo cacheInfo, Jedis jedis);

    /**
     * 设置缓存
     * @param cacheInfo
     * @param jedis
     * @return
     */
    T set(CacheInfo cacheInfo, Jedis jedis);

    /**
     * 删除缓存
     * @param cacheInfo
     * @param jedis
     * @return
     */
    T del(CacheInfo cacheInfo, Jedis jedis);
}
