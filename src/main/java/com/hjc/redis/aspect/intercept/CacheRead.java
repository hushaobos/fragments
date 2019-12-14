package com.hjc.redis.aspect.intercept;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjc.redis.aspect.local.LocalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class CacheRead<T> {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Value(value = "${hjc.redis.local-time}")
    private long cacheTime = 1000;//本地缓存时间

    public T getCache(List<CacheExecute> cacheExecuteList,Class<T> returnClazz,Class parseClass){
        boolean done = true;
        try {
            AspectIntercept.retryTime.get().getAndIncrement();

            TimeUnit.MILLISECONDS.sleep(50);
            for (CacheExecute cacheExecute:cacheExecuteList){
                if(!cacheExecute.getCacheFuture().isDone()){
                    done = false;
                    break;
                }
            }
        }
        catch (NullPointerException e){
            done = false;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!done && AspectIntercept.retryTime.get().get() <= 9){
            return getCache(cacheExecuteList,returnClazz,parseClass);
        }
        return objectParser(cacheExecuteList,returnClazz,parseClass);
    }

    public T objectParser(List<CacheExecute> cacheExecuteList, Class<T> returnClazz,Class parseClass) {
        for (CacheExecute cacheExecute : cacheExecuteList){
            try {
                if(Objects.nonNull(cacheExecute.getCacheFuture())){
                    Object result = cacheExecute.getCacheFuture().get();
                    if(Objects.nonNull(result)){
                        T valueObj;
                        if(result instanceof List){
                            valueObj = (T) JSONObject.parseArray(String.valueOf(result),parseClass);
                        }
                        else{
                            valueObj = JSONObject.parseObject(String.valueOf(result),returnClazz);
                        }
                        setLocal(cacheExecute.getCacheInfo().getKey(),valueObj);
                        return valueObj;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected void setLocal(String key,T obj) {
        if(Objects.nonNull(obj)){
            LocalCache.setCache(key,obj,cacheTime);
        }
    }
}
