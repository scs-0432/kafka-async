package com.scs.kafakasync.com.scs.service.producer;

import com.scs.kafakasync.com.scs.entity.MQEvent;
import com.scs.kafakasync.com.scs.entity.StatusEnum;
import com.scs.kafakasync.com.scs.mapper.EventMapper;
import com.scs.kafakasync.com.scs.util.Selfutil;
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
public class ProducerService {

    private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);
    @Autowired
    Selfutil selfutil;
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    EventMapper eventMapper;

    @Retryable(value = KafkaException.class, maxAttempts = 3, backoff = @Backoff(delay = 3600, multiplier = 1.5))
    public void sendMessage(String topic, Object o) {
       /* try {

            ListenableFuture<SendResult<String, Object>> send = kafkaTemplate.send(topic, o);
            send.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
                @Override
                public void onFailure(Throwable ex) {
                    eventMapper.updateEventStatus((MQEvent) o);
                    logger.error("业务方发送消息失败,准备重试->{} ,EnentId ", o.toString(), ((MQEvent) o).getMsgID().toString());
                }

                @Override
                public void onSuccess(SendResult<String, Object> result) {
                    logger.info("业务发起方成功发送消息->{} ", o.toString());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }*/

        kafkaTemplate.send(topic, o).addCallback(success -> {
            eventMapper.insertEvent((MQEvent) o);
            logger.info("业务发起方成功发送消息->{} ", o.toString());
        }, failete -> {
            logger.error("业务方发送消息失败,准备重试->{} ,MsgId{} ", o.toString(), ((MQEvent) o).getMsgID().toString());
            throw new KafkaException("kafka业务发起方发送异常");
        });
    }

    @Recover
    public void recover(KafkaException e,String topic, Object o) {
        MQEvent mqEvent = (MQEvent) o;
        mqEvent.setUpdateTime(selfutil.getCurrentTime());
        mqEvent.setMessageStatus(StatusEnum.ERROR.getCode());
        eventMapper.updateEventStatus(mqEvent);
        logger.error("重试此时用完,人工介入,消息Id是{}",mqEvent.getMsgID());
    }
}