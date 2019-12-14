package com.hjc.redis.aspect.annotation;

import java.lang.annotation.*;

/**
 * @author 胡绍波
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface HjcCachePut {
    String[] value() default {""};

    /**
     * <p>缓存key值 , 默认为空与cacheNames结合可生成多个key-value值对</p>
     * <ul>
     *     <li>规范格式为name:key</li>
     * </ul>
     * @return
     */
    String key() default "";

    /**key值生成器
     *
     * @return
     */
    String keyGenerator() default "";

    /**
     * 缓存类型默认是字符类型
     * @return
     */
    HjcCacheType cacheType() default HjcCacheType.STRING;

    /**
     * 缓存默认有效时间为40分钟
     * @return
     */
    long cacheTime() default 2400000L;

    /**满足该条件则执行缓存操作
     *
     * @return
     */
    String condition() default "";

    /**满足该条件则不执行缓存操作
     *
     * @return
     */
    String unless() default "";

    /**队列标识
     *
     * @return
     */
    String queueTag() default "";
}
