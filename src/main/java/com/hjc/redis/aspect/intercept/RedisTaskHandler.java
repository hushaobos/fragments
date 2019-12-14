package com.hjc.redis.aspect.intercept;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 任务处理类
 * @author 胡绍波
 * @param <T>返回类型
 */
@Component
public class RedisTaskHandler<T> {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JedisPool hjcCache;//redis线程池

    /**
     * 获取Jedis对象
     *
     * @return
     */
    public synchronized Jedis getJedis() {
        Jedis jedis = hjcCache.getResource();
        return jedis;
    }

    /**
     * 回收Jedis对象资源
     *
     * @param jedis
     */
    public synchronized void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * Jedis对象出异常的时候，回收Jedis对象资源
     *
     * @param jedis
     */
    public synchronized void returnBrokenResource(Jedis jedis) {
        if (jedis != null) {
        }

    }

    public T taskHandle(CacheInfo cacheInfo,RedisHandle<T> redisHandle)
    {
        Jedis jedis = getJedis();
        try {
            return redisHandle.handle(cacheInfo,jedis);
        }
        catch (Exception e){
            returnBrokenResource(jedis);
            logger.error(e.getMessage(), e);
            return null;
        }
        finally {
            returnResource(jedis);
        }
    }
}
