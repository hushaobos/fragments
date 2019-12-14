package com.hjc.redis.aspect.intercept;

import com.hjc.redis.aspect.local.LocalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;

import java.util.Objects;
import java.util.Random;

/**
 * @author 胡绍波
 * RedisHandle的抽象方法 , 统一实现handle方法
 * @param <T>
 */
public abstract class AbstractRedisHandle<T> implements RedisHandle<T>{
    Logger logger = LoggerFactory.getLogger(getClass());

    @Value(value = "${hjc.redis.local-time}")
    private long cacheTime = 1000;//本地缓存时间

    private Random random = new Random();
    private static final int randomFixedTime = 300000;

    /**
     *统一实现handle方法
     * @param cacheInfo 缓存信息类
     * @param jedis 缓存连接客户端
     * @return
     */
    @Override
    public T handle(CacheInfo cacheInfo, Jedis jedis) {
        switch (cacheInfo.getRead()){
            case READ://读
                return get(cacheInfo,jedis);
            case WRITE://写
                logger.debug("write hjcRedis");
                return set(cacheInfo,jedis);
            case DELTE://删除
                LocalCache.delCache(cacheInfo.getKey());
                return del(cacheInfo,jedis);
            default://默认为读
                return get(cacheInfo,jedis);
        }
    }

    protected void setLocal(String key,Object obj) {
        if(Objects.nonNull(obj)){
            LocalCache.setCache(key,obj,cacheTime);
        }
    }

    public long randomTime(){
        return random.nextInt(randomFixedTime);
    }
}
