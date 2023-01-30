package com.scs.kafakasync.com.scs.entity;

import lombok.*;

/**
 * @author 大菠萝
 * @date 2023/01/20 00:11
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventConsumer {
    private String msgID;
    private String body;
    private String topic;

}