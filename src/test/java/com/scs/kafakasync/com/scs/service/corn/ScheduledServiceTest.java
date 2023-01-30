package com.scs.kafakasync.com.scs.service.corn;

import cn.hutool.extra.mail.MailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ScheduledServiceTest {
    @Value("${email.address}")
    String warnEmailAddress;

    @Test
    public void testSendEmail() {
        MailUtil.send(warnEmailAddress, "警告", "出错了", false);
    }

}