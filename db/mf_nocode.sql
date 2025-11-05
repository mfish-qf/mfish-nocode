/**
  自助大屏、自助API相关表（使用自助大屏相关功能需要创建该表）
 */
DROP DATABASE IF EXISTS `mf_nocode`;
CREATE DATABASE  `mf_nocode` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE `mf_nocode`;

-- ----------------------------
-- Table structure for mf_api
-- ----------------------------
DROP TABLE IF EXISTS `mf_api`;
CREATE TABLE `mf_api`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'API名称',
  `folder_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目录id',
  `source_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据来源id',
  `source_type` tinyint(1) NULL DEFAULT NULL COMMENT '数据来源类型 0 数据库 1文件 2API接口',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `source_sql` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据源SQL（原生查询方式存储）',
  `param_flag` tinyint(1) NULL DEFAULT NULL COMMENT '参数标签 0无参 1有参数',
  `config` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'API配置信息',
  `rename_config` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '重命名配置',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标签 0未删除 1已删除',
  `query_type` tinyint(1) NULL DEFAULT NULL COMMENT '查询类型 0 自定义查询 1原生SQL查询',
  `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义API' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mf_api_folder
-- ----------------------------
DROP TABLE IF EXISTS `mf_api_folder`;
CREATE TABLE `mf_api_folder`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `parent_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父节点',
  `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目录名称',
  `folder_sort` int NULL DEFAULT NULL COMMENT '文件夹排序',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标签',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'API目录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mf_api_params
-- ----------------------------
DROP TABLE IF EXISTS `mf_api_params`;
CREATE TABLE `mf_api_params`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `api_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '接口ID',
  `name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '参数名称',
  `default_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '默认值',
  `is_use` tinyint(1) NULL DEFAULT NULL COMMENT '是否使用',
  `remark` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '参数描述',
  `required` tinyint(1) NULL DEFAULT NULL COMMENT '是否必须 0否 1是',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'API请求参数' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mf_file
-- ----------------------------
DROP TABLE IF EXISTS `mf_file`;
CREATE TABLE `mf_file`  (
    `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
    `file_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件key',
    `folder_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目录id',
    `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
    `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名',
    `file_size` int NULL DEFAULT NULL COMMENT '文件大小',
    `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标记(0未删除1删除)',
    `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件数据源表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for mf_file_folder
-- ----------------------------
DROP TABLE IF EXISTS `mf_file_folder`;
CREATE TABLE `mf_file_folder`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `parent_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父节点',
  `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目录名称',
  `folder_sort` int NULL DEFAULT NULL COMMENT '文件夹排序',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标签',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件目录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for mf_formula_info
-- ----------------------------
DROP TABLE IF EXISTS `mf_formula_info`;
CREATE TABLE `mf_formula_info`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一id',
  `category_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目录id',
  `en_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公式名称',
  `cn_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公式中文名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公式总体描述',
  `param_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数描述',
  `return_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '返回结果描述',
  `target_object` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公式对应类',
  `target_param` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公式参数',
  `return_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '返回值类型',
  `display` tinyint(1) NULL DEFAULT 1 COMMENT '是否显示 1显示 0不显示',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '公式信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mf_formula_info
-- ----------------------------
INSERT INTO `mf_formula_info` VALUES ('1', 'char_op', 'str_constant', '字符常量', '增加一个自定义的字符常量', '[\"字符：自定义的字符串\"]', '自定义的字符串', 'MFishStrConstant', '[{\"name\":\"字符\",\"paramType\":\"string\",\"comType\":\"string\",\"value\":\"\"}]', 'string', 1, 1, '', NULL, 'admin', '2025-02-11 23:31:06');
INSERT INTO `mf_formula_info` VALUES ('10', 'num_op', 'round', '小数四舍五入', '对数字进行指定小数位数进行四舍五入', '[\"数字：需要四舍五入的字段或数字\",\"精度：四舍五入后保留几位小数，正数为小数点之后，负数为小数点以前\"]', '四舍五入的结果', 'MFishRound', '[{\"name\":\"数字\",\"paramType\":\"number\",\"comType\":\"list\",\"value\":\"@{fieldName}\"},{\"name\":\"精度\",\"paramType\":\"number\",\"comType\":\"number\",\"value\":\"\"}]', 'number', 1, 2, '', NULL, 'admin', '2025-02-11 23:01:08');
INSERT INTO `mf_formula_info` VALUES ('11', 'num_op', 'abs', '获取绝对值', '获取数字的绝对值', '[\"字段：需要计算绝对值的字段\"]', '绝对值', 'MFishABS', '[{\"name\":\"字段\",\"paramType\":\"number\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'number', 1, 3, '', NULL, 'admin', '2025-02-11 23:01:30');
INSERT INTO `mf_formula_info` VALUES ('12', 'num_op', 'ceil', '向上取整', '对数字进行向上取整', '[\"字段：需要计算向上取整的字段\"]', '向上取整后的值', 'MFishCeil', '[{\"name\":\"字段\",\"paramType\":\"number\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'number', 1, 4, '', NULL, 'admin', '2025-02-11 23:01:48');
INSERT INTO `mf_formula_info` VALUES ('13', 'num_op', 'exp', 'e的指定数量次方', '计算 e 提升到指定数量的次方', '[\"指定数量：需要计算e的指定数量的次方\"]', '返回 e 提升到指定数量的次方', 'MFishExp', '[{\"name\":\"指定数量\",\"paramType\":\"number\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'number', 1, 9, '', NULL, 'admin', '2025-02-11 23:02:32');
INSERT INTO `mf_formula_info` VALUES ('14', 'num_op', 'floor', '向下取整', '对数字进行向下取整', '[\"字段：需要计算向下取整的字段\"]', '向下取整后的值', 'MFishFloor', '[{\"name\":\"字段\",\"paramType\":\"number\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'number', 1, 5, '', NULL, 'admin', '2025-02-11 23:01:56');
INSERT INTO `mf_formula_info` VALUES ('15', 'num_op', 'log', '计算对数', '返回数字的自然对数，或数字的对数到指定的基数', '[\"对数的基：对数的基,若为空则以e为基\",\"字段：需要计算对数的字段\"]', '对数', 'MFishLog', '[{\"name\":\"对数的基\",\"paramType\":\"number\",\"comType\":\"number\",\"value\":\"\"},{\"name\":\"字段\",\"paramType\":\"number\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'number', 1, 10, '', NULL, 'admin', '2025-02-11 23:02:36');
INSERT INTO `mf_formula_info` VALUES ('16', 'char_op', 'lower', '大写转小写', '将字符转化为小写', '[\"字符：需要转小写的字段\"]', '小写字符', 'MFishLower', '[{\"name\":\"字符\",\"paramType\":\"string\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'string', 1, 10, '', NULL, 'admin', '2025-02-11 23:00:19');
INSERT INTO `mf_formula_info` VALUES ('17', 'char_op', 'upper', '小写转大写', '将字段转化为大写', '[\"字段：需要转大写的字段\"]', '大写', 'MFishUpper', '[{\"name\":\"字段\",\"paramType\":\"string\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'string', 1, 11, '', NULL, 'admin', '2025-02-11 23:00:23');
INSERT INTO `mf_formula_info` VALUES ('18', 'num_op', 'power', '数值的x次方', '计算数值的X次方，X为指定数字', '[\"基数：数字的值\",\"指数：数字的幂\"]', '指数', 'MFishPower', '[{\"name\":\"基数\",\"paramType\":\"number\",\"comType\":\"list\",\"value\":\"@{fieldName}\"},{\"name\":\"指数\",\"paramType\":\"number\",\"comType\":\"number\",\"value\":\"\"}]', 'number', 1, 11, '', NULL, 'admin', '2025-02-11 23:02:41');
INSERT INTO `mf_formula_info` VALUES ('19', 'num_op', 'sqrt', '开方', '计算指定数字的开方后的值', '[\"数字：需要开方的数字或字段\"]', '开方后的结果', 'MFishSqrt', '[{\"name\":\"数字\",\"paramType\":\"number\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'number', 1, 12, '', NULL, 'admin', '2025-02-11 23:02:45');
INSERT INTO `mf_formula_info` VALUES ('2', 'num_op', 'num_constant', '数字常量', '返回一个自定义的数字常量', '[\"数字：自定义的数字\"]', '自定义的数字', 'MFishNumConstant', '[{\"name\":\"数字\",\"paramType\":\"number\",\"comType\":\"number\",\"value\":\"\"}]', 'number', 1, 1, '', NULL, 'admin', '2025-02-11 23:00:34');
INSERT INTO `mf_formula_info` VALUES ('20', 'char_op', 'concat', '字符连接', '连接多个字段或常量的值', '[\"字符：需要连接的字符\"]', '连接后的结果', 'MFishConcat', '[{\"name\":\"字符\",\"paramType\":\"list[string]\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'string', 1, 6, '', NULL, 'admin', '2025-02-11 23:31:39');
INSERT INTO `mf_formula_info` VALUES ('21', 'num_op', 'greatest', '返回参数列表的最大值', '获取多个列的数据中最大值', '[\"字段：需要比较大小的字段\"]', '指定的多个列中的最大值', 'MFishGreatest', '[{\"name\":\"字段\",\"paramType\":\"list[number]\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'number', 1, 8, '', NULL, 'admin', '2025-02-11 23:02:27');
INSERT INTO `mf_formula_info` VALUES ('22', 'num_op', 'least', '返回参数列表的最小值', '获取多个列的数据中最小值', '[\"字段：需要比较大小的字段\"]', '指定的多个列中的最小值', 'MFishLeast', '[{\"name\":\"字段\",\"paramType\":\"list[number]\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'number', 1, 7, '', NULL, 'admin', '2025-02-11 23:01:18');
INSERT INTO `mf_formula_info` VALUES ('23', 'date_op', 'now_time', '获取当前时间', '获取制定格式的当前时间', '[\"格式：需要获取的日期格式\"]', '制定格式的当前时间字符串', 'MFishNowTime', '[{\"name\":\"格式\",\"paramType\":\"string\",\"comType\":\"list\",\"value\":\"@{dateTimeFormatPattern}\"}]', 'string', 1, 2, '', NULL, 'admin', '2025-02-11 23:35:18');
INSERT INTO `mf_formula_info` VALUES ('24', 'num_op', 'num2char', '数字转字符', '数字转字符', '[\"字段：需要数字转字符的字段\"]', '指定的数字列转换成字符列', 'MFishNum2Char', '[{\"name\":\"字段\",\"paramType\":\"number\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'string', 1, 6, '', NULL, 'admin', '2025-02-11 23:02:16');
INSERT INTO `mf_formula_info` VALUES ('25', 'char_op', 'char2num', '字符转数字', '字符转数字', '[\"字符：需要转换为数字类型的字符\"]', '指定的字符列转换成数字列', 'MFishChar2Num', '[{\"name\":\"字符\",\"paramType\":\"string\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'number', 1, 12, '', NULL, 'admin', '2025-02-11 23:00:27');
INSERT INTO `mf_formula_info` VALUES ('3', 'char_op', 'substr', '截取字段', '截取字段', '[\"字段：为列名或字符串\",\"起始位置：起始位置从1开始\",\"截取长度：截取字符长度\"]', '截取后的结果', 'MFishSubStr', '[{\"name\":\"字段\",\"paramType\":\"string\",\"comType\":\"list\",\"value\":\"@{fieldName}\"},{\"name\":\"起始位置\",\"paramType\":\"number\",\"comType\":\"number\",\"value\":\"\"},{\"name\":\"截取长度\",\"paramType\":\"number\",\"comType\":\"number\",\"value\":\"\"}]', 'string', 1, 2, '', NULL, 'admin', '2025-02-11 23:31:22');
INSERT INTO `mf_formula_info` VALUES ('30', 'advance_op', 'if', '条件判断', '如果条件为TRUE则返回值，条件为FALSE则返回另一个值', '[\"比较值1：比较条件左边的值\",\"比较条件：两个值进行比较的条件\",\"比较值2：比较条件右边的值（比较条件为空或不为空时该值失效）\",\"满足值：条件满足时显示的值\",\"不满足值：条件不满足时显示的值\"]', '比较之后显示的值', 'MFishIf', '[{\"name\":\"比较值1\",\"paramType\":\"number\",\"comType\":\"list\",\"value\":\"@{fieldName}\"},{\"name\":\"比较条件\",\"paramType\":\"string\",\"comType\":\"list\",\"value\":\"@{operator}\"},{\"name\":\"比较值2\",\"paramType\":\"number\",\"comType\":\"list\",\"value\":\"@{fieldName}\"},{\"name\":\"满足值\",\"paramType\":\"string\",\"comType\":\"string\",\"value\":\"\"},{\"name\":\"不满足值\",\"paramType\":\"string\",\"comType\":\"string\",\"value\":\"\"}]', 'string', 1, 1, '', NULL, 'admin', '2025-02-11 23:40:48');
INSERT INTO `mf_formula_info` VALUES ('31', 'char_op', 'field_get', '获取字段', '字段', '[\"字段：需要显示的字段\"]', '指定的字段列', 'MFishFieldGet', '[{\"name\":\"字段\",\"paramType\":\"string\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'string', 1, 3, '', NULL, 'admin', '2025-02-11 23:31:29');
INSERT INTO `mf_formula_info` VALUES ('4', 'char_op', 'length', '获取字段长度', '获取指定字段长度', '[\"字段：需要计算长度的字段\"]', '字符串长度', 'MFishLength', '[{\"name\":\"字段\",\"paramType\":\"string\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'number', 1, 4, '', NULL, 'admin', '2025-02-11 23:31:34');
INSERT INTO `mf_formula_info` VALUES ('5', 'date_op', 'time_convert', '时间转换', '将时间以不同类型展示', '[\"时间：需要时间转换的字段\",\"类型：时间类型\"]', '时间转换后的结果', 'MFishTimeConvert', '[{\"name\":\"字段\",\"paramType\":\"string\",\"comType\":\"list\",\"value\":\"@{fieldName}\"},{\"name\":\"时间类型\",\"paramType\":\"string\",\"comType\":\"list\",\"value\":\"@{timeType}\"}]', 'string', 1, 1, '', NULL, 'admin', '2025-02-11 23:35:15');
INSERT INTO `mf_formula_info` VALUES ('6', 'char_op', 'trim', '去除左右两边字符串', '去除字段左右两边指定字符串', '[\"条件：选择去除的边 both两边,leading左边,trailing右边,空则去除两边空格\",\"移除字符：需要移除的字符串\",\"字段：需要去除指定字符串的字段\"]', '去除字符串后的结果', 'MFishTrim', '[{\"name\":\"条件\",\"paramType\":\"string\",\"comType\":\"list\",\"value\":\"@{condition}\"},{\"name\":\"移除字符\",\"paramType\":\"string\",\"comType\":\"string\",\"value\":\"\"},{\"name\":\"字段\",\"paramType\":\"string\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'string', 1, 5, '', NULL, 'admin', '2025-02-11 23:31:53');
INSERT INTO `mf_formula_info` VALUES ('7', 'char_op', 'ltrim', '删除字符串左边空格', '删除字符串左边空格', '[\"字符：需要去除左边空格的字符\"]', '去除左空格后的结果', 'MFishLTrim', '[{\"name\":\"字符\",\"paramType\":\"string\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'string', 1, 7, '', NULL, 'admin', '2025-02-11 23:32:02');
INSERT INTO `mf_formula_info` VALUES ('8', 'char_op', 'rtrim', '删除字符串右边空格', '删除字符串右边空格', '[\"字符：需要去除右边空格的字符\"]', '去除右空格后的结果', 'MFishRTrim', '[{\"name\": \"字符\",\"paramType\":\"string\",\"comType\": \"list\",\"value\": \"@{fieldName}\"}]', 'string', 1, 8, '', NULL, '', NULL);
INSERT INTO `mf_formula_info` VALUES ('9', 'char_op', 'replace', '替换指定的字符串', '将字段中的指定字符串替换成其他字符串', '[\"原字符串：需要替换字符的原字符串\",\"被替换字符串：原字符串中需要被替换的字符串\",\"替换字符串：被替换后的字符串\"]', '替换后的字符串', 'MFishReplace', '[{\"name\": \"原字符串\",\"paramType\":\"string\",\"comType\": \"list\",\"value\": \"@{fieldName}\"},{\"name\": \"被替换字符串\",\"paramType\":\"string\",\"comType\": \"string\",\"value\": \"\"},{\"name\": \"替换字符串\",\"paramType\":\"string\",\"comType\":\"string\",\"value\": \"\"}]', 'string', 1, 9, '', NULL, '', NULL);
INSERT INTO `mf_formula_info` VALUES ('a4ed59c802606b0b79cb2d07ba037f4b', 'date_op', 'datediff', '获取日期间隔', '获取两个日期之前的天数（开始日期小于结束日期时为负数）', '[\"开始日期：开始日期，格式yyyy-MM-dd HH:mm:ss\",\"结束日期：结束日期，格式yyyy-MM-dd HH:mm:ss\"]', '日期间隔天数', NULL, '[{\"name\":\"开始日期\",\"paramType\":\"string\",\"comType\":\"list\",\"value\":\"@{fieldName}\"},{\"name\":\"结束日期\",\"paramType\":\"string\",\"comType\":\"list\",\"value\":\"@{fieldName}\"}]', 'number', 1, 3, 'admin', '2025-02-11 15:32:47', 'admin', '2025-02-11 23:41:55');

-- ----------------------------
-- Table structure for mf_screen
-- ----------------------------
DROP TABLE IF EXISTS `mf_screen`;
CREATE TABLE `mf_screen`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '大屏唯一id',
  `folder_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目录id',
  `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `thumbnail` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '大屏缩略图片Key',
  `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `canvas_config` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '画布配置',
  `contains` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '大屏组件容器配置-位置信息(json方式存储)',
  `share_token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分享token',
  `share_end_time` datetime NULL DEFAULT NULL COMMENT '分享结束时间',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标记（0正常 1删除）',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '我的大屏信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mf_screen_charts
-- ----------------------------
DROP TABLE IF EXISTS `mf_screen_charts`;
CREATE TABLE `mf_screen_charts`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一id',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件名称',
  `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属分类（关联树形分类标screen_charts_type）',
  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件类型（用于创建组件）',
  `pic_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片KEY',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `chart_sort` int NULL DEFAULT NULL COMMENT '排序',
  `del_flag` tinyint(1) NULL DEFAULT NULL COMMENT '删除标记',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '组件基础信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mf_screen_charts
-- ----------------------------
INSERT INTO `mf_screen_charts` VALUES ('0217ac88565425f92096dc7138da118d', '折线堆叠图', 'afe955d350d5ac5ef541f145ffa7706f', 'MfLineStack', '9cc9a476f5ab40c2a3adfa058725b942.png', 'ant-design:line-chart-outlined', 4, NULL, 'admin', '2025-01-09 09:58:42', 'admin', '2025-03-07 11:54:50');
INSERT INTO `mf_screen_charts` VALUES ('074710f47f9fa4333ee479ac971a4b18', '标签选项', '688f4f25303f6ce601c99cc30db7dd5b', 'MfSegmented', '33f2a53f8ee54748b6f1c6e1507229fb.png', 'ant-design:select-outlined', 1, NULL, 'admin', '2025-03-17 15:24:50', 'admin', '2025-03-17 23:51:03');
INSERT INTO `mf_screen_charts` VALUES ('09c55daf8c0fade9dba7f1b6d0084f72', '标头2', '4a012bfdcac45a75617c03b9fd0d6b2c', 'MfHeader2', 'ed7bbdd231b04b98833fdaabda4731d8.png', 'carbon:open-panel-top', 2, NULL, 'admin', '2025-01-25 13:12:19', 'admin', '2025-03-07 11:57:40');
INSERT INTO `mf_screen_charts` VALUES ('0bd1545369c287336d3d4c27e80880b2', '圆形雷达图', '3055c9e5ce00720bbba2e7a423eeeaa3', 'MfRadarCircle', '7ff6e866f4af4a5dab7c4e2a8dfd61d2.png', 'ant-design:radar-chart-outlined', 3, NULL, 'admin', '2025-01-10 23:03:55', 'admin', '2025-03-07 11:53:04');
INSERT INTO `mf_screen_charts` VALUES ('109f453e507fd725275790426e966fa1', '普通标签', '6fab782d03d564025274e46a8ec63668', 'MfTag', 'd909cfda85ea4978b5a8075092ed2603.png', 'ant-design:tag-outlined', 1, NULL, 'admin', '2024-11-19 10:41:16', 'admin', '2025-03-07 11:56:24');
INSERT INTO `mf_screen_charts` VALUES ('167a0549cd9e6d7e9b3bc0be0d5648cf', '柱状图', 'fafa6224eb9d3ebe49fe21a20171b05d', 'MfBar', 'd3c4f94e9c2244b098b95cc233419626.png', 'ant-design:bar-chart-outlined', 1, NULL, 'admin', '2024-11-19 16:53:47', 'admin', '2025-03-07 12:05:34');
INSERT INTO `mf_screen_charts` VALUES ('27c284198b6a7f1209b8ba3e47536ac7', '数字液晶屏', '6fab782d03d564025274e46a8ec63668', 'MfDigits', 'bb1b90e27aee466d9f48e409a37e9c60.png', 'ant-design:field-number-outlined', 3, NULL, 'admin', '2025-01-13 11:10:39', 'admin', '2025-03-07 11:56:40');
INSERT INTO `mf_screen_charts` VALUES ('28c94dee47465fe101cf4d370e76c367', '飞线图', '27c665c8c4c8a1ca378b8c2342e75cf6', 'MfMapLine', '5f5951114ddf4118b04857fc886ca8e6.gif', 'carbon:map', 1, NULL, 'admin', '2025-01-26 23:28:30', 'admin', '2025-03-07 11:52:30');
INSERT INTO `mf_screen_charts` VALUES ('2ac89a75bae7941480d5a8c6ccf94f17', '雷达图', '3055c9e5ce00720bbba2e7a423eeeaa3', 'MfRadar', '1f99666fd066426fba8df78e3f462517.png', 'ant-design:radar-chart-outlined', 2, NULL, 'admin', '2025-01-10 15:35:54', 'admin', '2025-03-07 11:52:58');
INSERT INTO `mf_screen_charts` VALUES ('2e4e4f2bd1fca9ffbf6e8cb27ddec074', '边框1', 'fb8d6852d275309854125a152f56e40b', 'MfBorder1', 'a6e5768e301f41a3996cef51061403ca.gif', 'ant-design:border-outlined', 1, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:00:40');
INSERT INTO `mf_screen_charts` VALUES ('32bea5d0030fe6421407296914e45d6f', '标头5', '4a012bfdcac45a75617c03b9fd0d6b2c', 'MfHeader5', 'be104c62da6744c594bb887279b5c938.png', 'carbon:open-panel-top', 5, NULL, 'admin', '2025-01-25 22:05:21', 'admin', '2025-03-07 11:58:28');
INSERT INTO `mf_screen_charts` VALUES ('3f03caa4237bd3d6b6c196dddf67ce90', '标头1', '4a012bfdcac45a75617c03b9fd0d6b2c', 'MfHeader1', 'deffbd4dca264d93a8c409e09d160b36.png', 'carbon:open-panel-top', 1, NULL, 'admin', '2025-01-25 11:39:32', 'admin', '2025-03-07 11:57:31');
INSERT INTO `mf_screen_charts` VALUES ('4bd3f9978e1b37db20c6efb0708430e8', '柱状堆叠图', 'fafa6224eb9d3ebe49fe21a20171b05d', 'MfBarStack', '4d8a041a57074d9d9de1ed69e649788c.png', 'ant-design:bar-chart-outlined', 3, NULL, 'admin', '2025-01-08 15:23:47', 'admin', '2025-03-07 11:55:57');
INSERT INTO `mf_screen_charts` VALUES ('53addf58156af61aad51620ebae425b4', '标头6', '4a012bfdcac45a75617c03b9fd0d6b2c', 'MfHeader6', '44cd6ad4ec524e10b0ea3051e6806555.png', 'carbon:open-panel-top', 6, NULL, 'admin', '2025-01-25 22:05:36', 'admin', '2025-03-07 11:58:36');
INSERT INTO `mf_screen_charts` VALUES ('5c2f9ef1244732e64efee8c7f8feaa9b', '3D飞线图', '27c665c8c4c8a1ca378b8c2342e75cf6', 'MfMapLine3D', 'f0c6f7af407d4e03a99c82fa0bf032e1.gif', 'carbon:map', 2, NULL, 'admin', '2025-01-29 22:59:18', 'admin', '2025-04-14 23:53:05');
INSERT INTO `mf_screen_charts` VALUES ('612bf4c8a5efa599c94662c6aae2c925', '标头3', '4a012bfdcac45a75617c03b9fd0d6b2c', 'MfHeader3', '99f0c0299ead472893ff0847d2f71498.png', 'carbon:open-panel-top', 3, NULL, 'admin', '2025-01-25 13:32:54', 'admin', '2025-03-07 11:57:50');
INSERT INTO `mf_screen_charts` VALUES ('631f1eb885251bda05d193c5d1246ee0', '平滑折线图', 'afe955d350d5ac5ef541f145ffa7706f', 'MfLineSmooth', '2832f4c03d0243898d1b060fddad1ff6.png', 'ant-design:line-chart-outlined', 2, NULL, 'admin', '2025-01-08 12:25:09', 'admin', '2025-03-07 11:54:34');
INSERT INTO `mf_screen_charts` VALUES ('74e749a05ce7da2003d021b4bdcb4762', '条形图', 'fafa6224eb9d3ebe49fe21a20171b05d', 'MfBarHorizontal', '0305185977d745c48786b311177f5f06.png', 'ant-design:bars-outlined', 2, NULL, 'admin', '2025-01-06 17:40:21', 'admin', '2025-03-07 11:55:51');
INSERT INTO `mf_screen_charts` VALUES ('75a5511ab31aa66db386a4c6916e5f0b', '饼状图', '9790b4ccb2e28f0d54c3aaa528ca3fea', 'MfPie', '30532a0099b2444c838acbe1b25e5a5a.png', 'ant-design:pie-chart-outlined', 1, NULL, 'admin', '2024-11-19 16:58:42', 'admin', '2025-03-07 11:53:38');
INSERT INTO `mf_screen_charts` VALUES ('79fa654f78f84ecf24900daf3d602b1e', '环形图', '9790b4ccb2e28f0d54c3aaa528ca3fea', 'MfPieCircular', 'eb6d4ceedc61442e8d6678e649d1d5df.png', 'ant-design:pie-chart-outlined', 2, NULL, 'admin', '2025-01-08 20:43:03', 'admin', '2025-03-07 11:53:44');
INSERT INTO `mf_screen_charts` VALUES ('7e2ba0b33a2a2cbdda6b5bb00a9d51cc', '玫瑰图', '9790b4ccb2e28f0d54c3aaa528ca3fea', 'MfPieRose', '463cf1f93390472e8af2f3721710f483.png', 'ant-design:pie-chart-outlined', 3, NULL, 'admin', '2025-01-08 20:33:43', 'admin', '2025-03-07 11:54:25');
INSERT INTO `mf_screen_charts` VALUES ('8277aec293e04b61e1c438775d6f4638', '折线图', 'afe955d350d5ac5ef541f145ffa7706f', 'MfLine', 'ed31d5a315c64c8bba47285288ec5f1d.png', 'ant-design:line-chart-outlined', 1, NULL, 'admin', '2024-11-19 16:58:18', 'admin', '2025-03-07 11:54:16');
INSERT INTO `mf_screen_charts` VALUES ('8ae2d15251ac3f27a481d9125bfca22e', '散点图', '3055c9e5ce00720bbba2e7a423eeeaa3', 'MfScatter', '92c1974d930145258308d063069bb2e9.png', 'ant-design:dot-chart-outlined', 1, NULL, 'admin', '2025-01-10 10:36:02', 'admin', '2025-03-07 11:52:52');
INSERT INTO `mf_screen_charts` VALUES ('97c8d5db2ceea7b59166ae89d8abecc8', '环形占比图', '8fcbb99d3e9550f5e9d30dd592a67352', 'MfWheel', 'd2b5ef51d32e4691a6df762837035d41.png', 'ant-design:pie-chart-outlined', 1, NULL, 'admin', '2025-01-14 10:13:06', 'admin', '2025-03-07 11:53:21');
INSERT INTO `mf_screen_charts` VALUES ('a4877e2f8a1499569af1ffd985674dd7', '条形占比图', '8fcbb99d3e9550f5e9d30dd592a67352', 'MfTireMarks', 'e509d6c4fe2b45a3b20b3a4d6008d16d.png', 'ant-design:box-plot-outlined', 2, NULL, 'admin', '2025-01-14 17:32:47', 'admin', '2025-03-07 11:53:29');
INSERT INTO `mf_screen_charts` VALUES ('ad0dc6146147b0ba7a1f1cfbc5a92399', '半环形图', '9790b4ccb2e28f0d54c3aaa528ca3fea', 'MfPieHalf', '0b3d6d68a1be406c9fd635a5ad508394.png', 'ant-design:pie-chart-outlined', 4, NULL, 'admin', '2025-01-08 21:05:04', 'admin', '2025-03-07 11:53:53');
INSERT INTO `mf_screen_charts` VALUES ('bc09e7e612fba98581e9f31d2037aa80', '动态数据标签', '6fab782d03d564025274e46a8ec63668', 'MfDataTag', 'dcfaf3b3e9ef4363ad530e5e35fe2a1b.png', 'ant-design:tag-twotone', 2, NULL, 'admin', '2024-12-06 22:25:59', 'admin', '2025-03-07 11:56:31');
INSERT INTO `mf_screen_charts` VALUES ('cc896edeba2020e266454d0689c0f9ff', '面积图', 'afe955d350d5ac5ef541f145ffa7706f', 'MfLineArea', '12b20f74e09043b5947b35c3739114ea.png', 'ant-design:area-chart-outlined', 3, NULL, 'admin', '2025-01-08 15:15:51', 'admin', '2025-03-07 11:54:42');
INSERT INTO `mf_screen_charts` VALUES ('d0f1966383aff7f970caa1016bfda275', '面积堆叠图', 'afe955d350d5ac5ef541f145ffa7706f', 'MfLineAreaStack', '19c3c415e8454078a21106ec4bf98381.png', 'ant-design:line-chart-outlined', 5, NULL, 'admin', '2025-01-09 10:11:24', 'admin', '2025-03-07 11:55:02');
INSERT INTO `mf_screen_charts` VALUES ('d471e414a5de4140d59d64afcaf0c722', '正负条形图', 'fafa6224eb9d3ebe49fe21a20171b05d', 'MfBarPlusMinus', 'f4e2a327d252488b98e92752ff5e0790.png', 'ant-design:bar-chart-outlined', 5, NULL, 'admin', '2025-01-09 11:37:37', 'admin', '2025-03-07 11:56:10');
INSERT INTO `mf_screen_charts` VALUES ('d560a2366a1e7d39b5db2127ef08bb6f', '标头4', '4a012bfdcac45a75617c03b9fd0d6b2c', 'MfHeader4', 'ace51978716c42f3a5858267407e3027.png', 'carbon:open-panel-top', 4, NULL, 'admin', '2025-01-25 21:29:24', 'admin', '2025-03-07 11:58:19');
INSERT INTO `mf_screen_charts` VALUES ('e19f6a61c43711efa95fb03cdc9cfd05', '边框2', 'fb8d6852d275309854125a152f56e40b', 'MfBorder2', 'ad15a0185a2f471fbc1f0c2e5a9fa7b2.png', 'ant-design:border-outlined', 2, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:00:52');
INSERT INTO `mf_screen_charts` VALUES ('e1a049adc43711efa95fb03cdc9cfd05', '边框3', 'fb8d6852d275309854125a152f56e40b', 'MfBorder3', 'c22c7f948e9f4f6a8d794a9e887353b8.png', 'ant-design:border-outlined', 3, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:00:59');
INSERT INTO `mf_screen_charts` VALUES ('e1a0e699c43711efa95fb03cdc9cfd05', '边框4', 'fb8d6852d275309854125a152f56e40b', 'MfBorder4', '4c60e6e128564628a41f0717ca07147e.png', 'ant-design:border-outlined', 4, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:01:07');
INSERT INTO `mf_screen_charts` VALUES ('e1a1835fc43711efa95fb03cdc9cfd05', '边框5', 'fb8d6852d275309854125a152f56e40b', 'MfBorder5', '46547ff44eab4475974ba1154a63b370.png', 'ant-design:border-outlined', 5, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:01:15');
INSERT INTO `mf_screen_charts` VALUES ('e1a20ad2c43711efa95fb03cdc9cfd05', '边框6', 'fb8d6852d275309854125a152f56e40b', 'MfBorder6', 'c0e954e83fee4844b9753352dd5285e7.png', 'ant-design:border-outlined', 6, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:01:23');
INSERT INTO `mf_screen_charts` VALUES ('e468e465106ce5b3518cc84cee249572', '日期时间', 'aa4c61f29c59e7c982ec2255a7ab6316', 'MfDateTime', '579308c0050b478499c29cb1c71628eb.png', 'ant-design:field-time-outlined', 1, NULL, 'admin', '2025-01-11 23:42:30', 'admin', '2025-03-07 11:56:46');
INSERT INTO `mf_screen_charts` VALUES ('e609613c1c85cedb8c2bf393f82de2c9', '折柱混合图', 'fafa6224eb9d3ebe49fe21a20171b05d', 'MfBarLine', '34a0407c9bf04aebbc56a1a9bb70c885.png', 'ant-design:bar-chart-outlined', 4, NULL, 'admin', '2025-01-09 10:50:24', 'admin', '2025-03-07 11:56:04');
INSERT INTO `mf_screen_charts` VALUES ('eb0531ebc45e11efae65b03cdc9cfd05', '装饰1', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration1', 'f2b7e141c5294c80857bb63ee64538db.gif', 'ant-design:skin-outlined', 1, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 11:58:47');
INSERT INTO `mf_screen_charts` VALUES ('eb06df29c45e11efae65b03cdc9cfd05', '装饰2', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration2', '00e9837eabb648038d0fa89bb98c7025.gif', 'ant-design:skin-outlined', 2, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 11:58:56');
INSERT INTO `mf_screen_charts` VALUES ('eb080345c45e11efae65b03cdc9cfd05', '装饰3', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration3', '20f35e75bd794d71833479766d173aa8.gif', 'ant-design:skin-outlined', 3, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 11:59:05');
INSERT INTO `mf_screen_charts` VALUES ('eb0931fac45e11efae65b03cdc9cfd05', '装饰4', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration4', '7b85680707a148b6b03e62415e5efcaa.gif', 'ant-design:skin-outlined', 4, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 11:59:17');
INSERT INTO `mf_screen_charts` VALUES ('eb0a6b3fc45e11efae65b03cdc9cfd05', '装饰5', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration5', '264345057cb64813b3be2ff5bed097ad.gif', 'ant-design:skin-outlined', 5, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 11:59:25');
INSERT INTO `mf_screen_charts` VALUES ('eb0be1bfc45e11efae65b03cdc9cfd05', '装饰6', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration6', 'c99678bbcae845758cbd1416bc69e720.gif', 'ant-design:skin-outlined', 6, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 11:59:33');
INSERT INTO `mf_screen_charts` VALUES ('f1f1b74e7400eda0e46eacefdb3255bf', '滚动表格', 'b5af416292a8659fcd2bc8d58164bf32', 'MfScrollTable', '689726a0e4994843aad420650aead081.gif', 'ant-design:table-outlined', 1, NULL, 'admin', '2025-01-12 23:23:13', 'admin', '2025-03-07 11:56:57');
INSERT INTO `mf_screen_charts` VALUES ('f25f1316d6fc5fd3ebd24ba830f97c1b', '自定义图片', '00491f886354990b6aef2e7a41f397ba', 'MfPicture', 'bb5bb5f720d04596a7dbcfc230e83e02.png', 'ant-design:picture-outlined', 1, NULL, 'admin', '2025-01-23 20:53:39', 'admin', '2025-03-07 11:52:07');
INSERT INTO `mf_screen_charts` VALUES ('aca0f3cfbc9963495240bf3fbb0d0e53', '普通表格', 'b5af416292a8659fcd2bc8d58164bf32', 'MfTable', '63d99e78414c4cc4bf67857f0dd3f5b5.png', 'ant-design:table-outlined', 1, NULL, 'admin', '2025-06-04 17:05:56', 'admin', '2025-06-05 10:31:16');
INSERT INTO `mf_screen_charts` VALUES ('a872143fdfc55e48f076f800cf716ce0', '下拉选择', '688f4f25303f6ce601c99cc30db7dd5b', 'MfSelect', '55b3e5199d4b4488aa62159ad9a9064d.png', 'carbon:list-dropdown', 2, NULL, 'admin', '2025-06-09 10:03:23', 'admin', '2025-07-02 17:47:31');
INSERT INTO `mf_screen_charts` VALUES ('af909fb2f353cda6d6ddbf53ac4091e1', '按钮', '3c94138cae5b3a485858e589618e58d0', 'MfButton', 'c7d4a99f49034feb81aab8f45ee3dca3.png', 'carbon:button-centered', 1, NULL, 'admin', '2025-06-20 17:59:13', 'admin', '2025-07-02 18:00:41');
INSERT INTO `mf_screen_charts` VALUES ('00f7615ab1def50072f5a63d080d379c', '分布图', '27c665c8c4c8a1ca378b8c2342e75cf6', 'MfMapChunks', 'fa8913b0237045cd9fa13999ee1a73ab.png', 'carbon:map', 3, NULL, 'admin', '2025-07-04 11:24:54', 'admin', '2025-07-04 16:14:00');
INSERT INTO `mf_screen_charts` VALUES ('fae4e2667e1309fe194ba967ed085a7f', '气泡图', '27c665c8c4c8a1ca378b8c2342e75cf6', 'MfMapScatter', 'bc3cb95c0bf949138268b565975bcefe.png', 'carbon:map', NULL, NULL, 'admin', '2025-07-04 17:42:46', 'admin', '2025-07-06 00:04:16');
INSERT INTO `mf_screen_charts` VALUES ('bec3552d3f3d7c7eed54ebcaebe8853f', '文本输入框', 'fc74625ae4c3a53cbea0eb7bdd994a9a', 'MfInputTextArea', '15ec81b4946e499ea435b4694724215b.png', 'ant-design:edit-outlined', 2, NULL, 'admin', '2025-07-08 15:52:24', 'admin', '2025-07-08 16:19:07');
INSERT INTO `mf_screen_charts` VALUES ('a9afc424685701a988943027b7598e89', '输入框', 'fc74625ae4c3a53cbea0eb7bdd994a9a', 'MfInput', '9fba91218f694b78aa2d9baa60bb9b2e.png', 'ant-design:edit-outlined', 1, NULL, 'admin', '2025-07-06 23:28:05', 'admin', '2025-07-08 16:18:59');
INSERT INTO `mf_screen_charts` VALUES ('968b8c30bdc48db55596d841cdfac3aa', '浮动按钮', '3c94138cae5b3a485858e589618e58d0', 'MfFloatButton', '304476e8564f469eb1b336a48d2dfb40.png', 'carbon:button-centered', 2, NULL, 'admin', '2025-07-08 23:56:16', 'admin', '2025-07-09 15:52:36');
INSERT INTO `mf_screen_charts` VALUES ('102b65ae7bf9df5d06faa4ae21fa6f0f', '页面容器', 'cf4849acd006faac44fe2c40fae89939', 'MfFrame', '891b4fbb258b406e898e808dda10545b.png', 'ant-design:layout-outlined', 1, NULL, 'admin', '2025-07-10 13:54:51', 'admin', '2025-07-10 23:22:23');

-- ----------------------------
-- Table structure for mf_screen_folder
-- ----------------------------
DROP TABLE IF EXISTS `mf_screen_folder`;
CREATE TABLE `mf_screen_folder`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `parent_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父节点',
  `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目录名称',
  `folder_sort` int NULL DEFAULT NULL COMMENT '文件夹排序',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标签',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '大屏目录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mf_screen_layers
-- ----------------------------
DROP TABLE IF EXISTS `mf_screen_layers`;
CREATE TABLE `mf_screen_layers`  (
  `layer_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '图层ID',
  `screen_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '大屏ID或者模板ID',
  `config` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '组件配置信息(json方式存储)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`layer_id`, `screen_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '我的大屏图层信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mf_screen_layers_apis
-- ----------------------------
DROP TABLE IF EXISTS `mf_screen_layers_apis`;
CREATE TABLE `mf_screen_layers_apis`  (
  `screen_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '大屏ID',
  `layer_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '图层id',
  `api_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'APIID'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '大屏图层api关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mf_screen_relation
-- ----------------------------
DROP TABLE IF EXISTS `mf_screen_relation`;
CREATE TABLE `mf_screen_relation`  (
  `screen_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '大屏ID',
  `child_screen_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '子大屏ID'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '大屏之间关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mf_screen_resource
-- ----------------------------
DROP TABLE IF EXISTS `mf_screen_resource`;
CREATE TABLE `mf_screen_resource`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一id',
  `source_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源id',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '资源名称',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '资源描述',
  `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '资源分类',
  `canvas_config` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '画布配置',
  `pic_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片KEY',
  `contains` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '大屏组件容器配置-位置信息(json方式存储)',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '资源价格',
  `use_count` int NULL DEFAULT NULL COMMENT '使用次数',
  `favorites_count` int NULL DEFAULT NULL COMMENT '收藏次数',
  `click_count` int NULL DEFAULT NULL COMMENT '点击次数',
  `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `audit_state` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '审核状态（0待审核 1审核通过 2审核不通过）',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '大屏资源信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mf_screen_resource_api
-- ----------------------------
DROP TABLE IF EXISTS `mf_screen_resource_api`;
CREATE TABLE `mf_screen_resource_api`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `resource_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '资源id',
  `screen_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '大屏ID',
  `api_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '原API ID',
  `source_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据来源id',
  `source_type` tinyint(1) NULL DEFAULT NULL COMMENT '数据来源类型 0 数据库 1文件 2API接口',
  `source_sql` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据源SQL（原生查询方式存储）',
  `config` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'API配置信息',
  `rename_config` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '重命名配置',
  `query_type` tinyint(1) NULL DEFAULT NULL COMMENT '查询类型 0 自定义查询 1原生SQL查询',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '大屏资源API' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mf_http
-- ----------------------------
DROP TABLE IF EXISTS `mf_http`;
CREATE TABLE `mf_http`  (
                            `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
                            `name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求名称',
                            `request_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求地址',
                            `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求方式（get、post、put、delete）',
                            `content_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求类型',
                            `header_params` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头部参数',
                            `body_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求体参数',
                            `response_map` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '返回结果映射',
                            `folder_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目录id',
                            `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
                            `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标记(0未删除1删除)',
                            `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
                            `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                            `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
                            `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'HTTP请求数据源表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for mf_http_folder
-- ----------------------------
DROP TABLE IF EXISTS `mf_http_folder`;
CREATE TABLE `mf_http_folder`  (
                                   `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
                                   `parent_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父节点',
                                   `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
                                   `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目录名称',
                                   `folder_sort` int NULL DEFAULT NULL COMMENT '文件夹排序',
                                   `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标签',
                                   `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
                                   `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'http接口目录' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
