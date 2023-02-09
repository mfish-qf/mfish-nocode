package cn.com.mfish.openai.common;

import cn.com.mfish.common.core.utils.http.OkHttpUtils;
import cn.com.mfish.openai.entity.Completion;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: openai操作类
 * @author: mfish
 * @date: 2023/2/8 11:07
 */
@Slf4j
public class AiOperator {

    /**
     * 请求数据
     *
     * @param ask_string
     * @return
     * @throws IllegalAccessException
     */
    public static String answerMyQuestion(String url, String token, String ask_string) {
        Completion openAi = new Completion();
        //添加我们需要输入的内容
        openAi.setModel("text-davinci-003");
        openAi.setPrompt(ask_string);
        openAi.setTemperature(0.7);
        openAi.setMax_tokens(256);
        openAi.setTop_p(1);
        openAi.setFrequency_penalty(0);
        openAi.setPresence_penalty(0);
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", "Bearer " + token);
        map.put("Content-Type", "application/json");
        try {
            return OkHttpUtils.post(url, openAi, map);
        } catch (Exception ex) {
            log.error("请求出错", ex);
            return "";
        }
    }

}
