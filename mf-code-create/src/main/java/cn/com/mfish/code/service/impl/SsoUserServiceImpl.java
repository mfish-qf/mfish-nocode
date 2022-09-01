package cn.com.mfish.code.service.impl;

import cn.com.mfish.code.entity.SsoUser;
import cn.com.mfish.code.mapper.SsoUserMapper;
import cn.com.mfish.code.service.SsoUserService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 用户信息
 * @Author: mfish
 * @Date: 2022-09-01
 * @Version: V1.0
 */
@Service
public class SsoUserServiceImpl extends ServiceImpl<SsoUserMapper, SsoUser> implements SsoUserService {

}
