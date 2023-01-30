package com.scs.kafakasync.com.scs.service.conusmer;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.scs.kafakasync.com.scs.entity.MQEvent;
import com.scs.kafakasync.com.scs.entity.StatusEnum;
import com.scs.kafakasync.com.scs.mapper.EventMapper;
import com.scs.kafakasync.com.scs.service.business.ReceiveBusinessService;
import com.scs.kafakasync.com.scs.service.producer.ConsumerReplyService;
import com.scs.kafakasync.com.scs.util.Selfutil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
@Service
public class ConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);
    @Autowired
    Selfutil selfutil;
    @Autowired
    EventMapper eventMapper;
    @Autowired
    ReceiveBusinessService reviceBusinessService;
    @Value("${kafka.topic.reply}")
    String replyTopic;
    @Value("${kafka.topic.send}")
    String sendTopic;

    @Autowired
    ConsumerReplyService consumerReplyService;
/*

*/
    @Retryable(value = KafkaException.class, maxAttempts = 3, backoff = @Backoff(delay = 3600, multiplier = 1.5))
    @KafkaListener(topics = "send-topic",groupId = "consumer_consumer_group")
    public void consumeMessageFromSender(ConsumerRecord<?, ?> record, Acknowledgment ack) throws Exception{
        try {
            logger.info("业务接受放接收到了消息 msgID{}",ConvertObjectFromMQ(record).getMsgID());
            //业务处理
            reviceBusinessService.processBusiness(ConvertObjectFromMQ(record));
            //第一次 成功消费 参数重新组装改变状态码 并向发送方回调
            consumerReplyService.replyMessage(replyTopic, assembleAfterBusiness(ConvertObjectFromMQ(record)));
            logger.info("业务接受方成功发送回调函数 msgID{}",ConvertObjectFromMQ(record).getMsgID());
            ack.acknowledge();
            logger.info("业务方成功消费并且向发送方发送回调  MsgId{}",ConvertObjectFromMQ(record).getMsgID());
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                //已经消费过了
                logger.info("业务方已经成功消费！！！重复消息！！！ 发送回调  MsgId{}",ConvertObjectFromMQ(record).getMsgID());
                alreadyProcess(record);
                ack.acknowledge();
            } else {
                throw e;
            }
        }

    }

    /*
    *
    *   发送发接受来自业务方的回调
    *
    **/
    @Retryable(value = KafkaException.class, maxAttempts = 3, backoff = @Backoff(delay = 3600, multiplier = 1.5))
    @KafkaListener(topics = "reply-topic",groupId = "producer_consumer_group")
    public void consumeMessageFromConsumer(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        MQEvent mqEvent = ConvertObjectFromMQ(record);
        mqEvent.setUpdateTime(selfutil.getCurrentTime());
        eventMapper.updateEventStatus(mqEvent);
        ack.acknowledge();
        logger.info("业务发起方成功接受回调 并改变消息状态 MsgId{}",ConvertObjectFromMQ(record).getMsgID());

    }


    private void alreadyProcess(ConsumerRecord<?, ?> record) {
        MQEvent reviceEventWithDuplicateKey = ConvertObjectFromMQ(record);
        reviceEventWithDuplicateKey.setMessageStatus(StatusEnum.SUCCESSFUL_DUPLICATE.getCode());
        reviceEventWithDuplicateKey.setTopic(replyTopic);
        consumerReplyService.replyMessage(replyTopic, reviceEventWithDuplicateKey);
    }

    public MQEvent ConvertObjectFromMQ(ConsumerRecord<?, ?> record) {
        MQEvent reviceEventWithDuplicateKey = JSON.parseObject(record.value().toString(), MQEvent.class);
        return reviceEventWithDuplicateKey;
    }

    private MQEvent assembleAfterBusiness(MQEvent reviceEvent) {
        MQEvent afterBusiness = new MQEvent();
        BeanUtils.copyProperties(reviceEvent, afterBusiness);
        afterBusiness.setTopic(replyTopic);
        afterBusiness.setMessageStatus(StatusEnum.SUCCESSFUL_BUSINESS.getCode());
        return afterBusiness;
    }

}