package com.scs.kafakasync.com.scs.service.producer;

import com.scs.kafakasync.com.scs.entity.MQEvent;
import com.scs.kafakasync.com.scs.mapper.EventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class ConsumerReplyService {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerReplyService.class);
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    EventMapper eventMapper;

    @Retryable(value = KafkaException.class, maxAttempts = 3, backoff = @Backoff(delay = 3600, multiplier = 1.5))
    public void replyMessage(String topic, Object o) {

        kafkaTemplate.send(topic, o).addCallback(success -> {
            logger.info("业务接受方消费成功->{} ", o.toString());
        }, failete -> {
            logger.error("业务接收方消费失败,准备重试->{} ,EnentId ", o.toString(), ((MQEvent) o).getMsgID().toString());
            throw new KafkaException("kafka消费方业务放发送异常");
        });
    }

    @Recover
    public void recover(KafkaException e) {
        logger.info("重试此时用完,人工介入");
    }
}