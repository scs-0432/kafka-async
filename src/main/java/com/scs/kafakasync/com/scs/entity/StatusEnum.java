package com.scs.kafakasync.com.scs.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    SUCCESSFUL(1,"消息投递成功,等待业务消费方响应"),
    SUCCESSFUL_BUSINESS(2,"业务方消费成功"),
    SUCCESSFUL_DUPLICATE(3,"业务方已经成功消费过"),
    ERROR(4, "消息发送方发送消息出错,人工介入");
    private int code;
    private String Msg;

}
