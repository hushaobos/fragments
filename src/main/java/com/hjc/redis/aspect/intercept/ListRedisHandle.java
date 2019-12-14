package com.hjc.redis.aspect.intercept;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * @author 胡绍波
 * redis的list操作类
 */
@Component(value = "listRedisHandle")
@Order(2)
public class ListRedisHandle extends AbstractRedisHandle<List>{
    @Value(value = "${hjc.redis.jedis.value-len}")
    private int valueLen = 10;//每次取值的个数 , 默认为10;

    /**
     * 通过先获取队列的长度 , 通过lrange命令循环获取一定范围的值 , 再添加到resultList里返回
     * @param cacheInfo 缓存消息类
     * @param jedis redis集合
     * @return
     */
    @Override
    public List get(CacheInfo cacheInfo, Jedis jedis) {
        List<String> resultList = new LinkedList<String>();
        long llen = jedis.llen(cacheInfo.getKey());//获取队列长度用于索引获取值
        long end = llen - 1;//结尾索引值
        long start = 0;
        long stop = valueLen - 1;
        Set<String> valueList = new HashSet<String>();
        while (start < llen)
        {
            stop = stop >= llen ? end : stop;
            valueList.addAll(jedis.lrange(cacheInfo.getKey(),start,stop));//通过lrange命令获取一段范围的值

            start = stop + 1;
            stop = stop + valueLen;
        }
        resultList.addAll(valueList);
        return resultList;
    }

    /**
     * 通过rpush循环设置redis的list
     * @param cacheInfo 缓存消息类
     * @param jedis redis集合
     * @return
     */
    @Override
    public List set(CacheInfo cacheInfo, Jedis jedis) {
        logger.error("写 list {}", JSONObject.toJSONString(cacheInfo));
        long setNum = 0;
        for (String value : cacheInfo.getValues())
        {
            setNum = jedis.rpush(cacheInfo.getKey(),value);//循环把value值push到list中
        }
        jedis.pexpire(cacheInfo.getKey(),cacheInfo.getTime() + randomTime());//设置有效时间

        List<Long> resultList = new ArrayList<Long>();//result结果集
        resultList.add(setNum);
        return resultList;
    }

    /**
     * 删除key值元素
     * @param cacheInfo
     * @param jedis
     * @return
     */
    @Override
    public List del(CacheInfo cacheInfo, Jedis jedis) {
        List<String> resultList = new LinkedList<String>();
        String value;
        while (Objects.nonNull(value = jedis.lpop(cacheInfo.getKey()))){
            resultList.add(value);
        }
        return resultList;
    }
}
