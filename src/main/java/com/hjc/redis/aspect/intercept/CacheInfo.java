package com.hjc.redis.aspect.intercept;

import com.hjc.redis.aspect.util.RedisConstant;
import redis.clients.jedis.Tuple;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author 胡绍波
 * 缓存信息类
 */
public class CacheInfo {
    private String key; //缓存key值
    private String field;//获取缓存字段名
    private String[] values; //缓存value值, 可能有多个适配list
    private List<CacheField> cacheFields;//缓存对象的字段
    private List<Tuple> zsets;//zset数组
    private RedisConstant.RedisOptation read;//0:代表读,1代表写,2代表删除
    private Object resultObj;//结果对象
    private long time;//缓存时间

    public CacheInfo() {
    }

    private CacheInfo(String key, String field, List<CacheField> cacheFields, List<Tuple> zsets, RedisConstant.RedisOptation read, Object resultObj, long time, String... values) {
        this.key = key;
        this.field = field;
        this.values = values;
        this.cacheFields = cacheFields;
        this.zsets = zsets;
        this.read = read;
        setResultObj(resultObj);
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public String getField() {
        return field;
    }

    public String[] getValues() {
        return values;
    }

    public RedisConstant.RedisOptation getRead() {
        return read;
    }

    public long getTime() {
        return time;
    }

    public List<CacheField> getCacheFields() {
        return cacheFields;
    }

    public List<Tuple> getZsets() {
        return zsets;
    }

    public void setResultObj(Object resultObj) {
        this.resultObj = resultObj;
        if(Objects.nonNull(resultObj) && Objects.nonNull(cacheFields)){
            initValue();
        }
    }

    public void setRead(RedisConstant.RedisOptation read) {
        this.read = read;
    }

    private void initValue(){
        for (CacheField cacheField : cacheFields)
        {
            try {
                cacheField.field.setAccessible(true);
                Object value = cacheField.field.get(resultObj);
                cacheField.setValue(value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public Object getResultObj() {
        return resultObj;
    }

    /**
     * 字段属性类 , 用于适配hash
     */
    public static class CacheField{
        private Field field;
        private Object value;

        public CacheField(Field field) {
            this.field = field;
        }

        public CacheField(Field field, String value) {
            this.field = field;
            this.value = value;
        }

        public Field getField() {
            return field;
        }

        public Object getValue() {
            return value;
        }

        protected void setValue(Object value) {
            this.value = value;
        }
    }

    /**
     * 缓存信息创建类
     */
    public static class Builder{
        private String key; //缓存key值
        private String field;//获取缓存字段名
        private String[] values; //缓存value值, 可能有多个适配list
        private List<CacheField> cacheFields;//缓存对象的字段
        private List<Tuple> zsets;//zset数组
        private RedisConstant.RedisOptation read;//0:代表读,1代表写,2代表删除
        private Object resultObj;//结果对象
        private long time;//缓存时间

        public Builder() {
            cacheFields = new LinkedList<CacheField>();
        }

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public Builder setField(String field) {
            this.field = field;
            return this;
        }

        public Builder setValues(String... values) {
            this.values = values;
            return this;
        }

        public Builder setCacheFields(List<CacheField> cacheFields) {
            this.cacheFields = cacheFields;
            return this;
        }

        public Builder setCacheFields(CacheField cacheField) {
            this.cacheFields.add(cacheField);
            return this;
        }

        public Builder setRead(RedisConstant.RedisOptation read) {
            this.read = read;
            return this;
        }

        public Builder setTime(long time) {
            this.time = time;
            return this;
        }

        public Builder setZsets(List<Tuple> zsets) {
            this.zsets = zsets;
            return this;
        }

        public Builder setResultObj(Object resultObj) {
            this.resultObj = resultObj;
            return this;
        }

        public CacheInfo build(){
            return new CacheInfo(key,field,cacheFields,zsets,read,resultObj,time,values);
        }
    }
}
