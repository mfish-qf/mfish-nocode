package cn.com.mfish.sys.service.impl;

import cn.com.mfish.sys.entity.DbConnect;
import cn.com.mfish.sys.mapper.DbConnectMapper;
import cn.com.mfish.sys.service.DbConnectService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @description: 数据库连接
 * @author: mfish
 * @date: 2023-03-13
 * @version: V1.0.0
 */
@Service
public class DbConnectServiceImpl extends ServiceImpl<DbConnectMapper, DbConnect> implements DbConnectService {

}
