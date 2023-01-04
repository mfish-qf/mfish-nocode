package cn.com.mfish.oauth.mapper;

import cn.com.mfish.oauth.entity.SsoOrg;
import cn.com.mfish.oauth.req.ReqSsoOrg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 组织结构表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.0.0
 */
public interface SsoOrgMapper extends BaseMapper<SsoOrg> {
    int insertOrg(SsoOrg ssoOrg);

    Integer queryMaxOrgLevel(@Param("reqSsoOrg") ReqSsoOrg reqSsoOrg);

    List<SsoOrg> queryOrg(@Param("reqSsoOrg") ReqSsoOrg reqSsoOrg, @Param("levels") List<Integer> levels);
}
