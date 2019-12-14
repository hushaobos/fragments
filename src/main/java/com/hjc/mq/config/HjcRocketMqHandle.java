package com.hjc.mq.config;

import java.lang.annotation.*;

/**
 * @author 胡绍波
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface HjcRocketMqHandle{
    String topic() default "";
    String tag() default "";
}
