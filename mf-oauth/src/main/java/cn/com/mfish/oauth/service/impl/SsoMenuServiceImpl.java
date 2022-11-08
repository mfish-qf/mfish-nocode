package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.oauth.entity.SsoMenu;
import cn.com.mfish.oauth.mapper.SsoMenuMapper;
import cn.com.mfish.oauth.service.SsoMenuService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;

/**
 * @Description: 菜单权限表
 * @Author: mfish
 * @Date: 2022-09-21
 * @Version: V1.0
 */
@Service
public class SsoMenuServiceImpl extends ServiceImpl<SsoMenuMapper, SsoMenu> implements SsoMenuService {

    @Override
    public boolean insertMenu(SsoMenu ssoMenu) {
        if(StringUtils.isEmpty(ssoMenu.getParentId())){
            ssoMenu.setParentId("");
        }
        return baseMapper.insertMenu(ssoMenu) == 1;
    }
}
