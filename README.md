# 摸鱼低代码平台后端

[![文档地址](https://img.shields.io/badge/docs-%E6%96%87%E6%A1%A3%E5%9C%B0%E5%9D%80-green)](http://www.mfish.com.cn)
[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/mfish-qf/mfish-nocode/blob/main/LICENSE)
[![Version](https://img.shields.io/badge/version-2.0.0-brightgreen.svg)](https://github.com/mfish-qf/mfish-nocode/releases/tag/v2.0.0)

[![GitHub stars](https://img.shields.io/github/stars/mfish-qf/mfish-nocode.svg?style=social&label=Stars)](https://github.com/mfish-qf/mfish-nocode)
[![GitHub forks](https://img.shields.io/github/forks/mfish-qf/mfish-nocode.svg?style=social&label=Fork)](https://github.com/mfish-qf/mfish-nocode)
[![star](https://gitee.com/qiufeng9862/mfish-nocode/badge/star.svg?theme=white)](https://gitee.com/qiufeng9862/mfish-nocode/stargazers)
[![fork](https://gitee.com/qiufeng9862/mfish-nocode/badge/fork.svg?theme=white)](https://gitee.com/qiufeng9862/mfish-nocode/members)

## 🧱架构图

![](http://oscimg.oschina.net/AiCreationDetail/up-49fe6faf5fa60eefe9a5c2e0fa65c797.png)

## 🐟项目介绍

摸鱼低代码平台，是一款致力于 **让开发像摸鱼一样轻松** 的低代码/无代码平台。我们希望打破技术门槛，让程序员和非程序员都能快速构建业务系统，提升效率，释放创造力。

这不仅是程序员偷闲时的效率神器，更是职场小白的建站利器，甚至是领导画原型的秘密武器！

## 🚀核心特点
低代码 + 无代码统一平台：灵活切换，按需使用

即可快速生成业务代码，也可以无代码生成API接口和可视化大屏

单实例微服务一体化架构：支持单体服务和微服务两种开发部署模式，一套代码解决两种架构，开箱即用

权限解耦：企业级的权限控制，安全可靠与业务代码完全解耦，通过注解控制权限

## 🎯适用场景
企业内部系统搭建（ERP、CRM、OA等）

快速原型设计与验证

数据展示看板等轻应用开发快速集成

## 💡技术栈
* 后端基于SpringBoot4, Spring Cloud Alibaba，实现微服务、单体服务代码一体化架构
* 前端采用VUE3+AntDesign
* 注册中心、配置中心采用nacos(作为单体服务时无需使用注册中心)
* 支持oauth2统一认证接入，支持多种登录方式（账号密码登录、手机短信登录、微信扫码登录）
* 支持租户切换，租户可以自己管理自己的人员、组织、角色
* 支持可视化配置查询API接口，后端自动生成SQL执行
* 支持注解方式进行数据权限控制，与业务代码完全解耦

## 🧩功能模块

```

├─驾驶舱
│  ├─工作台
├─低代码
│  ├─自助大屏
│  ├─自助API
│  ├─数据源
│  └─代码生成
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
├─审批管理
│  ├─审批列表
│  ├─我的申请
│  └─流程管理
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
│  ├─开发文档
│  ├─Gitee地址
│  ├─Github地址
│  └─AntDesign文档
├─使用样例
│  ├─上传下载
│  ├─导入导出
│  ├─多级目录
│  ├─主子表
│  └─数据权限
├─流程表单
│  ├─流程图
│  └─表单设计器
├─关于
└─其他模块 
   └─更多功能开发中...
```
## 🛡️安全报告

[![Security Status](https://www.murphysec.com/platform3/v31/badge/1796428877999906816.svg)](https://www.murphysec.com/console/report/1672256253122600960/1796428877999906816)

## 🌐前端源码地址

[![github](https://img.shields.io/badge/前端地址-github-black.svg)](https://github.com/mfish-qf/mfish-nocode-view)
[![gitee](https://img.shields.io/badge/前端地址-gitee-ad312d.svg)](https://gitee.com/qiufeng9862/mfish-nocode-view)
[![gitcode](https://img.shields.io/badge/前端地址-gitcode-be3642.svg)](https://gitcode.com/mfish-qf/mfish-nocode-view.git)

## 🌐后端源码地址

[![github](https://img.shields.io/badge/后端地址-github-black.svg)](https://github.com/mfish-qf/mfish-nocode)
[![gitee](https://img.shields.io/badge/后端地址-gitee-ad312d.svg)](https://gitee.com/qiufeng9862/mfish-nocode)
[![gitcode](https://img.shields.io/badge/后端地址-gitcode-be3642.svg)](https://gitcode.com/mfish-qf/mfish-nocode.git)

## 📖文档地址

+ [文档地址](http://www.mfish.com.cn)
+ [在线预览](http://app.mfish.com.cn:11119)

## 🎭平台交流

### 微信:

![微信](https://oscimg.oschina.net/oscnet/up-aaf63a91b96c092ad240b2e9755d926ba62.png)

### QQ群:
[![加入QQ2群](https://img.shields.io/badge/QQ2%E7%BE%A4-289877815-blue.svg)](https://qm.qq.com/q/zQdI2rMsj8)

点击链接加入群聊【摸鱼低代码2群】

[![加入QQ1群](https://img.shields.io/badge/QQ1%E7%BE%A4-522792773--已满-blue--已满)](https://jq.qq.com/?_wv=1027&k=0A2bxoZX)

点击链接加入群聊【摸鱼低代码1群】（已满）

### 大屏配置教学
[1.自助大屏配置系列-画布操作](https://www.bilibili.com/video/BV14YLbz7ESh/?share_source=copy_web&vd_source=0cf425790dc7750eb5d8a4d1c0b028f4)

[2.自助大屏配置系列-画布配置](https://www.bilibili.com/video/BV15CLnzBEWN/?share_source=copy_web&vd_source=0cf425790dc7750eb5d8a4d1c0b028f4)

[3.自助大屏配置系列-数据绑定](https://www.bilibili.com/video/BV1Mr5KzSE6V/?share_source=copy_web&vd_source=0cf425790dc7750eb5d8a4d1c0b028f4)

[4.自助大屏配置系列-动画绑定](https://www.bilibili.com/video/BV1K1JNzdEG1/?share_source=copy_web&vd_source=0cf425790dc7750eb5d8a4d1c0b028f4)

[5.自助大屏配置系列-多屏联动](https://www.bilibili.com/video/BV1kvjHzdEok/?share_source=copy_web&vd_source=0cf425790dc7750eb5d8a4d1c0b028f4)

[6.自助大屏配置系列-动态显隐](https://www.bilibili.com/video/BV1CkrNBWEsj/?share_source=copy_web&vd_source=0cf425790dc7750eb5d8a4d1c0b028f4)

[7.自助大屏配置系列-表单查询](https://www.bilibili.com/video/BV1efzsB4Ebq/?share_source=copy_web&vd_source=0cf425790dc7750eb5d8a4d1c0b028f4)

### 项目截图

<table>
    <tr>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-88ced73c1f0228af408c50349497540b.png" /></td>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-c77cdb0b34e61b837cbfc22e5ad5acb7.png" /></td>
    </tr>
    <tr>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-ee4f2a35788db09fcc6e281cf171f8c1.jpg" /></td>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-6e87424a0defbdb968a21f9bc42a610b.jpg" /></td>
    </tr>
    <tr>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-0d6f1ff813c700191c34536a1d84bf6d.jpg" /></td>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-5586c6c0e7600f96fe62eed1e2a558ad.jpg" /></td>
    </tr>
    <tr>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-51a14b3a3d460c40a5cad78404420d78.png" /></td>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-a8003812c6f8895b1f52a9005742f521.png" /></td>
    </tr>
    <tr>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-68939a2ada4ed26028beebaa6cabac04.png" /></td>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-1e29d5d79f2176d1b54c06778010ab1a.png" /></td>
    </tr>
    <tr>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-a01321e2df1ead79c96e552b108b5332.png" /></td>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-444b8574b9965a5f031febde2337f0e6.png" /></td>
    </tr>
    <tr>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-1c749d25a5a7cf3f6d881ac252ff4a45.png" /></td>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-e12d1cf33ec7bed102e42d60306c13bb.png" /></td>
    </tr>
    <tr>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-aae99916987fd56a5106571ad16499c6.png" /></td>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-e62a5748e91eaf1909cb7f27a7c3b4a5.png" /></td>
    </tr>
    <tr>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-3cf5951020da20b3e7ccf445fadfc0c3.png" /></td>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-7bb21a0253bd19d4172331ec2472b277.jpg" /></td>
    </tr>
    <tr>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-7fa88472a42caa99f6606697b1b0a250.png" /></td>
      <td><img src="http://oscimg.oschina.net/AiCreationDetail/up-6723cb1d005c5760fdc2c004c379a8b4.png" /></td>
    </tr>
</table>

### 数据库信息

| 文件                 | 描述                           |
|--------------------|------------------------------|
| `mf_config.sql`    | nacos数据库                     |
| `mf_oauth.sql`     | 认证数据库                        |
| `mf_system.sql`    | 系统管理数据库                      |
| `mf_scheduler.sql` | 调度中心数据库                      |
| `mf_nocode.sql`    | 低代码中心数据库                     |
| `mf_workflow.sql`  | 工作流数据库                       |
| `mf_demo.sql`      | 样例中心数据库(非必须)                 |
| `mfish_nocode.sql` | 单实例数据库<br />`单实例启动只需要执行这个脚本` |

```
如果单实例使用只需要导入mfish_nocode.sql库即可
如果使用微服务需要导入mf_config.sql、mf_oauth.sql
、mf_system.sql、mf_scheduler.sql、mf_nocode.sql、mf_workflow.sql、mf_demo.sql等数据库
```
### 单实例启动
* 初始化SQL脚本 mfish-nocode.sql
* 在目录mf-start/mf-start-boot中找到MfNoCodeStart启动

### 微服务启动
* 请查看 https://www.mfish.com.cn

### swagger访问地址

* http://localhost:8888/swagger-ui/index.html

### 账号密码
+ 账号：admin
+ 密码：!QAZ2wsx
