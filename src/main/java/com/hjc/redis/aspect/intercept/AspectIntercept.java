package com.hjc.redis.aspect.intercept;

import com.alibaba.fastjson.JSONObject;
import com.hjc.redis.aspect.annotation.HjcCacheEvict;
import com.hjc.redis.aspect.annotation.HjcCachePut;
import com.hjc.redis.aspect.annotation.HjcCacheType;
import com.hjc.redis.aspect.annotation.HjcCacheable;
import com.hjc.redis.aspect.local.LocalCache;
import com.hjc.redis.aspect.util.RedisConstant;
import com.hjc.redis.config.ObjectFieldParserConfig;
import com.hjc.utils.AspectMethod;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 胡绍波
 */
@Aspect
@Component
public class AspectIntercept {
    Logger logger = LoggerFactory.getLogger(getClass());

    protected static ThreadLocal<AtomicInteger> retryTime = new ThreadLocal<AtomicInteger>();

    @Autowired
    private CacheInfoParser cacheInfoParser;
    
    @Autowired
    private CacheRead cacheRead;

    @Pointcut("@annotation(com.hjc.redis.aspect.annotation.HjcCacheable)")
    public void hjcCacheablePoint(){
    }

    @Around("hjcCacheablePoint()")
    public Object HjcCacheableBefore(ProceedingJoinPoint pjp) throws Throwable {
        Method currentMethod = AspectMethod.getMethod(pjp);
        HjcCacheable cacheable = currentMethod.getAnnotation(HjcCacheable.class);
        long cacheTime = cacheable.cacheTime();//获取缓存时间
        Boolean condition = SpelParser.parseSpel(currentMethod,pjp.getArgs(),cacheable.condition(),Boolean.class,true);//对condition做el表达式解析 , condition 为true则做缓存操作
        Boolean unless = SpelParser.parseSpel(currentMethod,pjp.getArgs(),cacheable.unless(),Boolean.class,true);//对unless做el表达式解析, unless 为true则做缓存操作

        List<CacheExecute> cacheExecuteList = null;
        Class returnType = null;
        HjcCacheType hjcCacheType = null;
        String queueTag = cacheable.queueTag();
        if(condition || unless){
            AspectIntercept.retryTime.set(new AtomicInteger(0));//设置重试次数

            String parserKey = SpelParser.parseSpel(currentMethod,pjp.getArgs(),cacheable.key(),String.class,"");//对key做el表达式解析
            if(Objects.nonNull(parserKey) && !parserKey.isEmpty())
            {
                cacheExecuteList = new LinkedList<CacheExecute>();
                returnType = AspectMethod.getMethodReturnType(currentMethod);//获取方法返回类

                String[] cacheNames = cacheable.value();
                hjcCacheType = cacheable.cacheType();//获取缓存类型 , 类型分别有 hash , string , list ,set ,zset
                queueTag = SpelParser.parseSpel(currentMethod,pjp.getArgs(),queueTag,String.class,"");//对队列标识做el表达式解析  获取缓存队列标识

                for(String name : cacheNames)
                {
                    String key = name.concat("::").concat(parserKey);
                    Object localResult = LocalCache.getCache(key);//查询本地缓存是否存在key相关的缓存值
                    if(Objects.nonNull(localResult)){ //本地缓存值如果不为空则返回缓存值
                        logger.info("get value from lcoal");
                        return localResult;
                    }
                    else{
                        CacheExecute execute = null;
                        if(cacheable.sync()){//如果需要同步则从同步集合中获取CacheExecute , 从而达到复用效果
                            execute = ExecuteQueue.queueMap.get(key);
                        }

                        if(Objects.isNull(execute)){//如果从同步集合中没有找到 CacheExecute 则创建一个CacheExecute
                            execute = cacheInfoParser.createExecute(key,cacheTime,hjcCacheType,RedisConstant.RedisOptation.READ,returnType,null,queueTag);//新建CacheExecute

                            if(cacheable.sync()){//如果同步则还需要写入queueMap中 , 给其他线程复用查询的 cacheExecute
                                ExecuteQueue.queueMap.put(key,execute);
                            }
                        }
                        cacheExecuteList.add(execute);
                    }
                }

                Object object = cacheRead.getCache(cacheExecuteList,currentMethod.getReturnType(),returnType);//读取缓存读取线程的值
                if(Objects.nonNull(object)){//如果可以读取到则返回从redis读取的值
                    logger.info("get value from redis");
                    return object;
                }
            }
        }

        Object resultObject = pjp.proceed();//执行方法

        if(Objects.nonNull(cacheExecuteList)){//如果读的cacheExecuteList不为空则创建写的cacheExecute
            for(CacheExecute cacheExecute : cacheExecuteList)
            {
                cacheInfoParser.createExecute(cacheExecute.getCacheInfo(),hjcCacheType,RedisConstant.RedisOptation.WRITE,resultObject,queueTag);//新建 写的CacheExecute
            }
        }
        return resultObject;
    }


    @Pointcut("@annotation(com.hjc.redis.aspect.annotation.HjcCachePut)")
    public void hjcCachePutPoint(){
    }

    @Around("hjcCachePutPoint()")
    public Object HjcCachePutBefore(ProceedingJoinPoint pjp) throws Throwable {
        Object resultObject = pjp.proceed();
        logger.error("put {}",JSONObject.toJSONString(resultObject));

        Method currentMethod = AspectMethod.getMethod(pjp);
        HjcCachePut cachePut = currentMethod.getAnnotation(HjcCachePut.class);
        Boolean condition = SpelParser.parseSpel(currentMethod,pjp.getArgs(),cachePut.condition(),Boolean.class,null);//对condition做el表达式解析 , condition 为true则做缓存操作
        if(Objects.isNull(condition)){
            condition = SpelParser.parseSpel(resultObject,cachePut.condition(),Boolean.class,true);//对condition做返回值的el表达式解析
        }

        Boolean unless = SpelParser.parseSpel(currentMethod,pjp.getArgs(),cachePut.unless(),Boolean.class,null);//对unless做el表达式解析, unless 为true则做缓存操作
        if(Objects.isNull(unless)){
            unless = SpelParser.parseSpel(resultObject,cachePut.unless(),Boolean.class,true);//对key做el表达式解析
        }

        if(condition || unless){
            String parserKey = SpelParser.parseSpel(currentMethod,pjp.getArgs(),cachePut.key(),String.class,null);//对key做el表达式解析
            if(Objects.isNull(parserKey)){
                parserKey = SpelParser.parseSpel(resultObject,cachePut.key(),String.class,"");//对key做el表达式解析
            }

            if(Objects.nonNull(parserKey) && !parserKey.isEmpty())
            {
                logger.info("set value to redis by put key:{}",parserKey);

                String queueTag = SpelParser.parseSpel(currentMethod,pjp.getArgs(),cachePut.queueTag(),String.class,"");//对队列标识做el表达式解析  获取缓存队列标识
                long cacheTime = cachePut.cacheTime();//获取缓存时间
                String[] cacheNames = cachePut.value();
                HjcCacheType hjcCacheType = cachePut.cacheType();//获取缓存类型 , 类型分别有 hash , string , list ,set ,zset

                Class returnType = AspectMethod.getMethodReturnType(currentMethod);//获取方法返回类
                for(String name : cacheNames)
                {
                    String key = name.concat("::").concat(parserKey);
                    cacheInfoParser.createExecute(key,cacheTime,hjcCacheType,RedisConstant.RedisOptation.WRITE,returnType,resultObject,queueTag);//新建 写的CacheExecute
                }
            }
        }
        return resultObject;
    }

    @Pointcut("@annotation(com.hjc.redis.aspect.annotation.HjcCacheEvict)")
    public void hjcCacheEvict(){
    }

    @Around("hjcCacheEvict()")
    public Object HjcCacheEvictBefore(ProceedingJoinPoint pjp) throws Throwable {
        Method currentMethod = AspectMethod.getMethod(pjp);
        HjcCacheEvict cacheEvict = currentMethod.getAnnotation(HjcCacheEvict.class);

        Boolean condition = SpelParser.parseSpel(currentMethod,pjp.getArgs(),cacheEvict.condition(),Boolean.class,true);//对condition做el表达式解析 , condition 为true则做缓存操作

        String parserKey = "";
        String queueTag = cacheEvict.queueTag();
        String[] cacheNames = null;
        HjcCacheType hjcCacheType = null;
        Class returnType = null;
        List<CacheExecute> cacheExecuteList = null;
        if(condition){
            parserKey = SpelParser.parseSpel(currentMethod,pjp.getArgs(),cacheEvict.key(),String.class,parserKey);//对key做el表达式解析
            if(Objects.nonNull(parserKey) && !parserKey.isEmpty())
            {
                logger.info("del value from redis by evict first key:{}",parserKey);
                cacheNames = cacheEvict.value();
                hjcCacheType = cacheEvict.cacheType();//获取缓存类型 , 类型分别有 hash , string , list ,set ,zset
                queueTag = SpelParser.parseSpel(currentMethod,pjp.getArgs(),queueTag,String.class,"");//对队列标识做el表达式解析  获取缓存队列标识

                returnType = AspectMethod.getMethodReturnType(currentMethod);//获取方法返回类
                cacheExecuteList = new LinkedList<CacheExecute>();
                for(String name : cacheNames)
                {
                    String key = name.concat("::").concat(parserKey);
                    cacheExecuteList.add(cacheInfoParser.createExecute(key,0,hjcCacheType, RedisConstant.RedisOptation.DELTE,returnType,null,queueTag));
                }
            }
        }

        Object resultObject = pjp.proceed();

        if(Objects.nonNull(cacheExecuteList))//二次删除
        {
            for(CacheExecute execute: cacheExecuteList)
            {
                logger.info("del value from redis by evict second key:{}",parserKey);
                cacheInfoParser.createExecute(execute.getCacheInfo(),hjcCacheType, RedisConstant.RedisOptation.DELTE,null,queueTag);//创建删除执行线程类CacheExecute
            }
        }
        return resultObject;
    }
}
