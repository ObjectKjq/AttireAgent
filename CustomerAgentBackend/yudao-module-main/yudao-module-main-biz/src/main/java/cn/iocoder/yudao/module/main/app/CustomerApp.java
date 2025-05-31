package cn.iocoder.yudao.module.main.app;

import cn.iocoder.yudao.module.main.advisor.MyLoggerAdvisor;
import cn.iocoder.yudao.module.main.advisor.ReReadingAdvisor;
import cn.iocoder.yudao.module.main.chatmemory.FileBaseChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class CustomerApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是一个智能衣服推荐助手，能根据用户提供信息提供穿搭建议列表，请根据用户的问题，提供专业的回答。";

    public CustomerApp(ChatModel chatModel) {
        // ChatMemory chatMemory = new InMemoryChatMemory(); // 基于内存的会话记忆
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        ChatMemory chatMemory = new FileBaseChatMemory(fileDir); // 基于文件存储的会话记忆
        chatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                // 指定拦截器
                .defaultAdvisors(
                        // InMemoryChatMemory基于内存的存储会话记忆
                        // CassandraChatMemory基于cassandra有过期时间的存储会话记忆
                        // Neo4jChatMemory基于Neo4j没有过期时间的存储会话记忆
                        // JdbcChatMemory基于数据库的存储会话记忆
                        new MessageChatMemoryAdvisor(chatMemory),
                        // 自定义日志拦截器
                        new MyLoggerAdvisor()
                        // 自定义推理增强，开启后成本更高
                        // new ReReadingAdvisor()
                )
                .build();
    }

    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String context = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", context);
        return context;

    }

    public record CustomerReport(String title, List<String> suggestions) {

    }

    /**
     * 生成会话报告
     * @param message
     * @param chatId
     * @return
     */
    public CustomerReport doChatWithReport(String message, String chatId) {
        CustomerReport customerReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成会话报告，标题为{用户名}的咨询报告，内容为建议的列表")
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(CustomerReport.class); // 结构化输出
        log.info("CustomerReport: {}", customerReport);
        return customerReport;

    }

    @Resource
    private VectorStore customerAppVectorStore;

    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())
                .advisors(new QuestionAnswerAdvisor(customerAppVectorStore))
                .call()
                .chatResponse();
        String context = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", context);
        return context;
    }
}
