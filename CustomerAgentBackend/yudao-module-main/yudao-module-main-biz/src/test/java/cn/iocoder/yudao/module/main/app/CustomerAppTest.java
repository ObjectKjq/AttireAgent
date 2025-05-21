package cn.iocoder.yudao.module.main.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerAppTest {

    @Resource
    private CustomerApp customerApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是小艾。";
        String answer = customerApp.doChat(message, chatId);
        message = "我想买条牛仔裤。";
        answer = customerApp.doChat(message, chatId);
        message = "我叫什么来着？我刚才告诉过你！";
        answer = customerApp.doChat(message, chatId);
    }
}