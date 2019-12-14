package com.hjc.redis.aspect.intercept;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Component(value = "hashRedisHandle")
@Order(1)
public class HashRedisHandle extends AbstractRedisHandle<Object>{
    Logger logger = LoggerFactory.getLogger(getClass());
    private static final long delDefail = -1L;

    @Value(value = "${hjc.redis.jedis.value-len}")
    private int valueLen = 10;//每次取值的个数 , 默认为10;

    @Override
    public JSONObject get(CacheInfo cacheInfo, Jedis jedis) {
        JSONObject obj = new JSONObject();
        String value;
        for (CacheInfo.CacheField cacheField : cacheInfo.getCacheFields())
        {
            String fieldName = cacheField.getField().getName();
            value = jedis.hget(cacheInfo.getKey(),fieldName);
            if (Objects.nonNull(value)) {
                obj.put(fieldName,value);
            }
        }
        logger.debug("get hashValue by key {}",cacheInfo.getKey());
        if(obj.isEmpty()){
            return null;
        }
        return obj;
    }

    @Override
    public String set(CacheInfo cacheInfo, Jedis jedis) {
        int valueSize = cacheInfo.getCacheFields().size();
        Map<String,String> valueMap = new HashMap<String, String>();
        int len;
        int valueIndex = valueSize - 1;//values数组最大索引值
        String result = "";
        logger.debug("设置值 hash valueSize {}",valueSize);
        for(int i = 0;i < valueSize;)
        {
            len = i + valueLen;
            len = len >= valueSize ? valueIndex : len;
            logger.debug("设置值 i {} len {}",i,len);
            for (;i <= len;i++)
            {
                CacheInfo.CacheField cacheField = cacheInfo.getCacheFields().get(i);
                if(Objects.nonNull(cacheField.getValue())){
                    valueMap.put(cacheField.getField().getName(), String.valueOf(cacheField.getValue()));
                }
            }
            logger.debug("设置值 {} {}",cacheInfo.getKey(),JSONObject.toJSONString(valueMap));
            result = jedis.hmset(cacheInfo.getKey(),valueMap);
        }
        jedis.pexpire(cacheInfo.getKey(),cacheInfo.getTime() + randomTime());
        return result;
    }

    @Override
    public Long del(CacheInfo cacheInfo, Jedis jedis) {
        try {
            int valueSize = cacheInfo.getCacheFields().size();
            String[] fields;
            int len;
            int valueIndex = valueSize - 1;//values数组最大索引值
            long delNum = 0;
            for(int i = 0;i < valueSize;)
            {
                len = i + valueLen;
                len = len >= valueSize ? valueIndex : len;
                fields = new String[len];
                for (int r = 0;i < len;++r,++i)
                {
                    CacheInfo.CacheField cacheField = cacheInfo.getCacheFields().get(i);
                    fields[r] = cacheField.getField().getName();
                }
                delNum += jedis.hdel(cacheInfo.getKey(),fields);
            }
            return delNum;
        }catch (Exception e){
            return delDefail;
        }
    }
}
