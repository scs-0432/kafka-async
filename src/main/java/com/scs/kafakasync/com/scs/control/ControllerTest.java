package com.scs.kafakasync.com.scs.control;

import com.scs.kafakasync.com.scs.aspect.CostTime;
import com.scs.kafakasync.com.scs.entity.MQEvent;
import com.scs.kafakasync.com.scs.entity.StatusEnum;
import com.scs.kafakasync.com.scs.service.producer.ConsumerReplyService;
import com.scs.kafakasync.com.scs.service.producer.ProducerService;
import com.scs.kafakasync.com.scs.util.Selfutil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author 大菠萝
 * @date 2023/01/19 15:03
 **/



@RestController

@RequestMapping(value = "/test")
public class ControllerTest {
    @Value("${kafka.topic.reply}")
    String replyTopic;
    @Value("${kafka.topic.send}")
    String sendTopic;
    @Autowired
    Selfutil selfutil;
    @Autowired
    ProducerService producerService;
    private AtomicLong atomicLong = new AtomicLong();
    private static final Logger logger = LoggerFactory.getLogger(ControllerTest.class);

    @CostTime
    @PostMapping("/send")
    public void sendMessageToKafkaTopic(@Param("num")int num) {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        mockEntity(num).forEach(mqEvent -> {
            threadPool.submit(() -> {
                producerService.sendMessage(sendTopic, mqEvent);
                logger.info("正在发送消息MsgId{}", mqEvent.getMsgID());
            });
        });


    }

    @PostMapping("/duplicate")
    public void sendMessageToKafkaTopicWithDuplicate(@Param("msgId")String msgId) {
        MQEvent mqEvent = mockEntity(msgId);
        producerService.sendMessage(sendTopic, mqEvent);
        logger.info("正在发送重复消息MsgId{}", mqEvent.getMsgID());


    }


    private List<MQEvent> mockEntity(int num) {
        List<MQEvent> res = new ArrayList<>();
        for (int i = 0; i < num; i++) {

            res.add(MQEvent.builder()
                    .sendTime(selfutil.getCurrentTime())
                    .messageStatus(StatusEnum.SUCCESSFUL.getCode())
                    .creatTime(selfutil.getCurrentTime())
                    .body("test message     " + i)
                    .msgID(UUID.randomUUID().toString())
                    .topic(sendTopic)
                    .updateTime(selfutil.getCurrentTime())
                    .build());

        }
        return res;
    }

    private MQEvent mockEntity(String msgId) {
        MQEvent build = MQEvent.builder()
                .sendTime(selfutil.getCurrentTime())
                .messageStatus(StatusEnum.SUCCESSFUL.getCode())
                .creatTime(selfutil.getCurrentTime())
                .body("test message with duplicate  ")
                .topic(sendTopic)
                .updateTime(selfutil.getCurrentTime()).build();
        build.setMsgID(msgId);
        return build;

    }
}