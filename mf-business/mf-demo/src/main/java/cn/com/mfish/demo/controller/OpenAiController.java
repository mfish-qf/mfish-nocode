package cn.com.mfish.demo.controller;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.demo.common.AiOperator;
import cn.com.mfish.demo.config.OpenAiConfig;
import cn.com.mfish.demo.entity.CompletionResult;
import cn.com.mfish.demo.entity.Question;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @description: 聊天机器人
 * @author: mfish
 * @date: 2023/2/8 11:46
 */
@Slf4j
@Tag(name = "chatGpt")
@RestController
@RequestMapping("/openai")
public class OpenAiController {
    @Resource
    OpenAiConfig openAiConfig;

    @PostMapping("/answer")
    public Result<CompletionResult> answer(@RequestBody Question question) throws IOException {
        Result<String> result = AiOperator.answerMyQuestion(openAiConfig.getUrl(), openAiConfig.getToken(), question.getData());
        return Result.buildResult(new CompletionResult().setId(question.getId())
                .setResult(result.getData()), result.getCode(), result.getMsg());
    }
}
