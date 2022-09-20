package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.oauth.entity.SsoRole;
import cn.com.mfish.oauth.mapper.SsoRoleMapper;
import cn.com.mfish.oauth.service.SsoRoleService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @Date: 2022-09-20
 * @Version: V1.0
 */
@Service
public class SsoRoleServiceImpl extends ServiceImpl<SsoRoleMapper, SsoRole> implements SsoRoleService {

}
