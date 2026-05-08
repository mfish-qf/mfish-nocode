package cn.com.mfish.common.core.web;

import cn.com.mfish.common.core.constants.Constants;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求返回泛型结果
 *
 * @author: mfish
 * @date: 2021/3/16 14:34
 */
@Schema(description = "通用泛型结果返回")
@Accessors(chain = true)
@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "是否成功")
    private boolean success = true;
    @Schema(description = "状态码")
    private int code = Constants.SUCCESS;
    @Schema(description = "返回内容")
    private String msg;
    @Schema(description = "数据对象")
    private T data;
    @JsonIgnore
    @Schema(description = "补充参数 用于多次检查时携带上一次参数提供下次使用")
    private Map<String, String> param = new HashMap<>();

    /**
     * 构建返回结果
     *
     * @param data 数据对象
     * @param code 状态码
     * @param msg  返回消息
     * @param <T>  数据类型
     * @return 结果对象
     */
    public static <T> Result<T> buildResult(T data, int code, String msg) {
        return new Result<T>().setCode(code).setData(data).setMsg(msg).setSuccess(code == Constants.SUCCESS);
    }

    /**
     * 返回成功结果（无数据无消息）
     *
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> ok() {
        return buildResult(null, Constants.SUCCESS, null);
    }

    /**
     * 返回成功结果（带消息）
     *
     * @param msg 返回消息
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> ok(String msg) {
        return buildResult(null, Constants.SUCCESS, msg);
    }

    /**
     * 返回成功结果（带数据）
     *
     * @param data 数据对象
     * @param <T>  数据类型
     * @return 成功结果
     */
    public static <T> Result<T> ok(T data) {
        return buildResult(data, Constants.SUCCESS, null);
    }

    /**
     * 返回成功结果（带数据和消息）
     *
     * @param data 数据对象
     * @param msg  返回消息
     * @param <T>  数据类型
     * @return 成功结果
     */
    public static <T> Result<T> ok(T data, String msg) {
        return buildResult(data, Constants.SUCCESS, msg);
    }

    /**
     * 返回失败结果（无数据无消息）
     *
     * @param <T> 数据类型
     * @return 失败结果
     */
    public static <T> Result<T> fail() {
        return buildResult(null, Constants.FAIL, null);
    }

    /**
     * 返回失败结果（带消息）
     *
     * @param msg 返回消息
     * @param <T> 数据类型
     * @return 失败结果
     */
    public static <T> Result<T> fail(String msg) {
        return buildResult(null, Constants.FAIL, msg);
    }

    /**
     * 返回失败结果（带数据）
     *
     * @param data 数据对象
     * @param <T>  数据类型
     * @return 失败结果
     */
    public static <T> Result<T> fail(T data) {
        return buildResult(data, Constants.FAIL, null);
    }

    /**
     * 返回失败结果（带数据和消息）
     *
     * @param data 数据对象
     * @param msg  返回消息
     * @param <T>  数据类型
     * @return 失败结果
     */
    public static <T> Result<T> fail(T data, String msg) {
        return buildResult(data, Constants.FAIL, msg);
    }

    /**
     * 返回失败结果（带状态码和消息）
     *
     * @param code 状态码
     * @param msg  返回消息
     * @param <T>  数据类型
     * @return 失败结果
     */
    public static <T> Result<T> fail(int code, String msg) {
        return buildResult(null, code, msg);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
