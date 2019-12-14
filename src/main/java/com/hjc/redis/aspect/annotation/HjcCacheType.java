package com.hjc.redis.aspect.annotation;

/**
 * @author 胡绍波
 * 缓存类型
 */
public enum HjcCacheType {
    STRING,/** ---string类型----**/
    LIST,/** ---list类型----**/
    SET,/** ---set类型----**/
    ZSET,/** ---zset类型----**/
    HASH;/** ---hash类型----**/
}
