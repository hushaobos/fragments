package com.hjc.redis.aspect.local;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class LocalCacheDaemonThread implements Runnable{
    Logger logger = LoggerFactory.getLogger(LocalCache.class);
    private long cacheTime;
    public LocalCacheDaemonThread(long cacheTime){
        this.cacheTime = cacheTime;
    }
    @Override
    public void run() {
        if(Objects.nonNull(LocalCache.keyRemoveScheduled) && (LocalCache.keyRemoveScheduled.isCancelled() || LocalCache.keyRemoveScheduled.isDone())){
            logger.error("本地缓存 key移除线程崩溃 正在重新创建");
            LocalCache.keyRemoveScheduled = null;
            LocalCache.removeKeyThreadInit(cacheTime);
        }
    }
}
