package com.scs.kafakasync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableRetry
@EnableScheduling
public class KafakAsyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafakAsyncApplication.class, args);
    }

}
