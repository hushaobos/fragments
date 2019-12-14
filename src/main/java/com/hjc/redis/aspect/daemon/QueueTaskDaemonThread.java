package com.hjc.redis.aspect.daemon;

import com.hjc.redis.aspect.intercept.ExecuteQueue;


public class QueueTaskDaemonThread implements Runnable{
    @Override
    public void run() {
        if(ExecuteQueue.taskThread.isInterrupted()){
            ExecuteQueue.taskThread.start();
        }
    }
}
