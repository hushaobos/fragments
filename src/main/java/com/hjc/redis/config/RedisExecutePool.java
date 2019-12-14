package com.hjc.redis.config;

import com.hjc.redis.aspect.intercept.ExecuteQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author 胡绍波
 */
@Configuration
@EnableAutoConfiguration
public class RedisExecutePool {
    Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * redis缓存操作线程池初始化
     * @return
     */
    @Bean
    public ThreadPoolExecutor hjcRedisExecutePool(){
        int availProcessors = Runtime.getRuntime().availableProcessors();//取得系统核心数
        logger.warn("核心数 {}",availProcessors);
        int queueSize = availProcessors * 2 + 1;//等待队列数
        int coreSize = availProcessors / 2;//核心线程为核数的一半
        coreSize = coreSize < 2 ? 2 : coreSize; //如果核心线程小与2 , 则设置核心线程为2
        long keepAliveTime = 5 * 60 * 1000L;//保持空闲线程时间为 5 分钟
        BlockingQueue<Runnable> redisQueue = new ArrayBlockingQueue<Runnable>(queueSize);//数组有界队列
        RejectedExecutionHandler redisHanlder = new ThreadPoolExecutor.AbortPolicy();//多任务出则抛出异常

        ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize,availProcessors,keepAliveTime, TimeUnit.MILLISECONDS,redisQueue,redisHanlder);
        taskThread(executor);
        return executor;
    }

    public static void taskThread(ThreadPoolExecutor hjcRedisExecutePool){
        ExecuteQueue.taskThread = new Thread(new ExecuteQueue.HjcRedisTaskThread(hjcRedisExecutePool));
        ExecuteQueue.taskThread.start();
    }
}
