package com.scs.kafakasync.com.scs.util;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * @author 大菠萝
 * @date 2023/01/20 00:49
 **/
@Component
public class Selfutil {
    public Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }
}