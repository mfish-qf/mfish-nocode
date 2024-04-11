DROP DATABASE IF EXISTS `mf_config`;
CREATE DATABASE  `mf_config` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE `mf_config`;

-- ----------------------------
-- Table structure for config_info
-- ----------------------------
DROP TABLE IF EXISTS `config_info`;
CREATE TABLE `config_info`  (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
                                `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'content',
                                `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'md5',
                                `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'source user',
                                `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'source ip',
                                `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
                                `c_desc` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                `c_use` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                `effect` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                `type` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                `c_schema` text CHARACTER SET utf8 COLLATE utf8_bin NULL,
                                `encrypted_data_key` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '秘钥',
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE INDEX `uk_configinfo_datagrouptenant`(`data_id`, `group_id`, `tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_info' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info
-- ----------------------------
INSERT INTO `config_info` VALUES (1, 'application-dev.yml', 'DEFAULT_GROUP', 'spring:\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n  redis:\n    host: localhost\n    port: 6379\n    password: 1qaz@WSX\n    database: 5\n    timeout: 10s\n    lettuce:\n      pool:\n        # 连接池中的最小空闲连接\n        min-idle: 0\n        # 连接池中的最大空闲连接\n        max-idle: 8\n        # 连接池的最大数据库连接数\n        max-active: 8\n        # #连接池最大阻塞等待时间（使用负值表示没有限制）\n        max-wait: -1ms\n  servlet:\n    multipart:\n      # 单个文件大小\n      max-file-size: 1200MB\n      # 设置总上传的文件大小\n      max-request-size: 1200MB\n  #增加下面属性防止swagger页面无法查看接口，报错no operations defined in spec错误\n  mvc:\n    pathmatch:\n      matching-strategy: ANT_PATH_MATCHER\n# feign 配置\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n  compression:\n    request:\n      enabled: false\n    response:\n      enabled: false\n\n# 暴露监控端点\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \'*\'\n  endpoint:\n    health:\n      show-details: always\nlogging:\n  config: classpath:logback-logstash.xml\nlog:\n  level: info\n  maxSize: 30MB\n# 临时缓存存储时间（单位：天）\ncache:\n  temp:\n    time: 7\nDBConnect:\n  password:\n    privateKey: 54f0e36b98cf63c3bc6185b61c6e4f2a6e1df3bdeb293a50d7b0bc66881c2419\nswagger:\n  title: \'摸鱼低代码平台\'\n  enabled: true\n  description : 摸鱼框架接口文档\n  license : Powered By mfish\n  licenseUrl : http://www.mfish.com.cn\n  version : 版本号:V1.2.0\n  termsOfServiceUrl : http://www.mfish.com.cn\n  contact:\n    name: mfish\n    email: qiufeng9862@qq.com\n    url: http://www.mfish.com.cn\n  needAuth : true\n', '040947504d3653a6da46f5defacbc6a0', '2023-01-11 12:10:11', '2024-01-26 17:23:43', NULL, '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (2, 'mf-gateway-dev.yml', 'DEFAULT_GROUP', 'spring: \n  cloud:\n    gateway:\n      globalcors:\n        cors-configurations:\n          \'[/**]\':\n            max-age: 3600\n            allowed-origin-patterns: \"*\"\n            allowed-headers: \"*\"\n            allow-credentials: true\n            allowed-methods:\n              - GET\n              - POST\n              - DELETE\n              - PUT\n              - OPTION\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: mf-oauth\n          uri: lb://mf-oauth\n          predicates:\n            - Path=/oauth2/**\n          filters:\n            - CacheFilter\n            # 验证码处理\n            - CheckCodeFilter\n            - StripPrefix=1\n        # 测试中心\n        - id: mf-test\n          uri: lb://mf-test\n          predicates:\n            - Path=/test/**\n          filters:\n            - StripPrefix=1\n        - id: mf-sys\n          uri: lb://mf-sys\n          predicates:\n            - Path=/sys/**\n          filters:\n            - StripPrefix=1\n        - id: mf-web\n          uri: lb://mf-web\n          predicates:\n            - Path=/web/**\n          filters:\n            - StripPrefix=1\n        - id: mf-storage\n          uri: lb://mf-storage\n          predicates:\n            - Path=/storage/**\n          filters:\n            - StripPrefix=1\n        - id: mf-scheduler\n          uri: lb://mf-scheduler\n          predicates:\n            - Path=/scheduler/**\n          filters:\n            - StripPrefix=1\n        - id: mf-openai\n          uri: lb://mf-openai\n          predicates:\n            - Path=/ai/**\n          filters:\n            - StripPrefix=1\n        - id: mf-nocode\n          uri: lb://mf-nocode\n          predicates:\n            - Path=/nocode/**\n          filters:\n            - StripPrefix=1\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n    # 网关校验直接返回\n    gatewayCheckCaptcha:\n      # - /oauth2/authorize\n      # 网关校验，自己服务返回\n    selfCheckCaptcha:\n      - /oauth2/authorize?loginType=user_password\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n  # 不校验白名单\n  ignore:\n    whites:\n      - /oauth2/user/revoke\n      - /oauth2/accessToken\n      - /oauth2/authorize\n      - /oauth2/sendMsg\n      - /oauth2/qrCodeLogin/*\n      - /oauth2/wx/bind/**\n      - /oauth2/static/**\n      - /storage/file/*.*\n      - /code/**\n      - /test/**\n      - /*/v2/api-docs', 'b2d5d8a20497930c9b9b86c87f825c1b', '2023-01-11 12:10:11', '2024-04-11 16:02:13', NULL, '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (3, 'mf-oauth-dev.yml', 'DEFAULT_GROUP', 'swagger:\n  title: \'摸鱼认证中心\'\n  needAuth: true\n# mybatis配置\nmybatis-plus:\n  # 搜索指定包别名\n  type-aliases-package: cn.com.mfish.oauth.mapper\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n  mapper-locations: classpath*:cn/com/mfish/**/mapper/**/*Mapper.xml\n  global-config:\n    # 关闭MP3.0自带的banner\n    banner: false\n    db-config:\n      #主键类型  0:\"数据库ID自增\",1:\"该类型为未设置主键类型\", 2:\"用户输入ID\",3:\"全局唯一ID (数字类型唯一ID)\", 4:\"全局唯一ID UUID\",5:\"字符串全局唯一ID (idWorker 的字符串表示)\";\n      #id-type: 0\n      # 默认数据库表下划线命名\n      table-underline: true\n      #逻辑删除 删除前\n      logic-not-delete-value: 0\n      #逻辑删除 删除后\n      logic-delete-value: 1\n  configuration:\n    # 这个配置会将执行的sql打印出来，在开发或测试的时候可以用\n    #log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    # 返回类型为Map,显示null对应的字段\n    call-setters-on-nulls: true\n    map-underscore-to-camel-case: true\n    auto-mapping-behavior: full\n    jdbc-type-for-null:\nspring: \n  thymeleaf:\n    cache: false\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://localhost:3306/mf_oauth?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n      # seata: true    # 开启seata代理，开启后默认每个数据源都代理，如果某个不需要代理可单独关闭\nredisCache:\n  expire: 50400\n  keyPrefix: sso_cache\nredisSession:\n  expire: 50400\n  keyPrefix: sso_session\nshiro:\n  security:\n    algorithmName: md5\n    hashIterations: 2\n    hexEncoded: true\noauth2:\n  expire:\n    code: 180\n    token: 21600\n    refreshToken: 604800\n  login:\n    mutex: false\n  user:\n    autoCreate: false\n  token:\n    sm4key: 143be1ae6ee10b048f7e441cec2a9803\nwechat:\n  miniapp:\n    appId: wx*************e34a3\n    secret: a65e*************fa227', 'bb83f8df9e8633e644574516def8df6e', '2023-01-11 12:10:11', '2024-01-31 23:10:25', NULL, '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (4, 'sentinel-mf-gateway', 'DEFAULT_GROUP', '[\n    {\n        \"resource\": \"mf-oauth\",\n        \"count\": 500,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n    {\n        \"resource\": \"mf-sys\",\n        \"count\": 500,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n    {\n        \"resource\": \"mf-storage\",\n        \"count\": 100,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n    {\n        \"resource\": \"mf-scheduler\",\n        \"count\": 100,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    }\n]', '07a23c3f1d6580115132bd0ba7897504', '2023-01-11 12:10:11', '2024-01-31 23:07:56', NULL, '0:0:0:0:0:0:0:1', '', '', '', '', '', 'text', '', '');
INSERT INTO `config_info` VALUES (6, 'mf-sys-dev.yml', 'DEFAULT_GROUP', 'swagger:\n  title: \'摸鱼系统业务中心\'\n# mybatis配置\nmybatis-plus:\n  # 配置mapper的扫描，找到所有的mapper.xml映射文件\n  mapper-locations: classpath*:cn/com/mfish/sys/mapper/**/*Mapper.xml\n  global-config:\n    # 关闭MP3.0自带的banner\n    banner: false\n    db-config:\n      #主键类型  0:\"数据库ID自增\",1:\"该类型为未设置主键类型\", 2:\"用户输入ID\",3:\"全局唯一ID (数字类型唯一ID)\", 4:\"全局唯一ID UUID\",5:\"字符串全局唯一ID (idWorker 的字符串表示)\";\n      #id-type: 0\n      # 默认数据库表下划线命名\n      table-underline: true\n      #逻辑删除 删除前\n      logic-not-delete-value: 0\n      #逻辑删除 删除后\n      logic-delete-value: 1\n  configuration:\n    # 这个配置会将执行的sql打印出来，在开发或测试的时候可以用\n    #log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    # 返回类型为Map,显示null对应的字段\n    call-setters-on-nulls: true\n    map-underscore-to-camel-case: true\n    auto-mapping-behavior: full\n    jdbc-type-for-null:\nspring: \n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://localhost:3306/mf_system?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n      # seata: true    # 开启seata代理，开启后默认每个数据源都代理，如果某个不需要代理可单独关闭\ncode:\n  template:\n    keys:\n      - ${packageName}/controller/${entityName}Controller.java.ftl\n      - ${packageName}/entity/${entityName}.java.ftl\n      - ${packageName}/mapper/${entityName}Mapper.java.ftl\n      - ${packageName}/mapper/xml/${entityName}Mapper.xml.ftl\n      - ${packageName}/req/Req${entityName}.java.ftl\n      - ${packageName}/service/${entityName}Service.java.ftl\n      - ${packageName}/service/impl/${entityName}ServiceImpl.java.ftl\n      - mfish-nocode-view/src/views/${apiPrefix}/${entityName#kebab-case}/${entityName#uncap_first}.data.ts.ftl\n      - mfish-nocode-view/src/views/${apiPrefix}/${entityName#kebab-case}/${entityName}Modal.vue.ftl\n      - mfish-nocode-view/src/views/${apiPrefix}/${entityName#kebab-case}/index.vue.ftl\n      - mfish-nocode-view/src/api/${apiPrefix}/model/${entityName}Model.ts.ftl\n      - mfish-nocode-view/src/api/${apiPrefix}/${entityName}.ts.ftl\n    path: template\n  savePath: D:/mfish', 'd3c7b0cf049b24cee3d1c584d08aad57', '2023-01-11 12:10:11', '2024-01-31 22:58:53', NULL, '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (7, 'mf-storage-dev.yml', 'DEFAULT_GROUP', 'swagger:\n  title: \'摸鱼文件管理\'\n# mybatis配置\nmybatis-plus:\n  # 配置mapper的扫描，找到所有的mapper.xml映射文件\n  mapper-locations: classpath*:cn/com/mfish/storage/mapper/**/*Mapper.xml\n  global-config:\n    # 关闭MP3.0自带的banner\n    banner: false\n    db-config:\n      #主键类型  0:\"数据库ID自增\",1:\"该类型为未设置主键类型\", 2:\"用户输入ID\",3:\"全局唯一ID (数字类型唯一ID)\", 4:\"全局唯一ID UUID\",5:\"字符串全局唯一ID (idWorker 的字符串表示)\";\n      #id-type: 0\n      # 默认数据库表下划线命名\n      table-underline: true\n      #逻辑删除 删除前\n      logic-not-delete-value: 0\n      #逻辑删除 删除后\n      logic-delete-value: 1\n  configuration:\n    # 这个配置会将执行的sql打印出来，在开发或测试的时候可以用\n    #log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    # 返回类型为Map,显示null对应的字段\n    call-setters-on-nulls: true\n    map-underscore-to-camel-case: true\n    auto-mapping-behavior: full\n    jdbc-type-for-null:\nspring: \n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://localhost:3306/mf_system?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n      # seata: true    # 开启seata代理，开启后默认每个数据源都代理，如果某个不需要代理可单独关闭\nstorage:\n  active: local\n  address: http://localhost:8888/storage/file/\n  local:\n    storagePath: E:/storage\n  aliyun:\n    endpoint: oss-cn-hangzhou.aliyuncs.com\n    accessKeyId: LTAI5***********FsgT\n    accessKeySecret: ZtZGF***************JADjS2f\n    bucketName: landwise\n  qiniu:\n    domain: http://cdn.mfish.com.cn\n    accessKey: cHlX4*******************cdsexgvP\n    secretKey: QPACH*******************1vEa1t4S\n    bucket: mfish-nocode', '53cab25b0ddfae459ab3f5b165052f6d', '2023-01-11 12:10:11', '2023-12-19 11:54:20', NULL, '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (14, 'mf-monitor-dev.yml', 'DEFAULT_GROUP', 'swagger:\n  title: \'摸鱼监控中心\'\nspring: \n  security:\n    user:\n      name: admin\n      password: 123456\n  boot:\n    admin:\n      ui:\n        title: 摸鱼监控中心', 'a674bfe5ede30ec4b46115021971bc4b', '2023-01-26 15:08:30', '2023-01-27 03:58:53', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (15, 'mf-scheduler-dev.yml', 'DEFAULT_GROUP', 'swagger:\n  title: \'摸鱼调度中心\'\n# mybatis配置\nmybatis-plus:\n  # 配置mapper的扫描，找到所有的mapper.xml映射文件\n  mapper-locations: classpath*:cn/com/mfish/scheduler/mapper/**/*Mapper.xml\n  global-config:\n    # 关闭MP3.0自带的banner\n    banner: false\n    db-config:\n      #主键类型  0:\"数据库ID自增\",1:\"该类型为未设置主键类型\", 2:\"用户输入ID\",3:\"全局唯一ID (数字类型唯一ID)\", 4:\"全局唯一ID UUID\",5:\"字符串全局唯一ID (idWorker 的字符串表示)\";\n      #id-type: 0\n      # 默认数据库表下划线命名\n      table-underline: true\n      #逻辑删除 删除前\n      logic-not-delete-value: 0\n      #逻辑删除 删除后\n      logic-delete-value: 1\n  configuration:\n    # 这个配置会将执行的sql打印出来，在开发或测试的时候可以用\n    #log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    # 返回类型为Map,显示null对应的字段\n    call-setters-on-nulls: true\n    map-underscore-to-camel-case: true\n    auto-mapping-behavior: full\n    jdbc-type-for-null:\nspring: \n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://localhost:3306/mf_scheduler?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n      # seata: true    # 开启seata代理，开启后默认每个数据源都代理，如果某个不需要代理可单独关闭\norg:\n  quartz:\n    scheduler:\n      instanceName: MfishClusteredScheduler\n      instanceId: AUTO\n    threadPool:\n      poolClass: org.quartz.simpl.SimpleThreadPool\n      threadCount: 50\n      threadPriority: 5\n    jobStore:\n      jdbcClass: org.quartz.impl.jdbcjobstore.JobStoreTX\n      driverDelegateClass: org.quartz.impl.jdbcjobstore.StdJDBCDelegate\n      useProperties: true\n      isClustered: true\n      tablePrefix: QRTZ_\n      dataSource: mf_scheduler\n      clusterCheckinInterval: 20000\n      misfireThreshold: 60000\n#rocketmq配置信息\nrocketmq:\n  #nameservice服务器地址（多个以英文逗号隔开）\n  name-server: 127.0.0.1:9876\n  #生产者配置\n  producer:\n    #组名\n    group: mfish-scheduler-group\n    sendMessageTimeout: 300000\n    #目的地（topic:tag）\n    #topic\n    topic: mfish-scheduler-topic', 'b09c68c3d9db2babb4b69e3a91f7647e', '2023-02-03 06:47:14', '2023-03-01 07:44:56', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (18, 'mf-web-dev.yml', 'DEFAULT_GROUP', 'swagger:\n  title: \'摸鱼其他业务\'\nmybatis-plus:\n  # 配置mapper的扫描，找到所有的mapper.xml映射文件\n  mapper-locations: classpath*:cn/com/mfish/web/mapper/**/*Mapper.xml\n  global-config:\n    # 关闭MP3.0自带的banner\n    banner: false\n    db-config:\n      #主键类型  0:\"数据库ID自增\",1:\"该类型为未设置主键类型\", 2:\"用户输入ID\",3:\"全局唯一ID (数字类型唯一ID)\", 4:\"全局唯一ID UUID\",5:\"字符串全局唯一ID (idWorker 的字符串表示)\";\n      #id-type: 0\n      # 默认数据库表下划线命名\n      table-underline: true\n      #逻辑删除 删除前\n      logic-not-delete-value: 0\n      #逻辑删除 删除后\n      logic-delete-value: 1\n  configuration:\n    # 这个配置会将执行的sql打印出来，在开发或测试的时候可以用\n    #log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    # 返回类型为Map,显示null对应的字段\n    call-setters-on-nulls: true\n    map-underscore-to-camel-case: true\n    auto-mapping-behavior: full\n    jdbc-type-for-null:\nspring: \n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://localhost:3306/mf_system?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n      # seata: true    # 开启seata代理，开启后默认每个数据源都代理，如果某个不需要代理可单独关闭', 'ade835184bb959e0ffceca1a635d5dd6', '2023-02-03 07:29:24', '2023-02-03 07:36:41', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (42, 'mf-openai-dev.yml', 'DEFAULT_GROUP', 'swagger:\n  title: \'聊天机器人\'\n# mybatis配置\nmybatis-plus:\n  # 配置mapper的扫描，找到所有的mapper.xml映射文件\n  mapper-locations: classpath*:cn/com/mfish/openai/mapper/**/*Mapper.xml\n  global-config:\n    # 关闭MP3.0自带的banner\n    banner: false\n    db-config:\n      #主键类型  0:\"数据库ID自增\",1:\"该类型为未设置主键类型\", 2:\"用户输入ID\",3:\"全局唯一ID (数字类型唯一ID)\", 4:\"全局唯一ID UUID\",5:\"字符串全局唯一ID (idWorker 的字符串表示)\";\n      #id-type: 0\n      # 默认数据库表下划线命名\n      table-underline: true\n      #逻辑删除 删除前\n      logic-not-delete-value: 0\n      #逻辑删除 删除后\n      logic-delete-value: 1\n  configuration:\n    # 这个配置会将执行的sql打印出来，在开发或测试的时候可以用\n    #log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    # 返回类型为Map,显示null对应的字段\n    call-setters-on-nulls: true\n    map-underscore-to-camel-case: true\n    auto-mapping-behavior: full\n    jdbc-type-for-null:\nspring: \n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://localhost:3306/mf_system?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n      # seata: true    # 开启seata代理，开启后默认每个数据源都代理，如果某个不需要代理可单独关闭\nopenai:\n  chat:\n    url: https://api.openai.com/v1/completions\n    token: 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', 'bc1638a50c9259eb8898bffd2fffd0d7', '2023-02-08 03:54:07', '2023-02-09 06:53:59', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (46, 'mf-consume-dev.yml', 'DEFAULT_GROUP', 'swagger:\n  title: \'摸鱼调度中心-MQ消费端测试\'\n#rocketmq配置信息\nrocketmq:\n  #生产者配置\n  consumer:\n    nameServer: 127.0.0.1:9876\n    #topic\n    topic: mfish-scheduler-topic\n    #组名\n    group: mfish-scheduler-group', '644d34b657585db61576939eb3c10360', '2023-03-01 08:49:37', '2023-03-01 09:01:24', 'nacos', '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (47, 'mf-nocode-dev.yml', 'DEFAULT_GROUP', 'swagger:\n  title: \'摸鱼无代码中心\'\nmybatis-plus:\n  # 配置mapper的扫描，找到所有的mapper.xml映射文件\n  mapper-locations: classpath*:cn/com/mfish/nocode/mapper/**/*Mapper.xml\n  global-config:\n    # 关闭MP3.0自带的banner\n    banner: false\n    db-config:\n      #主键类型  0:\"数据库ID自增\",1:\"该类型为未设置主键类型\", 2:\"用户输入ID\",3:\"全局唯一ID (数字类型唯一ID)\", 4:\"全局唯一ID UUID\",5:\"字符串全局唯一ID (idWorker 的字符串表示)\";\n      #id-type: 0\n      # 默认数据库表下划线命名\n      table-underline: true\n      #逻辑删除 删除前\n      logic-not-delete-value: 0\n      #逻辑删除 删除后\n      logic-delete-value: 1\n  configuration:\n    # 这个配置会将执行的sql打印出来，在开发或测试的时候可以用\n    #log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    # 返回类型为Map,显示null对应的字段\n    call-setters-on-nulls: true\n    map-underscore-to-camel-case: true\n    auto-mapping-behavior: full\n    jdbc-type-for-null:\nspring: \n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://localhost:3306/mf_nocode?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n      # seata: true    # 开启seata代理，开启后默认每个数据源都代理，如果某个不需要代理可单独关闭\nstorage:\n  active: local\n  address: http://localhost:8888/storage/file/\n  local:\n    storagePath: E:/storage\n  aliyun:\n    endpoint: oss-cn-hangzhou.aliyuncs.com\n    accessKeyId: LTAI5***********FsgT\n    accessKeySecret: ZtZGF***************JADjS2f\n    bucketName: landwise\n  qiniu:\n    domain: http://cdn.mfish.com.cn\n    accessKey: cHlX4*******************cdsexgvP\n    secretKey: QPACH*******************1vEa1t4S\n    bucket: mfish-nocode', '9e31248e66e26c9469bf7212111e35ba', '2023-07-18 17:12:49', '2023-12-19 11:54:37', NULL, '0:0:0:0:0:0:0:1', '', '', '', '', '', 'yaml', '', '');


-- ----------------------------
-- Table structure for config_info_aggr
-- ----------------------------
DROP TABLE IF EXISTS `config_info_aggr`;
CREATE TABLE `config_info_aggr`  (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                     `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
                                     `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
                                     `datum_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'datum_id',
                                     `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '内容',
                                     `gmt_modified` datetime NOT NULL COMMENT '修改时间',
                                     `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                     `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     UNIQUE INDEX `uk_configinfoaggr_datagrouptenantdatum`(`data_id`, `group_id`, `tenant_id`, `datum_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '增加租户字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info_aggr
-- ----------------------------

-- ----------------------------
-- Table structure for config_info_beta
-- ----------------------------
DROP TABLE IF EXISTS `config_info_beta`;
CREATE TABLE `config_info_beta`  (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                     `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
                                     `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
                                     `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
                                     `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'content',
                                     `beta_ips` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'betaIps',
                                     `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'md5',
                                     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'source user',
                                     `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'source ip',
                                     `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
                                     `encrypted_data_key` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '秘钥',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     UNIQUE INDEX `uk_configinfobeta_datagrouptenant`(`data_id`, `group_id`, `tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_info_beta' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info_beta
-- ----------------------------

-- ----------------------------
-- Table structure for config_info_tag
-- ----------------------------
DROP TABLE IF EXISTS `config_info_tag`;
CREATE TABLE `config_info_tag`  (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                    `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
                                    `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
                                    `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_id',
                                    `tag_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'tag_id',
                                    `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
                                    `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'content',
                                    `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'md5',
                                    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'source user',
                                    `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'source ip',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `uk_configinfotag_datagrouptenanttag`(`data_id`, `group_id`, `tenant_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_info_tag' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info_tag
-- ----------------------------

-- ----------------------------
-- Table structure for config_tags_relation
-- ----------------------------
DROP TABLE IF EXISTS `config_tags_relation`;
CREATE TABLE `config_tags_relation`  (
                                         `id` bigint(20) NOT NULL COMMENT 'id',
                                         `tag_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'tag_name',
                                         `tag_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'tag_type',
                                         `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
                                         `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
                                         `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_id',
                                         `nid` bigint(20) NOT NULL AUTO_INCREMENT,
                                         PRIMARY KEY (`nid`) USING BTREE,
                                         UNIQUE INDEX `uk_configtagrelation_configidtag`(`id`, `tag_name`, `tag_type`) USING BTREE,
                                         INDEX `idx_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_tag_relation' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_tags_relation
-- ----------------------------

-- ----------------------------
-- Table structure for group_capacity
-- ----------------------------
DROP TABLE IF EXISTS `group_capacity`;
CREATE TABLE `group_capacity`  (
                                   `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
                                   `quota` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
                                   `usage` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
                                   `max_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
                                   `max_aggr_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数，，0表示使用默认值',
                                   `max_aggr_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
                                   `max_history_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
                                   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE INDEX `uk_group_id`(`group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '集群、各Group容量信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_capacity
-- ----------------------------

-- ----------------------------
-- Table structure for his_config_info
-- ----------------------------
DROP TABLE IF EXISTS `his_config_info`;
CREATE TABLE `his_config_info`  (
                                    `id` bigint(20) UNSIGNED NOT NULL,
                                    `nid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
                                    `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                    `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                    `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
                                    `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                    `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL,
                                    `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                    `op_type` char(10) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                    `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
                                    `encrypted_data_key` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '秘钥',
                                    PRIMARY KEY (`nid`) USING BTREE,
                                    INDEX `idx_gmt_create`(`gmt_create`) USING BTREE,
                                    INDEX `idx_gmt_modified`(`gmt_modified`) USING BTREE,
                                    INDEX `idx_did`(`data_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '多租户改造' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of his_config_info
-- ----------------------------

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
                                `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                `resource` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                `action` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                UNIQUE INDEX `uk_role_permission`(`role`, `resource`, `action`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permissions
-- ----------------------------

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
                          `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                          `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                          UNIQUE INDEX `idx_user_role`(`username`, `role`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES ('nacos', 'ROLE_ADMIN');

-- ----------------------------
-- Table structure for tenant_capacity
-- ----------------------------
DROP TABLE IF EXISTS `tenant_capacity`;
CREATE TABLE `tenant_capacity`  (
                                    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Tenant ID',
                                    `quota` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
                                    `usage` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
                                    `max_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
                                    `max_aggr_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数',
                                    `max_aggr_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
                                    `max_history_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
                                    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `uk_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '租户容量信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tenant_capacity
-- ----------------------------

-- ----------------------------
-- Table structure for tenant_info
-- ----------------------------
DROP TABLE IF EXISTS `tenant_info`;
CREATE TABLE `tenant_info`  (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                `kp` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'kp',
                                `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_id',
                                `tenant_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_name',
                                `tenant_desc` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'tenant_desc',
                                `create_source` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'create_source',
                                `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
                                `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE INDEX `uk_tenant_info_kptenantid`(`kp`, `tenant_id`) USING BTREE,
                                INDEX `idx_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'tenant_info' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tenant_info
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
                          `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                          `password` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                          `enabled` tinyint(1) NOT NULL,
                          PRIMARY KEY (`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', 1);

SET FOREIGN_KEY_CHECKS = 1;
