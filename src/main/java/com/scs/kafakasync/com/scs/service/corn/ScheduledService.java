package com.scs.kafakasync.com.scs.service.corn;

import cn.hutool.extra.mail.MailUtil;
import com.google.common.hash.BloomFilter;
import com.scs.kafakasync.com.scs.entity.MQEvent;
import com.scs.kafakasync.com.scs.mapper.EventMapper;
import com.scs.kafakasync.com.scs.service.conusmer.ConsumerService;
import com.scs.kafakasync.com.scs.service.producer.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * @author 大菠萝
 * @date 2023/01/20 20:34
 **/
@Component
public class ScheduledService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledService.class);
    @Value("${kafka.topic.reply}")
    String replyTopic;
    @Value("${kafka.topic.send}")
    String sendTopic;
    @Autowired
    ProducerService producerService;
    @Autowired
    EventMapper eventMapper;

    @Autowired
    @Qualifier("MsgIdBloomFilter")
    BloomFilter<String> bloomFilter;

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    @Qualifier("MsgIdAlreadyBloomFilter")
    BloomFilter<String> msgIdAlreadyBloomFilter;

    @Value("${email.address}")
    String warnEmailAddress;

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void scheduledCompensate() {
        eventMapper.findFailureMessage().forEach(mqEvent -> {
            if (bloomFilter.mightContain(mqEvent.getMsgID().toString())&&!msgIdAlreadyBloomFilter.mightContain(mqEvent.getMsgID().toString())) {
                /*
                 *  已经重新投递过邮件 人工介入处理

                 */
                sendEmail(warnEmailAddress, mqEvent);
                msgIdAlreadyBloomFilter.put(mqEvent.getMsgID());
            } else {
                logger.info("定时任务重发消息ID是{}" + mqEvent.getMsgID());
                bloomFilter.put(mqEvent.getMsgID());
                producerService.sendMessage(sendTopic, mqEvent);

            }

        });

    }

    private void sendEmail(String warnEmailAddress, MQEvent mqEvent) {
        try {
            String msg = mqEvent.getMsgID() + "消息异常人工介入"+ UUID.randomUUID().toString();
            MailUtil.send(warnEmailAddress, "补偿后依旧失败消息", msg, false);
        } catch (Exception e) {
            logger.error(e.toString());
            logger.error("消息发送服务异常");
        }

    }


}