# PG数据库适配修改
postgresql数据库已完全适配，有需要请联系作者...

# 数据库信息

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