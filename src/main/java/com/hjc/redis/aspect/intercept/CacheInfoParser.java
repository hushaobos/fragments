package com.hjc.redis.aspect.intercept;

import com.alibaba.fastjson.JSONObject;
import com.hjc.redis.aspect.annotation.HjcCacheType;
import com.hjc.redis.aspect.local.LocalCache;
import com.hjc.redis.aspect.util.RedisConstant;
import com.hjc.redis.config.ObjectFieldParserConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.hjc.redis.aspect.intercept.ExecuteQueue.consistentHash;

@Component
public class CacheInfoParser<T> {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    @Qualifier("hashRedisHandle")
    private RedisHandle<Object> hashRedisHandle;

    @Autowired
    @Qualifier("listRedisHandle")
    private RedisHandle<List> listRedisHandle;

    @Autowired
    @Qualifier("setRedisHandle")
    private RedisHandle<List> setRedisHandle;

    @Autowired
    @Qualifier("zsetRedisHandle")
    private RedisHandle<List> zSetRedisHandle;

    @Autowired
    @Qualifier("stringRedisHandle")
    private RedisHandle<String> stringRedisHandle;

    @Autowired
    private RedisTaskHandler redisTaskHandler;

    @Value(value = "${hjc.redis.local-time}")
    private long cacheTime = 1000;//本地缓存时间

    protected CacheExecute createExecute(String key,long cacheTime,HjcCacheType hjcCacheType,RedisConstant.RedisOptation redisOptation,Class<?> returnType,Object resultObj,String queueTag){
        CacheInfo cacheInfo = new CacheInfo.Builder()
                .setKey(key)
                .setTime(cacheTime)
                .setRead(redisOptation)
                .setCacheFields(ObjectFieldParserConfig.fieldMap.get(returnType.getSimpleName()))
                .setResultObj(resultObj)
                .build();
        return parser(cacheInfo,hjcCacheType,queueTag);
    }

    protected CacheExecute createExecute(CacheInfo sourceCacheInfo,HjcCacheType hjcCacheType,RedisConstant.RedisOptation redisOptation,Object resultObj,String queueTag){
        CacheInfo cacheInfo = new CacheInfo.Builder()
                .setKey(sourceCacheInfo.getKey())
                .setTime(sourceCacheInfo.getTime())
                .setRead(redisOptation)
                .setCacheFields(sourceCacheInfo.getCacheFields())
                .setResultObj(resultObj)
                .build();
        return parser(cacheInfo,hjcCacheType,queueTag);
    }

    protected CacheExecute<T> parser(CacheInfo cacheInfo,HjcCacheType cacheType,String queueTag)
    {
        CacheExecute cacheExecute;
        int queueIndex;
        if(queueTag.isEmpty()){
            queueIndex = consistentHash.get(cacheInfo.getKey());
        }
        else{
            queueIndex = consistentHash.get(queueTag);
        }
        switch (cacheType)
        {
            case HASH:
                cacheExecute = new CacheExecute(cacheInfo,queueIndex,hashRedisHandle,redisTaskHandler);
                break;
            case LIST:
                cacheExecute = new CacheExecute(cacheInfo,queueIndex,listRedisHandle,redisTaskHandler);
                break;
            case SET:
                cacheExecute = new CacheExecute(cacheInfo,queueIndex,setRedisHandle,redisTaskHandler);
                break;
            case ZSET:
                cacheExecute = new CacheExecute(cacheInfo,queueIndex,zSetRedisHandle,redisTaskHandler);
                break;
            case STRING:
            default:
                cacheExecute = new CacheExecute(cacheInfo,queueIndex,stringRedisHandle,redisTaskHandler);
                break;
        }
        return cacheExecute;
    }
}
