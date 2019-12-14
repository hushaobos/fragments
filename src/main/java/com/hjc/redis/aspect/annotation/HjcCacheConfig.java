package com.hjc.redis.aspect.annotation;

import java.lang.annotation.*;

/**
 * @author 胡绍波
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface HjcCacheConfig {
}
