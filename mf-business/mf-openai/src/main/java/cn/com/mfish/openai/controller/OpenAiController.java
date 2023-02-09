package cn.com.mfish.openai.controller;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.openai.common.AiOperator;
import cn.com.mfish.openai.config.OpenAiConfig;
import cn.com.mfish.openai.entity.CompletionResult;
import cn.com.mfish.openai.entity.Question;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description: 聊天机器人
 * @author: mfish
 * @date: 2023/2/8 11:46
 */
@Slf4j
@Api(tags = "chatGpt")
@RestController
@RequestMapping("/openai")
public class OpenAiController {
    @Resource
    OpenAiConfig openAiConfig;

    @PostMapping("/answer")
    public Result<CompletionResult> answer(@RequestBody Question question) {
        return Result.ok(new CompletionResult().setId(question.getId())
                .setResult(AiOperator.answerMyQuestion(openAiConfig.getUrl(), openAiConfig.getToken(), question.getData())), "请求成功");
    }
}
