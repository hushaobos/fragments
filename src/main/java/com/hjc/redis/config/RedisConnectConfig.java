package com.hjc.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author 胡绍波
 * 二级缓存配置类 , 有效时长为1小时
 */
@Configuration
@EnableAutoConfiguration
public class RedisConnectConfig {
    @Value(value = "${hjc.redis.host}")
    private String host; //redis集群地址

    @Value(value = "${hjc.redis.port}")
    private int port; //redis集群端口

    @Value(value = "${hjc.redis.password}")
    private String password;//redis集群密码

    @Value(value = "${hjc.redis.timeout}")
    private int timeout;//redis连接时间 6000毫秒

    @Value(value = "${hjc.redis.jedis.pool.min-idle}")
    private int minIdle;//redis最小连接数

    @Value(value = "${hjc.redis.jedis.pool.max-active}")
    private int maxActive;//redis最大活动数

    @Value(value = "${hjc.redis.jedis.pool.max-idle}")
    private int maxIdle;//redis最大连接数

    @Value(value = "${hjc.redis.jedis.pool.max-wait}")
    private long maxWait;//redis最大等待时间 100毫秒

    /**
     * 二级缓存线程池配置
     * @return
     */
    @Bean
    public JedisPool hjcCache(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();//redis线程池配置
        // 设置最大10个连接
        jedisPoolConfig.setMaxTotal(maxActive);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWait);
        jedisPoolConfig.setMaxIdle(maxIdle);
        JedisPool pool = new JedisPool(jedisPoolConfig, host,port,timeout,password);

        return pool;
    }
}
