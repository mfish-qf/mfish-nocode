# 摸鱼低代码平台后端

[![文档地址](https://img.shields.io/badge/docs-%E6%96%87%E6%A1%A3%E5%9C%B0%E5%9D%80-green)](http://www.mfish.com.cn)
[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/mfish-qf/mfish-nocode/blob/main/LICENSE)
[![Version](https://img.shields.io/badge/version-1.1.0-brightgreen.svg)](https://github.com/mfish-qf/mfish-nocode/releases/tag/v1.1.0)

[![GitHub stars](https://img.shields.io/github/stars/mfish-qf/mfish-nocode.svg?style=social&label=Stars)](https://github.com/mfish-qf/mfish-nocode)
[![GitHub forks](https://img.shields.io/github/forks/mfish-qf/mfish-nocode.svg?style=social&label=Fork)](https://github.com/mfish-qf/mfish-nocode)
[![star](https://gitee.com/qiufeng9862/mfish-nocode/badge/star.svg?theme=white)](https://gitee.com/qiufeng9862/mfish-nocode/stargazers)
[![fork](https://gitee.com/qiufeng9862/mfish-nocode/badge/fork.svg?theme=white)](https://gitee.com/qiufeng9862/mfish-nocode/members)

## 架构图
![](https://oscimg.oschina.net/oscnet/up-63e6a3ba5667370d5bf2ef4d9401e007972.png)

## 项目介绍
摸鱼低代码平台希望打造一个基于低代码的无代码平台。即能给程序员使用，也能满足非专业人士的需求。
* 后端基于Spring Cloud Alibaba
* 注册中心、配置中心采用nacos
* 当前版本完成oauth2统一认证接入
* 持续进行功能完善

## 前端源码地址
[![github](https://img.shields.io/badge/前端地址-github-red.svg)](https://github.com/mfish-qf/mfish-nocode-view)
[![gitee](https://img.shields.io/badge/前端地址-gitee-lightgrey.svg)](https://gitee.com/qiufeng9862/mfish-nocode-view)

## 文档地址
+ [文档地址](http://www.mfish.com.cn)
+ [在线预览](http://app.mfish.com.cn:11119)

## 平台交流

### 微信:
![微信](https://oscimg.oschina.net/oscnet/up-aaf63a91b96c092ad240b2e9755d926ba62.png)

### QQ群:
[![加入QQ群](https://img.shields.io/badge/522792773-blue.svg)](https://jq.qq.com/?_wv=1027&k=0A2bxoZX) 点击链接加入群聊【摸鱼低代码交流群】
## 功能模块
```
├─ChatGpt
│  ├─聊天
├─驾驶舱
│  ├─工作台
├─系统管理
│  ├─菜单管理
│  ├─组织管理
│  ├─角色管理
│  ├─帐号管理
│  ├─字典管理
│  ├─日志管理
│  ├─文件管理
│  ├─在线用户
│  ├─数据库
│  └─数据源
├─租户管理
│  ├─租户配置
│  ├─租户信息
│  │  ├─租户组织
│  │  ├─租户角色
│  │  ├─租户人员
│  ├─个人信息
├─系统监控
│  ├─监控中心
├─任务调度
│  ├─任务管理
│  ├─任务日志
├─项目文档
│  ├─接口地址
│  ├─Github地址
│  ├─Gitee地址
│  ├─AntDesign文档
│  └─Vben文档
├─多级目录
├─系统工具
│  ├─代码生成
├─图形编辑器
├─引导页
├─关于
└─其他模块 
   └─更多功能开发中。。

```

### 一期目标:(脚手架完成)

1.基础框架搭建  
2.业务代码自动生成  
3.基础权限功能  
4.完成基础系统管理功能  
5.能够通过生成代码快速完成业务管理平台搭建满足程序员采用脚手架快速二开的需求

### 二期目标:
1.自助API功能（通过选项方式自动生成API接口）  
2.真正的低代码平台设计开发....  
......

#### 项目截图

![](https://oscimg.oschina.net/oscnet/up-7b8b53019b36fb12f5a0388491f7cedb06a.png)

![](https://oscimg.oschina.net/oscnet/up-cb060c85cfc867df4ea6c1be4ac65d64d74.png)

![](https://oscimg.oschina.net/oscnet/up-93645a610cf9dd0266580e0870ff497b946.png)

![](https://oscimg.oschina.net/oscnet/up-4f34924c18c4f5df0fb7823feef7431227d.png)

![](https://oscimg.oschina.net/oscnet/up-57d93c91b93340387c44d5d30e984e914d7.png)

![](https://oscimg.oschina.net/oscnet/up-0ff2d7b640896b9a9156af832baebcb313f.png)

![](https://oscimg.oschina.net/oscnet/up-81d9f856cdd794843d172c47874b69ff503.png)

![](https://oscimg.oschina.net/oscnet/up-c26c5a79214ed2e242512d0f5f4accca63b.png)

![](https://oscimg.oschina.net/oscnet/up-36d63fb4e8dd0a0844ff64a8f4c28682296.png)

![](https://oscimg.oschina.net/oscnet/up-434781fa769d2da21e396bfccbbe13c8f15.png)

![](https://oscimg.oschina.net/oscnet/up-7b2eeb5e679f75d889a841de61f9845c026.png)

![](https://oscimg.oschina.net/oscnet/up-c413a81f353a0175bbbd09cc32a7fb8d5bf.png)

![](https://oscimg.oschina.net/oscnet/up-736398ce5030ce21b6dda45ba9f24af4a72.png)
#### 数据库信息

|文件|描述|
|---|---|
|`mf_config.sql`| nacos数据库 |
|`mf_oauth.sql`| 认证数据库 |
|`mf_system.sql`| 系统管理数据库 |
|`mf_scheduler.sql`| 调度中心数据库 |

#### swagger访问地址

http://localhost:8888/swagger-ui/index.html
