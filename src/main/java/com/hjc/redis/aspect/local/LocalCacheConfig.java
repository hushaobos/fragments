package com.hjc.redis.aspect.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 胡绍波
 * 本地缓存配置类
 */
@Configuration
@EnableAutoConfiguration
public class LocalCacheConfig {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Value(value = "${hjc.redis.local-time}")
    private long cacheTime = 1000;//本地缓存时间

    @Bean
    public LocalCacheConfig localCache(){
        LocalCache.initLocalCache(cacheTime);
        return this;
    }
}
