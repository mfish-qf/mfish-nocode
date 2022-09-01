package cn.com.mfish.common.core.web;

import cn.com.mfish.common.core.constants.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 请求返回泛型结果
 *
 * @author qiufeng
 * @date 2021/3/16 14:34
 */
@ApiModel("通用泛型结果返回")
@Accessors(chain = true)
@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("是否成功")
    private boolean success;
    @ApiModelProperty("状态码")
    private int code;
    @ApiModelProperty("返回内容")
    private String msg;
    @ApiModelProperty("数据对象")
    private T data;
    /**
     * 成功
     */
    public static final int SUCCESS = Constants.SUCCESS;

    /**
     * 失败
     */
    public static final int FAIL = Constants.FAIL;

    private static <T> Result<T> restResult(T data, int code, String msg) {
        return new Result<T>().setCode(code).setData(data).setMsg(msg).setSuccess(code == SUCCESS ? true : false);
    }

    public static <T> Result<T> ok() {
        return restResult(null, SUCCESS, null);
    }

    public static <T> Result<T> ok(T data) {
        return restResult(data, SUCCESS, null);
    }

    public static <T> Result<T> ok(T data, String msg) {
        return restResult(data, SUCCESS, msg);
    }

    public static <T> Result<T> fail() {
        return restResult(null, FAIL, null);
    }

    public static <T> Result<T> fail(String msg) {
        return restResult(null, FAIL, msg);
    }

    public static <T> Result<T> fail(T data) {
        return restResult(data, FAIL, null);
    }

    public static <T> Result<T> fail(T data, String msg) {
        return restResult(data, FAIL, msg);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

}
