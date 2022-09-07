package cn.com.mfish.test.entity;

import cn.com.mfish.oauth.model.UserInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author ：qiufeng
 * @description：测试参数
 * @date ：2022/9/7 11:10
 */
@Data
@ApiModel("测试参数")
public class TestParam {
    @ApiModelProperty("用户信息")
    private UserInfo userInfo;
    @ApiModelProperty("字符参数")
    private String string1;
    @ApiModelProperty("map参数")
    private Map<String,String> map1;
    @ApiModelProperty("list参数")
    private List<String> list1;
}
