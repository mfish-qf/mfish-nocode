# mfish-nocode
摸鱼低代码平台持续探索中......
## 项目介绍
摸鱼低代码平台希望打造一个基于低代码的无代码平台。即能给程序员使用，也能满足非专业人士的需求。

### 一期目标:
1.基础框架搭建  
2.业务代码自动生成  
3.完成基础系统管理功能   
### 二期目标:
4.通过生成代码快速完成业务管理平台搭建  



* 后端基于Spring Cloud Alibaba
* 注册中心、配置中心采用nacos
* 当前版本完成oauth2统一认证接入
* 只需要认证代码可以查看mfish_oauth2_1.0.0分支
   * *[认证----gitee地址](https://gitee.com/qiufeng9862/mfish-cloud/tree/mfish_oauth2_1.0.0/)*
   * *[认证----github地址](https://github.com/qiufeng9862/mfish-cloud/tree/mfish_oauth2_1.0.0)*
* 持续进行功能完善


#### 数据库信息
<table>
    <tr>
        <td>文件</td>
        <td>描述</td>
    </tr>
    <tr>
        <td>mf_config.sql</td>
        <td>nacos数据库</td>
    </tr>
    <tr>
        <td>mf_oauth.sql</td>
        <td>认证数据库</td>
    </tr>
</table>

#### swagger访问地址
http://localhost:8888/swagger-ui/index.html

