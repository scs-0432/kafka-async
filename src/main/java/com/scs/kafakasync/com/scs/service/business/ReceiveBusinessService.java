package com.scs.kafakasync.com.scs.service.business;

import com.scs.kafakasync.com.scs.entity.MQEvent;
import com.scs.kafakasync.com.scs.mapper.EvenConusmertMapper;
import com.scs.kafakasync.com.scs.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 大菠萝
 * @date 2023/01/20 00:01
 **/
@Service
public class ReceiveBusinessService {
    @Autowired
    EvenConusmertMapper evenConusmertMapper;

    public void processBusiness(MQEvent mqEvent) {
        try {
            evenConusmertMapper.insertEvent(mqEvent);
        } catch (Exception e) {
            throw e;
        }
    }
}