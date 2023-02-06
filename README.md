摸鱼低代码平台后端
=====================================
当前最新版本： 1.0.0

[![Author](https://img.shields.io/badge/Author-mfish-orange.svg)](http://www.mfish.com.cn)
[![Blog](https://img.shields.io/badge/Blog-个人博客-yellow.svg)](http://www.mfish.com.cn)
[![Version](https://img.shields.io/badge/version-1.0.0-brightgreen.svg)](https://github.com/mfish-qf/mfish-nocode/releases/tag/v1.0.0)
[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/mfish-qf/mfish-nocode/blob/main/LICENSE)

[![GitHub watches](https://img.shields.io/github/watchers/mfish-qf/mfish-nocode.svg?style=social&label=Watch)](https://github.com/mfish-qf/mfish-nocode)
[![GitHub stars](https://img.shields.io/github/stars/mfish-qf/mfish-nocode.svg?style=social&label=Stars)](https://github.com/mfish-qf/mfish-nocode)
[![GitHub forks](https://img.shields.io/github/forks/mfish-qf/mfish-nocode.svg?style=social&label=Fork)](https://github.com/mfish-qf/mfish-nocode)
## 项目介绍
摸鱼低代码平台希望打造一个基于低代码的无代码平台。即能给程序员使用，也能满足非专业人士的需求。
* 后端基于Spring Cloud Alibaba
* 注册中心、配置中心采用nacos
* 当前版本完成oauth2统一认证接入
* 持续进行功能完善

## 前端源码地址
[![github](https://img.shields.io/badge/前端地址-github-red.svg)](https://github.com/mfish-qf/mfish-nocode-view)
[![gitee](https://img.shields.io/badge/前端地址-gitee-lightgrey.svg)](https://gitee.com/qiufeng9862/mfish-nocode-view)

## 演示环境
* *[demo地址](http://app.mfish.com.cn:11119)*

## 功能模块
```
├─驾驶舱
│  ├─工作台
├─系统管理
│  ├─菜单管理
│  ├─组织管理
│  ├─角色管理
│  ├─帐号管理
│  ├─字典管理
│  ├─个人管理
│  └─日志管理
├─系统监控
│  ├─监控中心
├─项目文档
│  ├─接口地址
│  ├─Git地址
│  ├─AntDesign文档
│  └─Vben文档
├─多级目录
├─引导页
├─关于
└─其他模块 
   └─更多功能开发中。。 
   
```

### 一期目标:

1.基础框架搭建  
2.业务代码自动生成  
3.基础权限功能  
4.完成基础系统管理功能  
5.能够通过生成代码快速完成业务管理平台搭建满足程序员采用脚手架快速二开的需求

### 二期目标:

1.真正的低代码平台设计开发....  
......

#### 数据库信息

|文件|描述|
|---|---|
|`mf_config.sql`| nacos数据库 |
|`mf_oauth.sql`| 认证数据库 |
|`mf_system.sql`| 系统管理数据库 |

#### swagger访问地址

http://localhost:8888/swagger-ui/index.html
