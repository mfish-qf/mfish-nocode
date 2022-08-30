package cn.com.mfish.oauth.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qiufeng
 * @date 2020/2/13 13:48
 */
@ApiModel(value = "检查+返回结果类")
public class CheckWithResult<T> {
    @ApiModelProperty("是否正确true 正确 false 错误")
    private boolean isSuccess = true;
    @ApiModelProperty("结果对象")
    private T result;
    @ApiModelProperty("返回附加信息")
    private String msg;
    @ApiModelProperty("补充参数 用于多次检查时携带上一次参数提供下次使用")
    private Map<String, String> param = new HashMap<>();

    public boolean isSuccess() {
        return isSuccess;
    }

    public CheckWithResult<T> setSuccess(boolean isTrue) {
        this.isSuccess = isTrue;
        return this;
    }

    public T getResult() {
        return result;
    }

    public CheckWithResult<T> setResult(T result) {
        this.result = result;
        return this;
    }

    public CheckWithResult<T> check(CheckWithResult<T> result) {
        if (!this.isSuccess) {
            return this;
        }
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public CheckWithResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Map<String, String> getParam() {
        return param;
    }

    public CheckWithResult<T> setParam(Map<String, String> param) {
        this.param = param;
        return this;
    }
}
