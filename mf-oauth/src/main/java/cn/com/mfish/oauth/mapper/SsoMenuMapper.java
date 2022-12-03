package cn.com.mfish.oauth.mapper;

import cn.com.mfish.oauth.entity.SsoMenu;
import cn.com.mfish.oauth.req.ReqSsoMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 菜单权限表
 * @Author: mfish
 * @Date: 2022-09-21
 * @Version: V1.0
 */
public interface SsoMenuMapper extends BaseMapper<SsoMenu> {
    int insertMenu(SsoMenu ssoMenu);

    Integer queryMaxMenuLevel(@Param("reqSsoMenu") ReqSsoMenu reqSsoMenu, @Param("userId") String userId);

    List<SsoMenu> queryMenu(@Param("reqSsoMenu") ReqSsoMenu reqSsoMenu, @Param("levels") List<Integer> levels, @Param("userId") String userId);
}
