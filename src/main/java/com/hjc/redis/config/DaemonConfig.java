package com.hjc.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.hjc.redis.aspect.daemon.QueueTaskDaemonThread;

public class DaemonConfig {
    @Value(value = "${hjc.redis.task-queue-check-time}")
    private long taskQueueCheckTime = 5 * 60 * 1000;//redis操作处理队列线程 检查 时间默认5分钟检查一次

    @Bean
    public ScheduledFuture<?> queueTaskDaemon(){
        ScheduledExecutorService daemonSchedule = new ScheduledThreadPoolExecutor(1);
        return daemonSchedule.scheduleWithFixedDelay(new QueueTaskDaemonThread(), taskQueueCheckTime,taskQueueCheckTime, TimeUnit.MILLISECONDS);//创建keyRemoveScheduled守护进程
    }
}
