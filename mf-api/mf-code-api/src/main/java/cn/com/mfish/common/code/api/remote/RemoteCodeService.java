package cn.com.mfish.common.code.api.remote;

import cn.com.mfish.common.code.api.fallback.RemoteCodeFallback;
import cn.com.mfish.common.code.api.req.ReqCode;
import cn.com.mfish.common.code.api.vo.CodeVo;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author: mfish
 * @description: 远程代码生成服务
 * @date: 2023/04/13
 */
@FeignClient(contextId = "remoteCodeService", value = ServiceConstants.CODE_SERVICE, fallbackFactory = RemoteCodeFallback.class)
public interface RemoteCodeService {
    @GetMapping("/code")
    Result<List<CodeVo>> getCode(@SpringQueryMap ReqCode reqCode);

    @GetMapping("/code/save")
    Result<String> saveCode(@SpringQueryMap ReqCode reqCode);

    @GetMapping("/code/download")
    Result<byte[]> downloadCode(@SpringQueryMap ReqCode reqCode);
}