package cn.com.mfish.common.oauth.api.remote;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.api.fallback.RemoteOrgFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @description: 组织远程接口
 * @author: mfish
 * @date: 2023/5/22 11:28
 */
@FeignClient(contextId = "remoteOrgService", value = ServiceConstants.OAUTH_SERVICE, fallbackFactory = RemoteOrgFallback.class)
public interface RemoteOrgService {

    /**
     * 根据ID列表查询组织信息
     *
     * @param origin 来源
     * @param ids    组织ID，多个逗号分隔
     * @return 组织列表
     */
    @GetMapping("/org/{ids}")
    Result<List<SsoOrg>> queryById(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("ids") String ids);

    /**
     * 根据固定编码查询组织树
     *
     * @param origin    来源
     * @param code      组织固定编码
     * @param direction 查询方向
     * @return 组织树列表
     */
    @GetMapping("/org/code/{code}")
    Result<List<SsoOrg>> queryByFixCode(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("code") String code, @RequestParam("direction") String direction);

    /**
     * 根据组织编码查询组织下用户列表
     *
     * @param origin   来源
     * @param code     组织固定编码
     * @param account  账号（模糊查询）
     * @param nickname 昵称（模糊查询）
     * @param phone    手机号（模糊查询）
     * @param reqPage  分页参数
     * @return 用户分页列表
     */
    @GetMapping("/org/user/{code}")
    Result<PageResult<UserInfo>> queryUserByCode(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("code") String code, @RequestParam("account") String account, @RequestParam("nickname") String nickname, @RequestParam("phone") String phone, @SpringQueryMap ReqPage reqPage);

    /**
     * 根据固定编码获取各级组织ID
     *
     * @param origin    来源
     * @param tenantId  租户ID
     * @param codes     组织固定编码，多个逗号分隔
     * @param direction 查询方向
     * @return 组织ID列表
     */
    @GetMapping("/org/ids")
    Result<List<String>> getOrgIdsByFixCode(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam("tenantId") String tenantId, @RequestParam("codes") String codes, @RequestParam("direction") String direction);

    /**
     * 根据组织ID获取各级组织ID
     *
     * @param origin    来源
     * @param tenantId  租户ID
     * @param orgIds    组织ID，多个逗号分隔
     * @param direction 查询方向
     * @return 组织ID列表
     */
    @GetMapping("/org/ids/byId")
    Result<List<String>> getOrgIdsById(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam("tenantId") String tenantId, @RequestParam("orgIds") String orgIds, @RequestParam("direction") String direction);
}
