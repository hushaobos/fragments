package com.hjc.redis.aspect.intercept;

import com.hjc.utils.hash.ConsistentHash;
import com.hjc.utils.hash.HashFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author 胡绍波
 * 本类为redis缓存操作执行队列类
 */
public class ExecuteQueue {

    protected static Map<String,CacheExecute> queueMap = new ConcurrentHashMap<String, CacheExecute>();//redis操作信息集合;
    protected static BlockingQueue<CacheExecute>[] redisQueue;//根据系统核心数创建队列个数
    public static Thread taskThread;
    private static boolean TaskThreadStart = true;

    public static ConsistentHash<Integer> consistentHash;

    static {
        int availProcessors = Runtime.getRuntime().availableProcessors();
        Set<Integer> nodes = new HashSet<Integer>();
        redisQueue = new LinkedBlockingQueue[availProcessors];
        for (int i = 0;i < availProcessors;i++)
        {
            redisQueue[i] = new LinkedBlockingQueue<CacheExecute>(10000);
            nodes.add(i);
        }
        consistentHash = new ConsistentHash<Integer>(new HashFunction(), 160, nodes);
    }

    public static class HjcRedisTaskThread implements Runnable{
        private ThreadPoolExecutor hjcRedisExecutePool;//redis执行线程池

        public HjcRedisTaskThread(ThreadPoolExecutor hjcRedisExecutePool) {
            this.hjcRedisExecutePool = hjcRedisExecutePool;
        }

        @Override
        public void run() {
            while (TaskThreadStart){
                for (BlockingQueue<CacheExecute> executeList : redisQueue)
                {
                    if(!executeList.isEmpty())
                    {
                        CacheExecute execute = executeList.peek();//获取redis队列头
                        if(Objects.nonNull(execute) && Objects.isNull(execute.getCacheFuture()))
                        {
                            execute.setCacheFuture(hjcRedisExecutePool.submit(execute));
                        }
                    }
                }
            }
        }
    }

    public boolean isTaskThreadStart() {
        return TaskThreadStart;
    }

    public void setTaskThreadStart(boolean taskThreadStart) {
        TaskThreadStart = taskThreadStart;
    }
}
