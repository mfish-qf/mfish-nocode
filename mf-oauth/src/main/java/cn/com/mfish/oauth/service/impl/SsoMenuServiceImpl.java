package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.oauth.entity.SsoMenu;
import cn.com.mfish.oauth.mapper.SsoMenuMapper;
import cn.com.mfish.oauth.service.SsoMenuService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 菜单权限表
 * @Author: mfish
 * @Date: 2022-09-20
 * @Version: V1.0
 */
@Service
public class SsoMenuServiceImpl extends ServiceImpl<SsoMenuMapper, SsoMenu> implements SsoMenuService {

}
