package cn.com.mfish.test.entity;

import cn.com.mfish.common.oauth.api.entity.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: mfish
 * @description: 测试参数
 * @date: 2022/9/7 11:10
 */
@Data
@Schema(description = "测试参数")
public class TestParam {
    @Schema(description = "用户信息")
    private UserInfo userInfo;
    @Schema(description = "字符参数")
    private String string1;
    @Schema(description = "map参数")
    private Map<String,String> map1;
    @Schema(description = "list参数")
    private List<String> list1;
}
