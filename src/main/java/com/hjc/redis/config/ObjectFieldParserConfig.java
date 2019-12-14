package com.hjc.redis.config;

import com.alibaba.fastjson.JSONObject;
import com.hjc.redis.aspect.annotation.HjcCachePut;
import com.hjc.redis.aspect.annotation.HjcCacheType;
import com.hjc.redis.aspect.annotation.HjcCacheable;
import com.hjc.redis.aspect.intercept.CacheInfo;
import org.apache.tomcat.util.buf.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 胡绍波
 */
@Configuration
@EnableAutoConfiguration
public class ObjectFieldParserConfig {
    public static Map<String, List<CacheInfo.CacheField>> fieldMap = new ConcurrentHashMap<String, List<CacheInfo.CacheField>>();
    private static URLClassLoader classLoader;//默认使用的类加载器

    public static URL serverPath;
    public static URL classPath;
    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 配置所有钉钉回调事件类
     */
    @Bean
    public ObjectFieldParserConfig loadConfig()
    {
        try{
            serverPath = ResourceUtils.getURL("");
            classPath = ResourceUtils.getURL("classpath:");
            classLoader = new URLClassLoader(new URL[]{serverPath,classPath},this.getClass().getClassLoader());
            findEventClass(classPath.getPath(),"");//找出所有事件类
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(Objects.nonNull(classLoader))
            {
                try {
                    classLoader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }

    /**
     *
     * @param path
     * @param packageName
     */
    private void findEventClass(String path,String packageName)
    {
        File file = new File(path);
        for (File fileNode : file.listFiles())
        {
            String packNameNext = packageName.isEmpty() ? fileNode.getName() : packageName + "." + fileNode.getName();
            if(fileNode.isDirectory())
            {
                findEventClass(fileNode.getPath(),packNameNext);
            }
            else if (fileNode.getName().endsWith(".class"))
            {
                try {
                    Class clazz = classLoader.loadClass(packNameNext.replace(".class",""));
                    Method[] methods = clazz.getDeclaredMethods();
                    for(Method method : methods)
                    {

                        HjcCacheable hjcCacheable = method.getAnnotation(HjcCacheable.class);
                        HjcCachePut hjcCachePut = method.getAnnotation(HjcCachePut.class);
                        HjcCacheType hjcCacheType = null;
                        if (Objects.nonNull(hjcCacheable)){
                            hjcCacheType = hjcCacheable.cacheType();
                        }
                        else if (Objects.nonNull(hjcCachePut)){
                            hjcCacheType = hjcCachePut.cacheType();
                        }

                        if (HjcCacheType.HASH.toString().equals(String.valueOf(hjcCacheType))){
                            Class objectClazz = method.getReturnType();
                            initField(objectClazz);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void initField(Class objClass)
    {
        List<CacheInfo.CacheField> cacheFieldList = new LinkedList<CacheInfo.CacheField>();

        for (Field field : objClass.getDeclaredFields()){
            CacheInfo.CacheField cacheField = new CacheInfo.CacheField(field);
            cacheFieldList.add(cacheField);
        }
        fieldMap.put(objClass.getSimpleName(), (List<CacheInfo.CacheField>) ((LinkedList<CacheInfo.CacheField>) cacheFieldList).clone());
    }
}
