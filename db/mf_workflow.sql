/**
  工作流数据库（采用微服务启动时，需要创建工作流数据库，数据表引擎会自动创建）
 */
DROP DATABASE IF EXISTS `mf_workflow`;
CREATE DATABASE  `mf_workflow` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
