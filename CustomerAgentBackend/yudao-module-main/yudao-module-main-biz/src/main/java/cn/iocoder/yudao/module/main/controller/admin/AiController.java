package cn.iocoder.yudao.module.main.controller.admin;

import cn.iocoder.yudao.module.main.agent.MyManus;
import cn.iocoder.yudao.module.main.app.CustomerApp;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
@Slf4j
public class AiController {

    @Resource
    private CustomerApp customerApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    @GetMapping("/customer_app/chat/sync")
    @Operation(summary = "同步调用")
    @PermitAll
    public String doChatWithCustomerAppSync(String message, String chatId) {
        return customerApp.doChat(message, chatId);
    }

    @GetMapping(value = "/customer_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "sse调用")
    @PermitAll
    public Flux<String> doChatWithCustomerAppSSE(String message, String chatId) {
        return customerApp.doChatByStream(message, chatId);
    }

    @GetMapping(value = "/customer_app/chat/event")
    @Operation(summary = "事件控制")
    @PermitAll
    public Flux<ServerSentEvent<String>> doChatWithCustomerAppEvent(String message, String chatId) {
        return customerApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    @GetMapping("/customer_app/chat/sse/emitter")
    @Operation(summary = "自己控制的sse")
    @PermitAll
    public SseEmitter doChatWithCustomerAppSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
        // 获取 Flux 数据流并直接订阅
        customerApp.doChatByStream(message, chatId)
                .subscribe(
                        // 处理每条消息
                        chunk -> {
                            try {
                                emitter.send(chunk);
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        // 处理错误
                        emitter::completeWithError,
                        // 处理完成
                        emitter::complete
                );
        // 返回emitter
        return emitter;
    }

    /**
     * 流式调用 Manus 超级智能体
     *
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    @Operation(summary = "流式智能体")
    @PermitAll
    public SseEmitter doChatWithManus(String message) {
        MyManus yuManus = new MyManus(allTools, dashscopeChatModel);
        return yuManus.runStream(message);
    }

}
