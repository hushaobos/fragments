package com.hjc.redis.aspect.intercept;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.*;

/**
 * @author 胡绍波
 */
@Component(value = "setRedisHandle")
@Order(3)
public class SetRedisHandle extends AbstractRedisHandle<List>{
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
            result = jedis.sscan(cacheInfo.getKey(),cursor,countParam);
            cursor = result.getCursor();
            resultList.addAll(result.getResult());
        }while (!"0".equals(cursor));
        return result.getResult();
    }

    @Override
    public List set(CacheInfo cacheInfo, Jedis jedis) {
        logger.error("写 set {}", JSONObject.toJSONString(cacheInfo));
        int valueSize = cacheInfo.getValues().length;
        String[] values;
        int len;
        int valueIndex = valueSize - 1;//values数组最大索引值
        List<Long> resultSet = new ArrayList<Long>();
        long addNum = 0;
        for(int i = 0;i < valueSize;)
        {
            values = new String[valueLen];
            len = i + valueLen;
            len = len >= valueSize ? valueIndex : len;
            for (int r = 0;i < len;++r,++i)
            {
                values[r] = cacheInfo.getValues()[i];
            }
            addNum += jedis.sadd(cacheInfo.getKey(),values);
        }
        jedis.pexpire(cacheInfo.getKey(),cacheInfo.getTime() + randomTime());
        resultSet.add(addNum);
        return resultSet;
    }

    @Override
    public List del(CacheInfo cacheInfo, Jedis jedis) {
        ScanResult<String> result;
        String cursor = "0";
        List<Long> resultList = new ArrayList<Long>();
        long addNum = 0;
        ScanParams countParam = new ScanParams();
        countParam.count(valueLen);
        do {
            result = jedis.sscan(cacheInfo.getKey(),cursor,countParam);
            cursor = result.getCursor();
            addNum += jedis.srem(cacheInfo.getKey(),result.getResult().toArray(new String[result.getResult().size()]));
        }while (!"0".equals(cursor));
        resultList.add(addNum);
        return resultList;
    }
}
