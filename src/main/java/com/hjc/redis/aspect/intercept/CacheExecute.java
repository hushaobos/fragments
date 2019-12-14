package com.hjc.redis.aspect.intercept;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author 胡绍波
 * 缓存任务处理线程
 * @param <T> 返回类型
 */
public class CacheExecute<T> implements Callable<T> {
    Logger logger = LoggerFactory.getLogger(getClass());
    private CacheInfo cacheInfo;//要操作的缓存信息
    private int queueIndex;//队列索引
    private Future<T> cacheFuture;//执行的线程状态类
    private RedisHandle<T> redisHandle;//缓存处理类
    private RedisTaskHandler<T> redisTaskHandler;//缓存任务处理类

    /**
     * 初始化
     * @param cacheInfo 要操作的缓存信息
     * @param redisHandle 缓存处理类
     * @param redisTaskHandler 缓存任务处理类
     */
    public CacheExecute(CacheInfo cacheInfo,int queueIndex, RedisHandle<T> redisHandle, RedisTaskHandler<T> redisTaskHandler) {
        this.cacheInfo = cacheInfo;
        this.redisHandle = redisHandle;
        this.redisTaskHandler = redisTaskHandler;
        setQueueIndex(queueIndex);
    }


    public void setQueueIndex(int queueIndex) {
        cacheFuture = null;
        this.queueIndex = queueIndex;
        boolean offerToQueue = ExecuteQueue.redisQueue[queueIndex].offer(this);
        logger.debug("添加到redis操作队列 {}",offerToQueue);
    }

    /**
     * 调用redisTaskHandler的taskHandle方法处理线程
     * @return
     * @throws Exception
     */
    @Override
    public T call() throws Exception {
        T t = redisTaskHandler.taskHandle(cacheInfo,redisHandle);
        ExecuteQueue.redisQueue[queueIndex].remove(this);
        ExecuteQueue.queueMap.remove(cacheInfo.getKey());
        return t;
    }

    public Future<T> getCacheFuture() {
        return cacheFuture;
    }

    protected void setCacheFuture(Future<T> cacheFuture) {
        this.cacheFuture = cacheFuture;
    }

    protected CacheInfo getCacheInfo() {
        return cacheInfo;
    }
}
