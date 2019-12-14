package com.hjc.redis.aspect.intercept;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;

import java.util.*;

@Component(value = "zsetRedisHandle")
@Order(4)
public class ZsetRedisHandle extends AbstractRedisHandle<List>{
    @Value(value = "${hjc.redis.jedis.value-len}")
    private int valueLen = 10;//每次取值的个数 , 默认为10;

    @Override
    public List get(CacheInfo cacheInfo, Jedis jedis) {
        ScanResult result;
        String cursor = "0";
        List resultList = new LinkedList();
        ScanParams countParam = new ScanParams();
        countParam.count(valueLen);
        do {
            result = jedis.zscan(cacheInfo.getKey(),cursor,countParam);
            cursor = result.getCursor();
            resultList.addAll(result.getResult());
        }while (!"0".equals(cursor));
        return result.getResult();
    }

    @Override
    public List set(CacheInfo cacheInfo, Jedis jedis) {
        int valueSize = cacheInfo.getZsets().size();
        logger.error("写 zset {}", JSONObject.toJSONString(cacheInfo));
        Map<String,Double> values;
        int len;
        int valueIndex = valueSize - 1;//values数组最大索引值
        List<Long> resultSet = new ArrayList<Long>();
        long addNum = 0;
        for(int i = 0;i < valueSize;)
        {
            values = new HashMap<String,Double>();
            len = i + valueLen;
            len = len >= valueSize ? valueIndex : len;
            for (int r = 0;i < len;++r,++i)
            {
                Tuple tuple = cacheInfo.getZsets().get(i);
                values.put(tuple.getElement(),tuple.getScore());
            }
            addNum += jedis.zadd(cacheInfo.getKey(),values);
        }
        jedis.pexpire(cacheInfo.getKey(),cacheInfo.getTime() + randomTime());
        resultSet.add(addNum);
        return resultSet;
    }

    @Override
    public List del(CacheInfo cacheInfo, Jedis jedis) {
        ScanResult<Tuple> result;
        String cursor = "0";
        List<Long> resultList = new ArrayList<Long>();
        long addNum = 0;
        ScanParams countParam = new ScanParams();
        countParam.count(valueLen);
        String[] values;
        int len;
        do {
            result = jedis.zscan(cacheInfo.getKey(),cursor,countParam);
            cursor = result.getCursor();
            len = result.getResult().size();
            values = new String[len];
            for (int r = 0;r < len;r++)
            {
                values[r] = result.getResult().get(r).getElement();
            }
            addNum += jedis.zrem(cacheInfo.getKey(),values);
        }while (!"0".equals(cursor));
        resultList.add(addNum);
        return resultList;
    }
}
