package com.scs.kafakasync.com.scs.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;


/**
 * @author 大菠萝
 * @date 2023/01/19 15:03
 **/

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MQEvent implements Serializable {
    private String msgID;
    private String body;
    private int messageStatus;
    private java.util.Date creatTime;
    private java.util.Date sendTime;
    private Date updateTime;
    private String topic;
}