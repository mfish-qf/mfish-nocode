package cn.com.mfish.common.core.web;

import cn.com.mfish.common.core.constants.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求返回泛型结果
 *
 * @author: mfish
 * @date: 2021/3/16 14:34
 */
@ApiModel("通用泛型结果返回")
@Accessors(chain = true)
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("是否成功")
    private boolean success = true;
    @ApiModelProperty("状态码")
    private int code = Constants.SUCCESS;
    @ApiModelProperty("返回内容")
    private String msg;
    @ApiModelProperty("数据对象")
    private T data;
    @ApiModelProperty("补充参数 用于多次检查时携带上一次参数提供下次使用")
    private Map<String, String> param = new HashMap<>();

    public static <T> Result<T> buildResult(T data, int code, String msg) {
        return new Result<T>().setCode(code).setData(data).setMsg(msg).setSuccess(code == Constants.SUCCESS ? true : false);
    }

    public static <T> Result<T> ok() {
        return buildResult(null, Constants.SUCCESS, null);
    }

    public static <T> Result<T> ok(String msg) {
        return buildResult(null, Constants.SUCCESS, msg);
    }

    public static <T> Result<T> ok(T data) {
        return buildResult(data, Constants.SUCCESS, null);
    }

    public static <T> Result<T> ok(T data, String msg) {
        return buildResult(data, Constants.SUCCESS, msg);
    }

    public static <T> Result<T> fail() {
        return buildResult(null, Constants.FAIL, null);
    }

    public static <T> Result<T> fail(String msg) {
        return buildResult(null, Constants.FAIL, msg);
    }

    public static <T> Result<T> fail(T data) {
        return buildResult(data, Constants.FAIL, null);
    }

    public static <T> Result<T> fail(T data, String msg) {
        return buildResult(data, Constants.FAIL, msg);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return buildResult(null, code, msg);
    }

}
