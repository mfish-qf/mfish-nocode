# 摸鱼低代码平台后端

[![文档地址](https://img.shields.io/badge/docs-%E6%96%87%E6%A1%A3%E5%9C%B0%E5%9D%80-green)](http://www.mfish.com.cn)
[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/mfish-qf/mfish-nocode/blob/main/LICENSE)
[![Version](https://img.shields.io/badge/version-1.3.2-brightgreen.svg)](https://github.com/mfish-qf/mfish-nocode/releases/tag/v1.3.0)

[![GitHub stars](https://img.shields.io/github/stars/mfish-qf/mfish-nocode.svg?style=social&label=Stars)](https://github.com/mfish-qf/mfish-nocode)
[![GitHub forks](https://img.shields.io/github/forks/mfish-qf/mfish-nocode.svg?style=social&label=Fork)](https://github.com/mfish-qf/mfish-nocode)
[![star](https://gitee.com/qiufeng9862/mfish-nocode/badge/star.svg?theme=white)](https://gitee.com/qiufeng9862/mfish-nocode/stargazers)
[![fork](https://gitee.com/qiufeng9862/mfish-nocode/badge/fork.svg?theme=white)](https://gitee.com/qiufeng9862/mfish-nocode/members)

## 架构图

![](https://mfish-pic.pages.dev/frame.png)

## 项目介绍

摸鱼低代码平台一个不正经的名字，却是一个很正经的项目。希望打造一个基于低代码的无代码平台。既能给程序员使用，也能满足非专业人士的需求。

* 后端基于SpringBoot3, Spring Cloud Alibaba，实现微服务、单体服务代码一体化架构
* 前端采用VUE3+AntDesign
* 注册中心、配置中心采用nacos(作为单体服务时无需使用注册中心)
* 支持oauth2统一认证接入，支持多种登录方式（账号密码登录、手机短信登录、微信扫码登录）
* 支持租户切换，租户可以自己管理自己的人员、组织、角色
* 支持可视化配置查询API接口，后端自动生成SQL执行
* 支持注解方式进行数据权限控制，与业务代码完全解耦

## 安全报告

[![Security Status](https://www.murphysec.com/platform3/v31/badge/1796428877999906816.svg)](https://www.murphysec.com/console/report/1672256253122600960/1796428877999906816)

## 前端源码地址

[![github](https://img.shields.io/badge/前端地址-github-black.svg)](https://github.com/mfish-qf/mfish-nocode-view)
[![gitee](https://img.shields.io/badge/前端地址-gitee-ad312d.svg)](https://gitee.com/qiufeng9862/mfish-nocode-view)
[![gitcode](https://img.shields.io/badge/前端地址-gitcode-be3642.svg)](https://gitcode.com/mfish-qf/mfish-nocode-view.git)

## 后端源码地址

[![github](https://img.shields.io/badge/后端地址-github-black.svg)](https://github.com/mfish-qf/mfish-nocode)
[![gitee](https://img.shields.io/badge/后端地址-gitee-ad312d.svg)](https://gitee.com/qiufeng9862/mfish-nocode)
[![gitcode](https://img.shields.io/badge/后端地址-gitcode-be3642.svg)](https://gitcode.com/mfish-qf/mfish-nocode.git)

## 文档地址

+ [文档地址](http://www.mfish.com.cn)
+ [在线预览](http://app.mfish.com.cn:11119)

## 平台交流

### 微信:

![微信](https://oscimg.oschina.net/oscnet/up-aaf63a91b96c092ad240b2e9755d926ba62.png)

### QQ群:

[![加入QQ群](https://img.shields.io/badge/522792773-blue.svg)](https://jq.qq.com/?_wv=1027&k=0A2bxoZX)
点击链接加入群聊【摸鱼低代码交流群】

## 功能模块

```
├─ChatGpt
│  ├─聊天
├─驾驶舱
│  ├─工作台
├─低代码
│  ├─数据源
│  ├─代码生成
│  ├─自助API
│  └─自助大屏
├─系统管理
│  ├─菜单管理
│  ├─组织管理
│  ├─角色管理
│  ├─帐号管理
│  ├─字典管理
│  ├─分类管理
│  ├─日志管理
│  ├─文件管理
│  ├─在线用户
│  ├─应用管理
│  └─数据库
├─租户管理
│  ├─租户配置
│  ├─个人信息
│  ├─租户信息
│  ├─租户组织
│  ├─租户角色
│  ├─租户人员
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
├─图形编辑器
├─引导页
├─关于
└─其他模块 
   └─更多功能开发中...
```

#### 项目截图

<table>
    <tr>
      <td><img src="https://mfish-pic.pages.dev/login.png" /></td>
      <td><img src="https://mfish-pic.pages.dev/menu.png" /></td>
    </tr>
    <tr>
      <td><img src="https://mfish-pic.pages.dev/screen.png" /></td>
      <td><img src="https://mfish-pic.pages.dev/screenConfig.png" /></td>
    </tr>
    <tr>
      <td><img src="https://mfish-pic.pages.dev/api.png" /></td>
      <td><img src="https://mfish-pic.pages.dev/flow.png" /></td>
    </tr>
    <tr>
      <td><img src="https://mfish-pic.pages.dev/datasource.png" /></td>
      <td><img src="https://mfish-pic.pages.dev/codebuild.png" /></td>
    </tr>
    <tr>
      <td><img src="https://mfish-pic.pages.dev/org.png" /></td>
      <td><img src="https://mfish-pic.pages.dev/role.png" /></td>
    </tr>
    <tr>
      <td><img src="https://mfish-pic.pages.dev/dict.png" /></td>
      <td><img src="https://mfish-pic.pages.dev/category.png" /></td>
    </tr>
    <tr>
      <td><img src="https://mfish-pic.pages.dev/account.png" /></td>
      <td><img src="https://mfish-pic.pages.dev/tenant.png" /></td>
    </tr>
    <tr>
      <td><img src="https://mfish-pic.pages.dev/schedule.png" /></td>
      <td><img src="https://mfish-pic.pages.dev/monitor.png" /></td>
    </tr>
    <tr>
      <td><img src="https://mfish-pic.pages.dev/file.png" /></td>
      <td><img src="https://mfish-pic.pages.dev/log.png" /></td>
    </tr>
</table>

#### 数据库信息

| 文件                 | 描述       |
|--------------------|----------|
| `mf_config.sql`    | nacos数据库 |
| `mf_oauth.sql`     | 认证数据库    |
| `mf_system.sql`    | 系统管理数据库  |
| `mf_scheduler.sql` | 调度中心数据库  |
| `mfish-nocode.sql` | 单实例数据库   |

```
如果单实例使用只需要导入mfish_nocode.sql库即可
如果使用微服务需要导入mf_config.sql、mf_oauth.sql
、mf_system.sql、mf_scheduler.sql四个数据库
```
#### 单实例启动
* 初始化SQL脚本 mfish-nocode.sql
* 在目录mf-start/mf-start-boot中找到MfNoCodeStart启动

#### 微服务启动
* 请查看 https://www.mfish.com.cn

#### swagger访问地址

http://localhost:8888/swagger-ui/index.html
