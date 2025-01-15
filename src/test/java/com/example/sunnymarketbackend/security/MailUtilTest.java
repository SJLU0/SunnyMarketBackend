package com.example.sunnymarketbackend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MailUtilTest {

    @Autowired
    private MailUtil mailUtil;

    @Test
    public void testSimpleHtml() {
        mailUtil.sendSimpleHtml(
                List.of("iamxyzgos5933@gmail.com"),
                "Simple html",
                "<html><body><p>你好！</p><p>My name is <b>Sunny Market 管理員</b>.</p></body></html>"
        );
    }

}