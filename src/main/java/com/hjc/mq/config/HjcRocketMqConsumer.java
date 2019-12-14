package com.hjc.mq.config;

import com.hjc.mq.constant.RocketGroupConstant;
import com.hjc.mq.constant.RocketMqTopicConstant;
import com.hjc.utils.SpringUtil;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

import static com.hjc.mq.config.TopicHandleBeanConfig.mqMap;

/**
 * 登录消息处理
 */
@Service
@RocketMQMessageListener(consumerGroup = "${rocketmq.consumer.group:}", topic = "")
public class HjcRocketMqConsumer implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt messageExt) {
        Map<String,Class> tagMap = mqMap.get(RocketMqTopicConstant.HJC_SIGN_TOPIC);
        if(Objects.nonNull(tagMap)){
            Class<RocketMQListener> rocketmqHandle = tagMap.get(messageExt.getTags());
            RocketMQListener listener = SpringUtil.getBean(rocketmqHandle);
            listener.onMessage(messageExt);
        }
    }
}
