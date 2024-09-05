package cn.com.mfish.demo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 回答结果
 * @author: mfish
 * @date: 2023/2/8 22:42
 */
@Data
@Accessors(chain = true)
public class CompletionResult {
    private String id;
    private String result;
}
