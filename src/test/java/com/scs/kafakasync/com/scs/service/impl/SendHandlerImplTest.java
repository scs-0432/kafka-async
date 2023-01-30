package com.scs.kafakasync.com.scs.service.impl;

import cn.hutool.core.date.DateTime;
import com.scs.kafakasync.com.scs.entity.MQEvent;
import com.scs.kafakasync.com.scs.mapper.EventMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@SpringBootTest
class SendHandlerImplTest {

    @Autowired
    EventMapper eventMapper;

    @Test
    public void insert() throws ParseException {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//注意月和小时的格式为两个大写字母
        java.util.Date date = DateTime.now();//获得当前时间
        String t = df.format(date);//将当前时间转换成特定格式的时间字符串，这样便可以插入到数据库中





            MQEvent mqEvent = new MQEvent();
            mqEvent.setBody("text mybatis");
            mqEvent.setMsgID(UUID.randomUUID().toString());
            mqEvent.setTopic("test");
            mqEvent.setMessageStatus(new Random().nextInt(5));
            mqEvent.setCreatTime(new Timestamp(System.currentTimeMillis()));
            mqEvent.setSendTime(getSendTime());
            mqEvent.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            eventMapper.insertEvent(mqEvent);

    }

    private Timestamp getSendTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    @Test
    public void test() {
        List<MQEvent> notSuccessful = eventMapper.findNotSuccessful();
        System.out.println(notSuccessful.size());
    }
    @Test
    public  void testUpdate(){
        eventMapper.updateEventStatus(MQEvent
                .builder().updateTime(getSendTime())
                .msgID("023cd64f-3bb1-4403-895c-756ab0954ab7")
                .messageStatus(1).build());
    }

}