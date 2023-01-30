package com.scs.kafakasync.com.scs.mapper;

import com.scs.kafakasync.com.scs.entity.MQEvent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 大菠萝
 * @date 2023/01/19 15:13
 **/
@Mapper
public interface EvenConusmertMapper {



    void insertEvent(MQEvent mqEvent);



}