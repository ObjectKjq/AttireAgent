package cn.iocoder.yudao.module.main.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.main.agent.MyManus;
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
    @Resource
    private MyManus myManus;

    @GetMapping("/chat")
    @Operation(summary = "测试基于内存的会话记忆多轮对话")
    public CommonResult<String> chat() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是小艾。";
        String answer = customerApp.doChat(message, chatId);
        message = "我想买条牛仔裤。";
        answer = customerApp.doChat(message, chatId);
        message = "我叫什么来着？我刚才告诉过你！";
        answer = customerApp.doChat(message, chatId);
        return success(answer);
    }

    @GetMapping("/chat/report")
    @Operation(summary = "测试结构化输出内容")
    public CommonResult<CustomerApp.CustomerReport> chatReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是小艾。";
        CustomerApp.CustomerReport customerReport = customerApp.doChatWithReport(message, chatId);
        return success(customerReport);
    }

    @GetMapping("/chat/rag")
    @Operation(summary = "测试rag增强会话")
    public CommonResult<String> testLoadMarkdowns() {
        String chatId = UUID.randomUUID().toString();
        String message = "我是一名男生，瘦高，皮肤偏黑的穿搭技巧是什么？";
        String s = customerApp.doChatWithRag(message, chatId);
        return success(s);
    }

    @GetMapping("/chat/tool")
    @Operation(summary = "测试工具调用会话")
    public CommonResult<String> testTools() {
        String chatId = UUID.randomUUID().toString();
        // 测试联网搜索问题的答案
        String answer1 = customerApp.doChatWithTools("五一想去北京买条裤子，北京有没有推荐的卖裤子的店铺？", chatId);

        // 测试网页抓取：恋爱案例分析
        String answer2 = customerApp.doChatWithTools("最近想买条牛仔裤，看看淘宝（https://uland.taobao.com/）有没有适合的裤子？", chatId);

        // 测试资源下载：图片下载
        String answer3 = customerApp.doChatWithTools("帮我下载一张裤子图片为文件", chatId);

        // 测试终端操作：执行代码
        String answer4 = customerApp.doChatWithTools("执行 Python 脚本来生成数据分析报告", chatId);

        // 测试文件操作：保存用户档案
        String answer5 = customerApp.doChatWithTools("保存我的裤子推荐档案为文件", chatId);

        // 测试 PDF 生成
        String answer6 = customerApp.doChatWithTools("生成一份‘黑皮肤，高个子男生的穿搭技巧PDF文件", chatId);
        return success(answer6);
    }

    @GetMapping("/chat/mcp")
    @Operation(summary = "测试MCP服务调用")
    public CommonResult<String> testMcp() {
        String chatId = UUID.randomUUID().toString();
        String message = "帮我搜索一些流行穿搭图片";
        String s = customerApp.doChatWithMcp(message, chatId);
        return success(s);
    }

    @GetMapping("/chat/reAct")
    @Operation(summary = "测试超级智能体")
    public CommonResult<String> testReAct() {
        String userPrompt = """  
                我现在居住在上海静安区，请帮我找到 5 公里内合适的购衣店铺，  
                并结合一些网络内容，制定一份详细的购衣计划，  
                并以 PDF 格式输出""";
        String answer = myManus.run(userPrompt);
        return success(answer);
    }

}
