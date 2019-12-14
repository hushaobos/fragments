package com.hjc.redis.aspect.intercept;

import com.alibaba.fastjson.JSONObject;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component(value = "stringRedisHandle")
@Order(5)
public class StringRedisHandle extends AbstractRedisHandle<String>{
    private static final String delDefail = "-1";

    @Override
    public String get(CacheInfo cacheInfo, Jedis jedis)
    {
        return jedis.get(cacheInfo.getKey());
    }

    @Override
    public String set(CacheInfo cacheInfo,Jedis jedis)
    {
        setLocal(cacheInfo.getKey(),cacheInfo.getResultObj());//设置本地缓存

        String valueStr = JSONObject.toJSONString(cacheInfo.getResultObj());
        String result = jedis.psetex(cacheInfo.getKey(),cacheInfo.getTime() + randomTime(),valueStr);
        return result;
    }

    @Override
    public String del(CacheInfo cacheInfo, Jedis jedis)
    {
        try {
            return String.valueOf(jedis.del(cacheInfo.getKey()));
        }catch (Exception e){
            return delDefail;
        }
    }
}
