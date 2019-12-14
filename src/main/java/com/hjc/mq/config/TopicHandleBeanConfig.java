package com.hjc.mq.config;

import com.hjc.redis.aspect.annotation.HjcCachePut;
import com.hjc.redis.aspect.annotation.HjcCacheType;
import com.hjc.redis.aspect.annotation.HjcCacheable;
import com.hjc.redis.aspect.intercept.CacheInfo;
import com.hjc.redis.config.ObjectFieldParserConfig;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 胡绍波
 * topic 配置类
 */
@Configuration
@EnableAutoConfiguration
public class TopicHandleBeanConfig {
    public static Map<String, Map<String,Class>> mqMap = new ConcurrentHashMap<String, Map<String,Class>>();
    private static URLClassLoader classLoader;//默认使用的类加载器

    public static URL serverPath;
    public static URL classPath;
    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 配置所有钉钉回调事件类
     */
    @Bean
    public TopicHandleBeanConfig loadConfig()
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
                    HjcRocketMqHandle rocketMqHandle = (HjcRocketMqHandle) clazz.getAnnotation(HjcRocketMqHandle.class);
                    if (Objects.nonNull(rocketMqHandle)){
                        Map<String,Class> tagMap = mqMap.get(rocketMqHandle.topic());
                        if(Objects.isNull(tagMap)){
                            tagMap = new ConcurrentHashMap<String, Class>();
                        }
                        tagMap.put(rocketMqHandle.tag(),clazz);
                        mqMap.put(rocketMqHandle.topic(),tagMap);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
