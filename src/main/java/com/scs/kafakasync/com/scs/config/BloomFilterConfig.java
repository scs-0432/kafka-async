package com.scs.kafakasync.com.scs.config;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.scs.kafakasync.com.scs.entity.MQEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author 大菠萝
 * @date 2023/01/20 20:59
 **/
@Configuration
public class BloomFilterConfig {
    @Bean
    public BloomFilter<String> MsgIdBloomFilter(){
        BloomFilter<String> filter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8) , 1000,0.00001);
        return filter;
    }
    @Bean
    public BloomFilter<String> MsgIdAlreadyBloomFilter(){
        BloomFilter<String> filter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8) , 1000,0.00001);
        return filter;
    }

}