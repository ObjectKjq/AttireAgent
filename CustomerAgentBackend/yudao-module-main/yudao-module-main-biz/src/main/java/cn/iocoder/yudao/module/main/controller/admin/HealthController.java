package cn.iocoder.yudao.module.main.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.main.app.CustomerApp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - Test")
@RestController
@RequestMapping("/main/health")
@Validated
public class HealthController {

    @Resource
    private CustomerApp customerApp;

    @GetMapping("/chat")
    @Operation(summary = "获取 test 信息")
    public CommonResult<String> chat() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是小艾。";
        String answer = customerApp.doChat(message, chatId);
        message = "我想买条牛仔裤。";
        answer = customerApp.doChat(message, chatId);
        message = "我叫什么来着？我刚才告诉过你！";
        answer = customerApp.doChat(message, chatId);
        return success("true");
    }

    @GetMapping("/chat/report")
    @Operation(summary = "获取 test 信息")
    public CommonResult<String> chatReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是小艾。";
        CustomerApp.LoveReport loveReport = customerApp.doChatWithReport(message, chatId);
        return success("true");
    }

}
