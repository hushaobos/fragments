package com.hjc.redis.aspect.local;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

/**
 * @className：CacheHandler
 * @description：缓存操作类，对缓存进行管理,清除方式采用Timer定时的方式
 * @creater: 胡绍波
 * @creatTime：2014年5月7日 上午9:18:54 @remark：
 * @version
 */
@SuppressWarnings("unchecked")
public class LocalCache {
    static Logger logger = LoggerFactory.getLogger(LocalCache.class);
    private static final ScheduledExecutorService timeSchedule;
    protected static final ConcurrentHashMap<String, Object> map;
    protected static BlockingQueue<CacheWrapper> cacheQueue;
    protected static ScheduledFuture keyRemoveScheduled;

    static {
        int availProcessors = Runtime.getRuntime().availableProcessors();//取得系统核心数
        timeSchedule = new ScheduledThreadPoolExecutor(availProcessors);
        map = new ConcurrentHashMap<String, Object>();
        cacheQueue = new LinkedBlockingQueue<CacheWrapper>(100000);
    }

    public static void initLocalCache(long cacheTime){
        removeKeyThreadInit(cacheTime);
        timeSchedule.scheduleAtFixedRate(new LocalCacheDaemonThread(cacheTime * 10), cacheTime,cacheTime, TimeUnit.MILLISECONDS);//创建keyRemoveScheduled守护进程
    }

    public static void removeKeyThreadInit(long cacheTime){
        keyRemoveScheduled = timeSchedule.scheduleAtFixedRate(new TimeoutTimerTask(), cacheTime,cacheTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 增加缓存对象
     *
     * @param key
     * @param ce
     * @param validityTime
     *            有效时间
     */
    public static void setCache(String key, Object ce, long validityTime) {
        CacheWrapper cacheWrapper = new CacheWrapper(validityTime,key, ce);
        boolean setToQueue = cacheQueue.offer(cacheWrapper);
        if(!setToQueue){
            setCache(cacheWrapper);
        }
        else{
            map.put(key, cacheWrapper);
        }
    }

    public static void setCache(CacheWrapper cacheWrapper) {
        CacheWrapper oldCache = cacheQueue.poll();
        map.remove(oldCache.getKey());
        boolean setToQueue = cacheQueue.offer(cacheWrapper);
        if(setToQueue){
            map.put(cacheWrapper.getKey(), cacheWrapper);
        }
    }

    // 获取缓存KEY列表
    public static Set<String> getCacheKeys() {
        return map.keySet();
    }

    public static List<String> getKeysFuzz(String patton) {
        List<String> list = new ArrayList<String>();
        for (String tmpKey : map.keySet()) {
            if (tmpKey.contains(patton)) {
                list.add(tmpKey);
            }
        }
        if (isNullOrEmpty(list)) {
            return null;
        }
        return list;
    }

    public static Integer getKeySizeFuzz(String patton) {
        Integer num = 0;
        for (String tmpKey : map.keySet()) {
            if (tmpKey.startsWith(patton)) {
                num++;
            }
        }
        return num;
    }

    /**
     * 增加缓存对象
     *
     * @param key
     * @param ce
     *            有效时间
     */
    public static void setCache(String key, Object ce) {
        map.put(key, new CacheWrapper(ce));
    }

    /**
     * 获取缓存对象
     *
     * @param key
     * @return
     */
    public static <T> T getCache(String key) {
        CacheWrapper wrapper = (CacheWrapper) map.get(key);
        if (wrapper == null) {
            return null;
        }
        wrapper.addDateTime(10);//有新的访问则给数据添加10ms的有效时间
        return (T) wrapper.getValue();
    }

    /**
     * 检查是否含有制定key的缓冲
     *
     * @param key
     * @return
     */
    public static boolean contains(String key) {
        return map.containsKey(key);
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public static void delCache(String key) {
        map.remove(key);
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public static void delCacheFuzz(String key) {
        for (String tmpKey : map.keySet()) {
            if (tmpKey.contains(key)) {
                map.remove(tmpKey);
            }
        }
    }

    /**
     * 获取缓存大小
     *
     */
    public static int getCacheSize() {
        return map.size();
    }

    /**
     * 清除全部缓存
     */
    public static void clearCache() {
        map.clear();
    }

    /**
     * @projName：lottery
     * @className：TimeoutTimerTask
     * @description：清除超时缓存定时服务类
     * @creater：Coody
     * @creatTime：2014年5月7日上午9:34:39
     * @alter：Coody
     * @alterTime：2014年5月7日 上午9:34:39 @remark：
     * @version
     */
    static class TimeoutTimerTask extends TimerTask {

        @Override
        public void run() {
//            CacheWrapper cacheWrapper;
            Iterator<CacheWrapper> cacheWrapperIterator = cacheQueue.iterator();
            while (cacheWrapperIterator.hasNext()){
                CacheWrapper cacheWrapper = cacheWrapperIterator.next();
                if (cacheWrapper.getDateTime() == 0) {
                    continue;
                }
                else if (cacheWrapper.getDateTime() > System.currentTimeMillis()) {
                    continue;
                }
                LocalCache.delCache(cacheWrapper.key);
                cacheWrapperIterator.remove();
            }
//            while ((cacheWrapper = cacheQueue.peek()) != null){
//                if (cacheWrapper.getDateTime() == 0) {
//                    return;
//                }
//                else if (cacheWrapper.getDateTime() > System.currentTimeMillis()) {
//                    return;
//                }
//                LocalCache.delCache(cacheWrapper.key);
//                cacheQueue.poll();
//            }
        }
    }

    public static boolean isNullOrEmpty(Object obj) {
        try {
            if (obj == null){
                return true;
            }
            if (obj instanceof CharSequence) {
                return ((CharSequence) obj).length() == 0;
            }
            if (obj instanceof Collection) {
                return ((Collection<?>) obj).isEmpty();
            }
            if (obj instanceof Map) {
                return ((Map<?, ?>) obj).isEmpty();
            }
            if (obj instanceof Object[]) {
                Object[] object = (Object[]) obj;
                if (object.length == 0) {
                    return true;
                }
                boolean empty = true;
                for (int i = 0; i < object.length; i++) {
                    if (!isNullOrEmpty(object[i])) {
                        empty = false;
                        break;
                    }
                }
                return empty;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private static class CacheWrapper {
        private long dateTime;
        private String key;
        private Object value;

        public CacheWrapper(long time,String key, Object value) {
            if(time == 0){
                this.dateTime = time;
            }
            else{
                this.dateTime = System.currentTimeMillis() + time;
            }
            this.key = key;
            this.value = value;
        }

        public CacheWrapper(Object value) {
            this.value = value;
        }

        public long getDateTime() {
            return dateTime;
        }

        public void addDateTime(long incrementTime) {
            this.dateTime += incrementTime;
        }

        public Object getValue() {
            return value;
        }

        public String getKey() {
            return key;
        }
    }
}
