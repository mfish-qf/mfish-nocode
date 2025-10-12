/**
  单实例启动需要初始化的表（如果采用单实例启动，只需要创建这个数据库，其他微服务相关数据库无需创建）
 */
DROP DATABASE IF EXISTS `mfish_nocode`;
CREATE DATABASE  `mfish_nocode` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE `mfish_nocode`;

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers`  (
                                       `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                       `trigger_name` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                       `trigger_group` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                       `blob_data` blob NULL,
                                       PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                       INDEX `sched_name`(`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                       CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`  (
                                   `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                   `calendar_name` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                   `calendar` blob NOT NULL,
                                   PRIMARY KEY (`sched_name`, `calendar_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers`  (
                                       `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                       `trigger_name` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                       `trigger_group` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                       `cron_expression` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                       `time_zone_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                       PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                       CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_cron_triggers
-- ----------------------------
INSERT INTO `qrtz_cron_triggers` VALUES ('MfishClusteredScheduler', '7027f822890a46d49f59e400ddba810d', 'DEFAULT', '0 1 * ? * *', 'Asia/Shanghai');
INSERT INTO `qrtz_cron_triggers` VALUES ('MfishClusteredScheduler', '9d44af0c2f604c7fa355902ea4a9485c', 'DFAULT', '0 * * ? * *', 'Asia/Shanghai');

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers`  (
                                        `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                        `entry_id` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                        `trigger_name` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                        `trigger_group` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                        `instance_name` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                        `fired_time` bigint NOT NULL,
                                        `sched_time` bigint NOT NULL,
                                        `priority` int NOT NULL,
                                        `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                        `job_name` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                        `job_group` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                        `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                        `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                        PRIMARY KEY (`sched_name`, `entry_id`) USING BTREE,
                                        INDEX `idx_qrtz_ft_trig_inst_name`(`sched_name`, `instance_name`) USING BTREE,
                                        INDEX `idx_qrtz_ft_inst_job_req_rcvry`(`sched_name`, `instance_name`, `requests_recovery`) USING BTREE,
                                        INDEX `idx_qrtz_ft_j_g`(`sched_name`, `job_name`, `job_group`) USING BTREE,
                                        INDEX `idx_qrtz_ft_jg`(`sched_name`, `job_group`) USING BTREE,
                                        INDEX `idx_qrtz_ft_t_g`(`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                        INDEX `idx_qrtz_ft_tg`(`sched_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details`  (
                                     `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `job_name` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `job_group` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `job_class_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `is_durable` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `is_update_data` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `job_data` blob NULL,
                                     PRIMARY KEY (`sched_name`, `job_name`, `job_group`) USING BTREE,
                                     INDEX `idx_qrtz_j_req_recovery`(`sched_name`, `requests_recovery`) USING BTREE,
                                     INDEX `idx_qrtz_j_grp`(`sched_name`, `job_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_job_details
-- ----------------------------
INSERT INTO `qrtz_job_details` VALUES ('MfishClusteredScheduler', '任务调度测试:[895baafe9caa9afd089f54d7c3932a25]', 'DEFAULT', '', 'cn.com.mfish.scheduler.execute.DisallowConcurrentJobExecute', '1', '1', '0', '0', 0x230D0A2353756E204170722032372031323A30313A30342043535420323032350D0A6A6F625F646174615F6D61703D7B22616C6C6F77436F6E63757272656E74225C3A302C22636C6173734E616D65225C3A22636E2E636F6D2E6D666973682E636F6E73756D652E6A6F622E436F6E73756D65724A6F62222C226964225C3A223839356261616665396361613961666430383966353464376333393332613235222C226A6F6247726F7570225C3A2244454641554C54222C226A6F624E616D65225C3A225C75344546425C75353241315C75384330335C75354541365C75364434425C7538424435222C226A6F6254797065225C3A322C226C6F6754797065225C3A302C226D6574686F644E616D65225C3A2274657374222C226D69736669726548616E646C6572225C3A312C22706172616D73225C3A22222C227072696F72697479225C3A312C2272656D61726B225C3A22222C22737461747573225C3A302C2273756273637269626573225C3A5B7B2263726F6E225C3A222035202A202A203F202A202A222C22656E6454696D65225C3A22323032352D30332D30312031375C3A30325C3A3238222C226964225C3A223038616434623132383831323466346239373132363439623837376161353536222C226A6F624964225C3A223839356261616665396361613961666430383966353464376333393332613235222C22737461727454696D65225C3A22323032332D30332D30312031375C3A30325C3A3238222C22737461747573225C3A307D5D2C2274696D655A6F6E65225C3A22417369612F5368616E67686169222C227570646174654279225C3A2261646D696E222C2275706461746554696D65225C3A22323032352D30342D32372031325C3A30315C3A3034227D0D0A);
INSERT INTO `qrtz_job_details` VALUES ('MfishClusteredScheduler', '本地调用测试:[908c3637e3c9914eb97f4576f187d668]', 'DFAULT', '本地单个参数测试', 'cn.com.mfish.scheduler.execute.DisallowConcurrentJobExecute', '1', '1', '0', '0', 0x230D0A2353756E204170722032372031323A30343A35382043535420323032350D0A6A6F625F646174615F6D61703D7B22616C6C6F77436F6E63757272656E74225C3A302C22636C6173734E616D65225C3A22636E2E636F6D2E6D666973682E7363686564756C65722E6A6F622E4D664A6F62222C226964225C3A223930386333363337653363393931346562393766343537366631383764363638222C226A6F6247726F7570225C3A22444641554C54222C226A6F624E616D65225C3A225C75363732435C75353733305C75384330335C75373532385C75364434425C7538424435222C226A6F6254797065225C3A302C226C6F6754797065225C3A302C226D6574686F644E616D65225C3A2274657374222C226D69736669726548616E646C6572225C3A312C22706172616D73225C3A225B5C5C6E20205C5C225C75363732435C75353733305C75364434425C75384244355C5C225C5C6E5D222C227072696F72697479225C3A312C2272656D61726B225C3A225C75363732435C75353733305C75353335355C75344532415C75353343325C75363537305C75364434425C7538424435222C22737461747573225C3A302C2273756273637269626573225C3A5B7B2263726F6E225C3A2230202A202A203F202A202A222C22656E6454696D65225C3A22323032362D30322D32342032335C3A30315C3A3531222C226964225C3A223964343461663063326636303463376661333535393032656134613934383563222C226A6F624964225C3A223930386333363337653363393931346562393766343537366631383764363638222C22737461727454696D65225C3A22323032332D30322D32342032335C3A30315C3A3531222C22737461747573225C3A307D5D2C2274696D655A6F6E65225C3A22417369612F5368616E67686169222C227570646174654279225C3A2261646D696E222C2275706461746554696D65225C3A22323032352D30342D32372031325C3A30345C3A3538227D0D0A);
INSERT INTO `qrtz_job_details` VALUES ('MfishClusteredScheduler', '类参数测试:[4ce14a9ba39b771cae9245b737396eb9]', 'DEFAULT', '', 'cn.com.mfish.scheduler.execute.DisallowConcurrentJobExecute', '1', '1', '0', '0', 0x230D0A2353756E204170722032372031323A30313A30372043535420323032350D0A6A6F625F646174615F6D61703D7B22616C6C6F77436F6E63757272656E74225C3A302C22636C6173734E616D65225C3A22636E2E636F6D2E6D666973682E7363686564756C65722E6A6F622E4D664A6F62222C226964225C3A223463653134613962613339623737316361653932343562373337333936656239222C226A6F6247726F7570225C3A2244454641554C54222C226A6F624E616D65225C3A225C75374337425C75353343325C75363537305C75364434425C7538424435222C226A6F6254797065225C3A302C226C6F6754797065225C3A302C226D6574686F644E616D65225C3A2274657374222C226D69736669726548616E646C6572225C3A312C22706172616D73225C3A225B5C5C6E20207B5C5C6E202020205C5C22747970655C5C225C3A205C5C226A6176612E6C616E672E537472696E675C5C222C5C5C6E202020205C5C2276616C75655C5C225C3A205C5C22696E6E65725C5C225C5C6E20207D2C5C5C6E20207B5C5C6E202020205C5C22747970655C5C225C3A205C5C22636E2E636F6D2E6D666973682E7379732E6170692E656E746974792E5379734C6F675C5C222C5C5C6E202020205C5C2276616C75655C5C225C3A207B5C5C6E2020202020205C5C227469746C655C5C225C3A205C5C22626262625C5C225C5C6E202020207D5C5C6E20207D5C5C6E5D222C227072696F72697479225C3A312C2272656D61726B225C3A22222C22737461747573225C3A302C2273756273637269626573225C3A5B7B2263726F6E225C3A22302031202A203F202A202A222C22656E6454696D65225C3A22323032362D30342D30322031385C3A30355C3A3430222C226964225C3A223730323766383232383930613436643439663539653430306464626138313064222C226A6F624964225C3A223463653134613962613339623737316361653932343562373337333936656239222C22737461727454696D65225C3A22323032342D30342D30322031385C3A30355C3A3430222C22737461747573225C3A307D5D2C2274696D655A6F6E65225C3A22417369612F5368616E67686169222C227570646174654279225C3A2261646D696E222C2275706461746554696D65225C3A22323032352D30342D32372031325C3A30315C3A3037227D0D0A);
INSERT INTO `qrtz_job_details` VALUES ('MfishClusteredScheduler', '远程调用测试:[357b8d7dac67eaf0a1a0ce369ec25462]', 'DEFAULT', '远程调用用户接口测试', 'cn.com.mfish.scheduler.execute.DisallowConcurrentJobExecute', '1', '1', '0', '0', 0x230D0A2353756E204170722032372031323A30313A30302043535420323032350D0A6A6F625F646174615F6D61703D7B22616C6C6F77436F6E63757272656E74225C3A302C22636C6173734E616D65225C3A22636E2E636F6D2E6D666973682E636F6D6D6F6E2E6F617574682E6170692E72656D6F74652E52656D6F74655573657253657276696365222C226964225C3A223335376238643764616336376561663061316130636533363965633235343632222C226A6F6247726F7570225C3A2244454641554C54222C226A6F624E616D65225C3A225C75384644435C75374130425C75384330335C75373532385C75364434425C7538424435222C226A6F6254797065225C3A312C226C6F6754797065225C3A312C226D6574686F644E616D65225C3A226765745573657242794964222C226D69736669726548616E646C6572225C3A312C22706172616D73225C3A225B5C5C6E20205C5C22696E6E65725C5C222C5C5C6E20205C5C22315C5C225C5C6E5D222C227072696F72697479225C3A312C2272656D61726B225C3A225C75384644435C75374130425C75384330335C75373532385C75373532385C75363233375C75363341355C75353345335C75364434425C7538424435222C22737461747573225C3A302C2273756273637269626573225C3A5B7B2263726F6E225C3A2231202A202A203F202A202A222C22656E6454696D65225C3A22323032352D30322D32362031375C3A32315C3A3233222C226964225C3A223263633033623765306436343438356161313332373865323939353336306264222C226A6F624964225C3A223335376238643764616336376561663061316130636533363965633235343632222C22737461727454696D65225C3A22323032332D30322D32362031375C3A32315C3A3233222C22737461747573225C3A307D2C7B2263726F6E225C3A223330202A202A203F202A202A222C22656E6454696D65225C3A22323032352D30322D32342032335C3A30395C3A3330222C226964225C3A223833373735303038373838373463303362346534623234626634376532313264222C226A6F624964225C3A223335376238643764616336376561663061316130636533363965633235343632222C22737461727454696D65225C3A22323032332D30322D32342032335C3A30395C3A3330222C22737461747573225C3A307D5D2C2274696D655A6F6E65225C3A22417369612F5368616E67686169222C227570646174654279225C3A2261646D696E222C2275706461746554696D65225C3A22323032352D30342D32372031325C3A30315C3A3030227D0D0A);

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks`  (
                               `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                               `lock_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                               PRIMARY KEY (`sched_name`, `lock_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------
INSERT INTO `qrtz_locks` VALUES ('MfishClusteredScheduler', 'STATE_ACCESS');
INSERT INTO `qrtz_locks` VALUES ('MfishClusteredScheduler', 'TRIGGER_ACCESS');

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps`  (
                                             `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                             `trigger_group` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                             PRIMARY KEY (`sched_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state`  (
                                         `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                         `instance_name` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                         `last_checkin_time` bigint NOT NULL,
                                         `checkin_interval` bigint NOT NULL,
                                         PRIMARY KEY (`sched_name`, `instance_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------
INSERT INTO `qrtz_scheduler_state` VALUES ('MfishClusteredScheduler', 'qiufeng1745726800959', 1745733346536, 20000);

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers`  (
                                         `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                         `trigger_name` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                         `trigger_group` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                         `repeat_count` bigint NOT NULL,
                                         `repeat_interval` bigint NOT NULL,
                                         `times_triggered` bigint NOT NULL,
                                         PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                         CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_simple_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers`  (
                                          `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                          `trigger_name` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                          `trigger_group` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                          `str_prop_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                          `str_prop_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                          `str_prop_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                          `int_prop_1` int NULL DEFAULT NULL,
                                          `int_prop_2` int NULL DEFAULT NULL,
                                          `long_prop_1` bigint NULL DEFAULT NULL,
                                          `long_prop_2` bigint NULL DEFAULT NULL,
                                          `dec_prop_1` decimal(13, 4) NULL DEFAULT NULL,
                                          `dec_prop_2` decimal(13, 4) NULL DEFAULT NULL,
                                          `bool_prop_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                          `bool_prop_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                          PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                          CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_simprop_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers`  (
                                  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                  `trigger_name` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                  `trigger_group` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                  `job_name` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                  `job_group` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                  `next_fire_time` bigint NULL DEFAULT NULL,
                                  `prev_fire_time` bigint NULL DEFAULT NULL,
                                  `priority` int NULL DEFAULT NULL,
                                  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                  `start_time` bigint NOT NULL,
                                  `end_time` bigint NULL DEFAULT NULL,
                                  `calendar_name` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                  `misfire_instr` smallint NULL DEFAULT NULL,
                                  `job_data` blob NULL,
                                  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                  INDEX `idx_qrtz_t_j`(`sched_name`, `job_name`, `job_group`) USING BTREE,
                                  INDEX `idx_qrtz_t_jg`(`sched_name`, `job_group`) USING BTREE,
                                  INDEX `idx_qrtz_t_c`(`sched_name`, `calendar_name`) USING BTREE,
                                  INDEX `idx_qrtz_t_g`(`sched_name`, `trigger_group`) USING BTREE,
                                  INDEX `idx_qrtz_t_state`(`sched_name`, `trigger_state`) USING BTREE,
                                  INDEX `idx_qrtz_t_n_state`(`sched_name`, `trigger_name`, `trigger_group`, `trigger_state`) USING BTREE,
                                  INDEX `idx_qrtz_t_n_g_state`(`sched_name`, `trigger_group`, `trigger_state`) USING BTREE,
                                  INDEX `idx_qrtz_t_next_fire_time`(`sched_name`, `next_fire_time`) USING BTREE,
                                  INDEX `idx_qrtz_t_nft_st`(`sched_name`, `trigger_state`, `next_fire_time`) USING BTREE,
                                  INDEX `idx_qrtz_t_nft_misfire`(`sched_name`, `misfire_instr`, `next_fire_time`) USING BTREE,
                                  INDEX `idx_qrtz_t_nft_st_misfire`(`sched_name`, `misfire_instr`, `next_fire_time`, `trigger_state`) USING BTREE,
                                  INDEX `idx_qrtz_t_nft_st_misfire_grp`(`sched_name`, `misfire_instr`, `next_fire_time`, `trigger_group`, `trigger_state`) USING BTREE,
                                  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_triggers
-- ----------------------------
INSERT INTO `qrtz_triggers` VALUES ('MfishClusteredScheduler', '7027f822890a46d49f59e400ddba810d', 'DEFAULT', '类参数测试:[4ce14a9ba39b771cae9245b737396eb9]', 'DEFAULT', '暂无描述', 1745733660000, 1745730060000, 1, 'WAITING', 'CRON', 1712052340000, 1775124340000, NULL, 1, 0x230D0A2353756E204170722032372031323A30313A30372043535420323032350D0A6A6F625F646174615F6D61703D7B22616C6C6F77436F6E63757272656E74225C3A302C22636C6173734E616D65225C3A22636E2E636F6D2E6D666973682E7363686564756C65722E6A6F622E4D664A6F62222C226964225C3A223463653134613962613339623737316361653932343562373337333936656239222C226A6F6247726F7570225C3A2244454641554C54222C226A6F624E616D65225C3A225C75374337425C75353343325C75363537305C75364434425C7538424435222C226A6F6254797065225C3A302C226C6F6754797065225C3A302C226D6574686F644E616D65225C3A2274657374222C226D69736669726548616E646C6572225C3A312C22706172616D73225C3A225B5C5C6E20207B5C5C6E202020205C5C22747970655C5C225C3A205C5C226A6176612E6C616E672E537472696E675C5C222C5C5C6E202020205C5C2276616C75655C5C225C3A205C5C22696E6E65725C5C225C5C6E20207D2C5C5C6E20207B5C5C6E202020205C5C22747970655C5C225C3A205C5C22636E2E636F6D2E6D666973682E7379732E6170692E656E746974792E5379734C6F675C5C222C5C5C6E202020205C5C2276616C75655C5C225C3A207B5C5C6E2020202020205C5C227469746C655C5C225C3A205C5C22626262625C5C225C5C6E202020207D5C5C6E20207D5C5C6E5D222C227072696F72697479225C3A312C2272656D61726B225C3A22222C22737461747573225C3A302C2273756273637269626573225C3A5B7B2263726F6E225C3A22302031202A203F202A202A222C22656E6454696D65225C3A22323032362D30342D30322031385C3A30355C3A3430222C226964225C3A223730323766383232383930613436643439663539653430306464626138313064222C226A6F624964225C3A223463653134613962613339623737316361653932343562373337333936656239222C22737461727454696D65225C3A22323032342D30342D30322031385C3A30355C3A3430222C22737461747573225C3A307D5D2C2274696D655A6F6E65225C3A22417369612F5368616E67686169222C227570646174654279225C3A2261646D696E222C2275706461746554696D65225C3A22323032352D30342D32372031325C3A30315C3A3037227D0D0A);
INSERT INTO `qrtz_triggers` VALUES ('MfishClusteredScheduler', '9d44af0c2f604c7fa355902ea4a9485c', 'DFAULT', '本地调用测试:[908c3637e3c9914eb97f4576f187d668]', 'DFAULT', '暂无描述', 1745733360000, 1745733300000, 1, 'WAITING', 'CRON', 1677250911000, 1771945311000, NULL, 1, 0x230D0A2353756E204170722032372031323A30343A35382043535420323032350D0A6A6F625F646174615F6D61703D7B22616C6C6F77436F6E63757272656E74225C3A302C22636C6173734E616D65225C3A22636E2E636F6D2E6D666973682E7363686564756C65722E6A6F622E4D664A6F62222C226964225C3A223930386333363337653363393931346562393766343537366631383764363638222C226A6F6247726F7570225C3A22444641554C54222C226A6F624E616D65225C3A225C75363732435C75353733305C75384330335C75373532385C75364434425C7538424435222C226A6F6254797065225C3A302C226C6F6754797065225C3A302C226D6574686F644E616D65225C3A2274657374222C226D69736669726548616E646C6572225C3A312C22706172616D73225C3A225B5C5C6E20205C5C225C75363732435C75353733305C75364434425C75384244355C5C225C5C6E5D222C227072696F72697479225C3A312C2272656D61726B225C3A225C75363732435C75353733305C75353335355C75344532415C75353343325C75363537305C75364434425C7538424435222C22737461747573225C3A302C2273756273637269626573225C3A5B7B2263726F6E225C3A2230202A202A203F202A202A222C22656E6454696D65225C3A22323032362D30322D32342032335C3A30315C3A3531222C226964225C3A223964343461663063326636303463376661333535393032656134613934383563222C226A6F624964225C3A223930386333363337653363393931346562393766343537366631383764363638222C22737461727454696D65225C3A22323032332D30322D32342032335C3A30315C3A3531222C22737461747573225C3A307D5D2C2274696D655A6F6E65225C3A22417369612F5368616E67686169222C227570646174654279225C3A2261646D696E222C2275706461746554696D65225C3A22323032352D30342D32372031325C3A30345C3A3538227D0D0A);
-- -------------------------------------------------
-- 上面表为QRTZ的表结构,下面表为mfish-nocode自定义的表结构
-- -------------------------------------------------
-- ----------------------------
-- Table structure for qrtz_job
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job`;
CREATE TABLE `qrtz_job` (
                            `id` varchar(32) NOT NULL COMMENT '任务ID',
                            `job_name` varchar(100) NOT NULL DEFAULT '' COMMENT '任务名称',
                            `job_group` varchar(100) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组',
                            `job_type` tinyint(1) DEFAULT NULL COMMENT '任务类型(0 本地任务 1 RPC远程调用任务 2 MQ消息任务)',
                            `class_name` varchar(100) NOT NULL COMMENT '类名称',
                            `method_name` varchar(100) DEFAULT NULL COMMENT '方法名称',
                            `params` varchar(500) DEFAULT NULL COMMENT '调用参数',
                            `allow_concurrent` tinyint(1) DEFAULT '0' COMMENT '允许并发执行（0不允许 1允许）',
                            `misfire_handler` tinyint(1) DEFAULT '1' COMMENT '过期策略（1立即执行一次 2放弃执行 ）',
                            `status` tinyint(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
                            `time_zone` varchar(50) DEFAULT NULL COMMENT '时区',
                            `priority` int(11) DEFAULT NULL COMMENT '优先级',
                            `remark` varchar(1000) DEFAULT '' COMMENT '备注信息',
                            `log_type` tinyint(1) DEFAULT '0' COMMENT '日志类型(0入库日志 1文件日志)',
                            `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                            PRIMARY KEY (`id`,`job_name`,`job_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='定时调度任务';

-- ----------------------------
-- Records of qrtz_job
-- ----------------------------
INSERT INTO `qrtz_job` VALUES ('357b8d7dac67eaf0a1a0ce369ec25462', '远程调用测试', 'DEFAULT', 1, 'cn.com.mfish.common.oauth.api.remote.RemoteUserService', 'getUserById', '[\n  \"inner\",\n  \"1\"\n]', 0, 1, 0, 'Asia/Shanghai', 1, '远程调用用户接口测试', 1, 'admin', '2023-02-24 23:09:49', 'admin', '2023-05-20 12:05:59');
INSERT INTO `qrtz_job` VALUES ('895baafe9caa9afd089f54d7c3932a25', '任务调度测试', 'DEFAULT', 2, 'cn.com.mfish.consume.job.ConsumerJob', 'test', '', 0, 1, 1, 'Asia/Shanghai', 1, '', 0, 'admin', '2023-02-26 19:58:23', 'admin', '2023-05-20 11:46:18');
INSERT INTO `qrtz_job` VALUES ('908c3637e3c9914eb97f4576f187d668', '本地调用测试', 'DFAULT', 0, 'cn.com.mfish.scheduler.job.MfJob', 'test', '[\n  \"本地测试\"\n]', 0, 1, 0, 'Asia/Shanghai', 1, '本地单个参数测试', 1, 'admin', '2023-02-24 23:02:42', 'admin', '2023-05-20 12:22:29');

-- ----------------------------
-- Table structure for qrtz_job_log
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_log`;
CREATE TABLE `qrtz_job_log` (
                                `id` varchar(36) NOT NULL COMMENT '唯一ID',
                                `job_id` varchar(36) NOT NULL COMMENT '任务ID',
                                `subscribe_id` varchar(36) DEFAULT NULL COMMENT '订阅ID',
                                `job_name` varchar(100) NOT NULL DEFAULT '' COMMENT '任务名称',
                                `job_group` varchar(100) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组',
                                `job_type` tinyint(1) DEFAULT NULL COMMENT '任务类型(0 本地任务 1 RPC远程调用任务 2 MQ消息任务)',
                                `class_name` varchar(100) NOT NULL COMMENT '类名称',
                                `method_name` varchar(100) DEFAULT NULL COMMENT '方法名称',
                                `params` varchar(500) DEFAULT NULL COMMENT '调用参数',
                                `cron` varchar(50) DEFAULT NULL COMMENT 'cron表达式',
                                `start_time` datetime DEFAULT NULL COMMENT '开始时间',
                                `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                                `log_type` tinyint(1) DEFAULT '0' COMMENT '日志类型(0入库日志 1文件日志)',
                                `cost_time` int(11) DEFAULT NULL COMMENT '耗时(单位:ms)',
                                `status` tinyint(1) DEFAULT '0' COMMENT '执行状态（0开始 1调度成功 2调度失败 3执行成功 4执行失败）',
                                `remark` varchar(1000) DEFAULT NULL COMMENT '备注信息',
                                `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                PRIMARY KEY (`id`,`job_name`,`job_group`) USING BTREE,
                                KEY `create_time_index` (`create_time`) USING BTREE COMMENT '创建时间索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='定时任务调度日志';

-- ----------------------------
-- Table structure for qrtz_job_subscribe
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_subscribe`;
CREATE TABLE `qrtz_job_subscribe`  (
                                       `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
                                       `job_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务ID',
                                       `cron` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'cron表达式',
                                       `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
                                       `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
                                       `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态（0正常 1暂停）',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务订阅表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_job_subscribe
-- ----------------------------
INSERT INTO `qrtz_job_subscribe` VALUES ('08ad4b1288124f4b9712649b877aa556', '895baafe9caa9afd089f54d7c3932a25', ' 5 * * ? * *', '2023-03-01 17:02:28', '2025-03-01 17:02:28', 0);
INSERT INTO `qrtz_job_subscribe` VALUES ('2cc03b7e0d64485aa13278e2995360bd', '357b8d7dac67eaf0a1a0ce369ec25462', '1 * * ? * *', '2023-02-26 17:21:23', '2025-02-26 17:21:23', 0);
INSERT INTO `qrtz_job_subscribe` VALUES ('8377500878874c03b4e4b24bf47e212d', '357b8d7dac67eaf0a1a0ce369ec25462', '30 * * ? * *', '2023-02-24 23:09:30', '2025-02-24 23:09:30', 0);
INSERT INTO `qrtz_job_subscribe` VALUES ('9d44af0c2f604c7fa355902ea4a9485c', '908c3637e3c9914eb97f4576f187d668', '0 * * ? * *', '2023-02-24 23:01:51', '2025-02-24 23:01:51', 1);

-- ----------------------------
-- Table structure for sso_client_details
-- ----------------------------
DROP TABLE IF EXISTS `sso_client_details`;
CREATE TABLE `sso_client_details`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `client_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端ID',
  `client_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端名称',
  `resource_ids` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端能访问的资源集合',
  `client_secret` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端密钥',
  `scope` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指定客户端权限范围',
  `grant_types` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '认证方式 授权码模式:authorization_code,密码模式:password,刷新token: refresh_token',
  `redirect_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端重定向url，authorization_code认证回调地址',
  `authorities` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指定用户的权限范围',
  `auto_approve` tinyint(1) NULL DEFAULT NULL COMMENT '跳过授权页,默认true,适用于authorization_code模式',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标记(0未删除1删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户端信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_client_details
-- ----------------------------
INSERT INTO `sso_client_details` VALUES ('1', 'system', '系统', NULL, 'system', 'all', 'authorization_code,password,refresh_token', 'http://localhost:\\d+/oauth2.*', NULL, 1, NULL, NULL, 'admin', '2023-10-13 16:02:05', 0);

-- ----------------------------
-- Table structure for sso_menu
-- ----------------------------
DROP TABLE IF EXISTS `sso_menu`;
CREATE TABLE `sso_menu`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单ID',
  `parent_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '父菜单ID',
  `menu_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单编码',
  `menu_level` tinyint(4) NULL DEFAULT NULL COMMENT '菜单级别',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `menu_icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `menu_sort` int(4) NULL DEFAULT 0 COMMENT '菜单顺序',
  `menu_type` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '菜单类型（0目录 1菜单 2按钮）',
  `route_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `permissions` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识(多个标识逗号隔开)',
  `is_external` tinyint(1) NULL DEFAULT 0 COMMENT '是否为外部链接（1是 0否）',
  `is_visible` tinyint(1) NULL DEFAULT 1 COMMENT '菜单状态（1显示 0隐藏）',
  `active_menu` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '当前激活菜单(当菜单隐藏时，设置当前激活的菜单项)',
  `is_keepalive` tinyint(1) NULL DEFAULT NULL COMMENT '是否缓存(1是 0否)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '描述',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_menu_code`(`menu_code`) USING BTREE COMMENT '菜单编码索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_menu
-- ----------------------------
INSERT INTO `sso_menu` VALUES ('503e3ac379a2e17e99105b77a727e6db', '', '00001', 1, '驾驶舱', 'ant-design:appstore-outlined', 1, 0, '/dashboard', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2022-11-08 16:53:57', 'admin', '2024-09-03 21:52:16');
INSERT INTO `sso_menu` VALUES ('76f68d05f5054818762718ee85d6d0fe', '503e3ac379a2e17e99105b77a727e6db', '0000100001', 2, '工作台', 'ant-design:calendar-outlined', 1, 1, '/workbench', '/dashboard/workbench/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-03-10 17:15:10', 'test', '2023-06-23 20:12:21');
INSERT INTO `sso_menu` VALUES ('268d140daddc00dc77823c7d7c2025fb', '76f68d05f5054818762718ee85d6d0fe', '000010000100001', 3, '查询', '#', 1, 2, '', NULL, 'sys:workbench:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:15:10', 'admin', '2024-04-03 10:19:23');
INSERT INTO `sso_menu` VALUES ('234dc900ad6502579a51784f9ddb05d5', '76f68d05f5054818762718ee85d6d0fe', '000010000100002', 3, '新增', '#', 2, 2, '', NULL, 'sys:workbench:insert', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:15:10', '', NULL);
INSERT INTO `sso_menu` VALUES ('6a38a3847b66cc690c3a2eacedb4e81f', '76f68d05f5054818762718ee85d6d0fe', '000010000100003', 3, '修改', '#', 3, 2, '', NULL, 'sys:workbench:update', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:15:10', '', NULL);
INSERT INTO `sso_menu` VALUES ('7e87849f80699ad24292fd9908f5aeb8', '76f68d05f5054818762718ee85d6d0fe', '000010000100004', 3, '删除', '#', 4, 2, '', NULL, 'sys:workbench:delete', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:15:10', 'admin', '2023-03-10 17:15:39');
INSERT INTO `sso_menu` VALUES ('2a4e024fdc76063da32926c63ca9ead2', '', '00002', 1, '系统管理', 'ant-design:setting-outlined', 3, 0, '/system', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2022-11-08 16:59:57', 'admin', '2023-09-01 15:13:58');
INSERT INTO `sso_menu` VALUES ('a988f38821885f8f8aaffa49d681aaac', '2a4e024fdc76063da32926c63ca9ead2', '0000200001', 2, '菜单管理', 'ion:ios-menu', 1, 1, '/menu', '/sys/menu/index.vue', '', 0, 1, NULL, 1, '', 'admin', '2022-11-08 17:02:02', 'admin', '2024-08-21 18:26:59');
INSERT INTO `sso_menu` VALUES ('75882dc140444e061741fbd9f026dd2b', 'a988f38821885f8f8aaffa49d681aaac', '000020000100001', 3, '查询', '#', 1, 2, '', NULL, 'sys:menu:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 17:04:16', 'admin', '2022-11-30 16:59:33');
INSERT INTO `sso_menu` VALUES ('967795af502129d318899a60716da84f', 'a988f38821885f8f8aaffa49d681aaac', '000020000100002', 3, '新增', '#', 2, 2, '', NULL, 'sys:menu:insert,sys:menu:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 17:04:45', 'admin', '2022-12-20 11:30:37');
INSERT INTO `sso_menu` VALUES ('6fd5cdaf86772d4db0587f3b9281f99b', 'a988f38821885f8f8aaffa49d681aaac', '000020000100003', 3, '修改', '#', 3, 2, '', NULL, 'sys:menu:update,sys:menu:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 17:05:12', 'admin', '2022-12-20 11:33:37');
INSERT INTO `sso_menu` VALUES ('1a73215261f568088e9adeef2dbd8e44', 'a988f38821885f8f8aaffa49d681aaac', '000020000100004', 3, '删除', '#', 4, 2, '', NULL, 'sys:menu:delete,sys:menu:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 17:05:36', 'admin', '2022-12-20 11:34:00');
INSERT INTO `sso_menu` VALUES ('4ef7029abe93c11601678ba16dac406f', '2a4e024fdc76063da32926c63ca9ead2', '0000200002', 2, '帐号管理', 'ion:people-sharp', 4, 1, '/account', '/sys/account/index.vue', '', 0, 1, NULL, 1, '', 'admin', '2022-11-30 16:40:28', 'admin', '2023-01-04 21:03:33');
INSERT INTO `sso_menu` VALUES ('6ac6bc8054107436e24356e3466f00db', '4ef7029abe93c11601678ba16dac406f', '000020000200001', 3, '查询', '#', 1, 2, '', NULL, 'sys:account:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 16:54:15', 'admin', '2022-12-20 11:46:51');
INSERT INTO `sso_menu` VALUES ('9f46c219e3fc35b1c2ef3a95438b16bf', '4ef7029abe93c11601678ba16dac406f', '000020000200002', 3, '新增', '#', 2, 2, '', NULL, 'sys:account:insert,sys:account:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:03:02', 'admin', '2022-12-20 11:47:06');
INSERT INTO `sso_menu` VALUES ('c46042d6e6d16ea95df6461648833675', '4ef7029abe93c11601678ba16dac406f', '000020000200003', 3, '修改', '#', 3, 2, '', NULL, 'sys:account:update,sys:account:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:03:23', 'admin', '2022-12-17 22:26:35');
INSERT INTO `sso_menu` VALUES ('a27822a74728632e0e0ed10d8285bf54', '4ef7029abe93c11601678ba16dac406f', '000020000200004', 3, '删除', '#', 4, 2, '', NULL, 'sys:account:delete,  sys:account:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:03:48', 'admin', '2022-12-20 11:47:15');
INSERT INTO `sso_menu` VALUES ('addeaf01bc278e216de75ad26a8f27b6', '2a4e024fdc76063da32926c63ca9ead2', '0000200003', 2, '组织管理', 'ion:ios-people', 2, 1, '/org', '/sys/org/index.vue', '', 0, 1, NULL, 1, '', 'admin', '2022-11-30 17:22:12', 'admin', '2023-01-03 20:27:42');
INSERT INTO `sso_menu` VALUES ('76f149981f1c86fce81f2f4cdb9674b9', 'addeaf01bc278e216de75ad26a8f27b6', '000020000300001', 3, '查询', '#', 1, 2, '', NULL, 'sys:org:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:26:51', '', NULL);
INSERT INTO `sso_menu` VALUES ('f87d8b297eb3650834048dba7c8d2d89', 'addeaf01bc278e216de75ad26a8f27b6', '000020000300002', 3, '新增', '#', 2, 2, '', NULL, 'sys:org:insert,sys:org:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:27:41', 'admin', '2022-12-20 11:34:47');
INSERT INTO `sso_menu` VALUES ('58efbcc5f46b95aeab069076031959e7', 'addeaf01bc278e216de75ad26a8f27b6', '000020000300003', 3, '修改', '#', 3, 2, '', NULL, 'sys:org:update,sys:org:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:28:45', 'admin', '2022-12-20 11:45:25');
INSERT INTO `sso_menu` VALUES ('ee3ae3a2161e8d58e2c62f340c3d7b55', 'addeaf01bc278e216de75ad26a8f27b6', '000020000300004', 3, '删除', '#', 4, 2, '', NULL, 'sys:org:delete,sys:org:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:29:49', 'admin', '2022-12-20 11:45:40');
INSERT INTO `sso_menu` VALUES ('0f5a85a6fd5bdc9df26b826eec3c17f1', '2a4e024fdc76063da32926c63ca9ead2', '0000200004', 2, '角色管理', 'ion:ios-key', 3, 1, '/role', '/sys/role/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2022-11-30 17:32:14', 'admin', '2022-12-14 14:25:37');
INSERT INTO `sso_menu` VALUES ('f4a0ed4ca7a609aa8268399bdffcecfb', '0f5a85a6fd5bdc9df26b826eec3c17f1', '000020000400001', 3, '查询', '#', 1, 2, '', NULL, 'sys:role:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:53:00', '', NULL);
INSERT INTO `sso_menu` VALUES ('fb5dac5b0b9b610ed1e996108d6445b0', '0f5a85a6fd5bdc9df26b826eec3c17f1', '000020000400002', 3, '新增', '#', 2, 2, '', NULL, 'sys:role:insert,sys:role:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:54:39', 'admin', '2022-12-20 11:46:05');
INSERT INTO `sso_menu` VALUES ('9b9139c09668bb22888201b7e8a812c4', '0f5a85a6fd5bdc9df26b826eec3c17f1', '000020000400003', 3, '修改', '#', 3, 2, '', NULL, 'sys:role:update,sys:role:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:58:08', 'admin', '2022-12-20 11:46:23');
INSERT INTO `sso_menu` VALUES ('c487023e85c9aaf5510a03e8017b768c', '0f5a85a6fd5bdc9df26b826eec3c17f1', '000020000400004', 3, '删除', '#', 4, 2, '', NULL, 'sys:role:delete,sys:role:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 18:02:10', 'admin', '2022-12-20 11:46:13');
INSERT INTO `sso_menu` VALUES ('c9eb585420911ee18335d935d3872934', '2a4e024fdc76063da32926c63ca9ead2', '0000200005', 2, '字典管理', 'ion:ios-list', 5, 1, '/dict', '/sys/dict/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2022-11-30 18:08:11', 'admin', '2024-08-21 15:32:19');
INSERT INTO `sso_menu` VALUES ('80526081fb00ce5dbe629ef358231909', 'c9eb585420911ee18335d935d3872934', '000020000500001', 3, '查询', '#', 1, 2, '', NULL, 'sys:dict:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:00:35', '', NULL);
INSERT INTO `sso_menu` VALUES ('f634ba1d6d840fb1f945b4f811dd928d', 'c9eb585420911ee18335d935d3872934', '000020000500002', 3, '新增', '#', 2, 2, '', NULL, 'sys:dict:insert,sys:dict:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:01:26', '', NULL);
INSERT INTO `sso_menu` VALUES ('ce71591e16d47d6b4ff1d52c2bb83ae7', 'c9eb585420911ee18335d935d3872934', '000020000500003', 3, '修改', '#', 3, 2, '', NULL, 'sys:dict:update,sys:dict:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:01:46', '', NULL);
INSERT INTO `sso_menu` VALUES ('e48e7b98d2c0331ff6241514e97dad8b', 'c9eb585420911ee18335d935d3872934', '000020000500004', 3, '删除', '#', 4, 2, '', NULL, 'sys:dict:delete,sys:dict:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:02:10', '', NULL);
INSERT INTO `sso_menu` VALUES ('67dfbce31013ada62800425f72997962', '2a4e024fdc76063da32926c63ca9ead2', '0000200006', 2, '字典项', 'ion:ios-menu', 6, 1, '/dict/:dictCode', '/sys/dict-item/index.vue', NULL, 0, 0, '0000200005', 0, '', 'admin', '2023-01-03 17:07:39', 'admin', '2023-04-11 22:22:19');
INSERT INTO `sso_menu` VALUES ('cfbdf3ce5297cebf806ac116fc239558', '2a4e024fdc76063da32926c63ca9ead2', '0000200008', 2, '日志管理', 'ion:ios-compose-outline', 8, 1, '/log', '/sys/sys-log/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-01-08 22:17:26', 'admin', '2023-04-11 22:21:36');
INSERT INTO `sso_menu` VALUES ('25a5783ea03e26d7844b9b7370576236', 'cfbdf3ce5297cebf806ac116fc239558', '000020000800001', 3, '查询', '#', 1, 2, '', NULL, 'sys:log:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:06:31', 'mfish', '2023-07-31 14:52:22');
INSERT INTO `sso_menu` VALUES ('9a293c164762776e0a876323a3363dec', 'cfbdf3ce5297cebf806ac116fc239558', '000020000800002', 3, '删除', '#', 2, 2, '', NULL, 'sys:log:delete,sys:log:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:07:06', 'mfish', '2023-07-31 14:52:36');
INSERT INTO `sso_menu` VALUES ('60efb66a88ab33b339718eb0d052a033', '2a4e024fdc76063da32926c63ca9ead2', '0000200009', 2, '文件管理', 'ion:file-tray-full-outline', 9, 1, '/file', '/storage/sys-file/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-03-02 14:58:44', 'admin', '2023-04-11 22:23:20');
INSERT INTO `sso_menu` VALUES ('731738ed9bbd2e36456b790dfadcb84e', '60efb66a88ab33b339718eb0d052a033', '000020000900001', 3, '查询', '#', 1, 2, '', NULL, 'sys:file:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:15:47', 'admin', '2023-03-10 10:17:31');
INSERT INTO `sso_menu` VALUES ('ce1a05cdedf2d0684574a30dd3ed14f9', '60efb66a88ab33b339718eb0d052a033', '000020000900002', 3, '上传', '#', 2, 2, '', NULL, 'sys:file:upload,sys:file:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:16:27', 'admin', '2023-03-10 10:17:40');
INSERT INTO `sso_menu` VALUES ('a7145a05342033be0caa4a8f1e262f8a', '60efb66a88ab33b339718eb0d052a033', '000020000900003', 3, '删除', '#', 3, 2, '', NULL, 'sys:file:delete,sys:file:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:16:57', 'admin', '2023-03-10 10:17:49');
INSERT INTO `sso_menu` VALUES ('3a74d165bcd286f102e10a1be8c23eef', '60efb66a88ab33b339718eb0d052a033', '000020000900004', 3, '状态修改', '#', 4, 2, '', NULL, 'sys:file:status,sys:file:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:51:47', '', NULL);
INSERT INTO `sso_menu` VALUES ('c52a49da263d57d2c89edcbc9ca70a0a', '2a4e024fdc76063da32926c63ca9ead2', '0000200010', 2, '在线用户', 'ion:wifi', 10, 1, '/online', '/sys/online/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-03-09 11:34:20', 'admin', '2023-03-09 15:19:05');
INSERT INTO `sso_menu` VALUES ('101cb161536a5a80731a4d6db0b5eeac', 'c52a49da263d57d2c89edcbc9ca70a0a', '000020001000001', 3, '查询', '#', 1, 2, '', NULL, 'sys:online:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:20:38', '', NULL);
INSERT INTO `sso_menu` VALUES ('2628fb3c4166d6469f06fcea9b9c0c55', 'c52a49da263d57d2c89edcbc9ca70a0a', '000020001000002', 3, '强制退出', '#', 2, 2, '', NULL, 'sys:online:revoke,sys:online:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:21:07', '', NULL);
INSERT INTO `sso_menu` VALUES ('3d1efa154266719e6322808064df4b13', '2a4e024fdc76063da32926c63ca9ead2', '0000200011', 2, '数据库', 'ion:server-outline', 12, 1, '/db-connect', '/sys/db-connect/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-04-05 21:55:09', 'admin', '2023-05-18 09:04:51');
INSERT INTO `sso_menu` VALUES ('3c4b2d0f7558d7f45a29fd9c6a7edea7', '3d1efa154266719e6322808064df4b13', '000020001100001', 3, '查询', '#', 1, 2, '', NULL, 'sys:database:query', 0, 1, NULL, NULL, '', 'admin', '2023-04-05 21:55:09', '', NULL);
INSERT INTO `sso_menu` VALUES ('79cbc9e257ee8f44db6b133c584ff86a', '3d1efa154266719e6322808064df4b13', '000020001100002', 3, '新增', '#', 2, 2, '', NULL, 'sys:database:query,sys:database:insert', 0, 1, NULL, NULL, '', 'admin', '2023-04-05 21:55:09', '', NULL);
INSERT INTO `sso_menu` VALUES ('5271166ced06a95d787dc049d3f19bd2', '3d1efa154266719e6322808064df4b13', '000020001100003', 3, '修改', '#', 3, 2, '', NULL, 'sys:database:query,sys:database:update', 0, 1, NULL, NULL, '', 'admin', '2023-04-05 21:55:09', '', NULL);
INSERT INTO `sso_menu` VALUES ('43ca0b3ba70c7ba5a2f91882f618208b', '3d1efa154266719e6322808064df4b13', '000020001100004', 3, '删除', '#', 4, 2, '', NULL, 'sys:database:query,sys:database:delete', 0, 1, NULL, NULL, '', 'admin', '2023-04-05 21:55:09', '', NULL);
INSERT INTO `sso_menu` VALUES ('cfc0ca0b3c9220fa3cae26420cf0c51f', '2a4e024fdc76063da32926c63ca9ead2', '0000200013', 2, '应用管理', 'ion:apps-outline', 11, 1, '/client', '/sys/sso-client-details/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-05-12 22:51:01', 'admin', '2023-05-18 09:04:46');
INSERT INTO `sso_menu` VALUES ('bf2e6661d9a4d115f018ffd4ff202d92', 'cfc0ca0b3c9220fa3cae26420cf0c51f', '000020001300001', 3, '查询', '#', 1, 2, '', NULL, 'sys:client:query', 0, 1, NULL, NULL, '', 'admin', '2023-05-17 22:24:48', '', NULL);
INSERT INTO `sso_menu` VALUES ('93190e5d426f69cd2712aac373542698', 'cfc0ca0b3c9220fa3cae26420cf0c51f', '000020001300002', 3, '新增', '#', 2, 2, '', NULL, 'sys:client:query,sys:client:insert', 0, 1, NULL, NULL, '', 'admin', '2023-05-17 22:26:36', '', NULL);
INSERT INTO `sso_menu` VALUES ('b40f985b74d1dd6e311e21369beea8ea', 'cfc0ca0b3c9220fa3cae26420cf0c51f', '000020001300003', 3, '修改', '#', 3, 2, '', NULL, 'sys:client:query,sys:client:update', 0, 1, NULL, NULL, '', 'admin', '2023-05-17 22:27:08', '', NULL);
INSERT INTO `sso_menu` VALUES ('9840528e4a1efd3a510d8a087baf4bdb', 'cfc0ca0b3c9220fa3cae26420cf0c51f', '000020001300004', 3, '删除', '#', 4, 2, '', NULL, 'sys:client:query,sys:client:delete', 0, 1, NULL, NULL, '', 'admin', '2023-05-17 23:30:20', '', NULL);
INSERT INTO `sso_menu` VALUES ('f16ec999ec440bee68dd4e01a650fd54', '2a4e024fdc76063da32926c63ca9ead2', '0000200014', 2, '分类管理', 'ant-design:cluster-outlined', 7, 1, '/category', '/sys/dict-category/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2024-03-14 14:21:35', '', NULL);
INSERT INTO `sso_menu` VALUES ('4d1e926fd6767a786b1cab58e3bc5624', 'f16ec999ec440bee68dd4e01a650fd54', '000020001400001', 3, '查询', '#', 1, 2, '', NULL, 'sys:dictCategory:query', 0, 1, NULL, NULL, '', 'admin', '2024-03-14 14:22:44', '', NULL);
INSERT INTO `sso_menu` VALUES ('b54e001d27b804b769564f35430193b2', 'f16ec999ec440bee68dd4e01a650fd54', '000020001400002', 3, '新增', '#', 2, 2, '', NULL, 'sys:dictCategory:query,sys:dictCategory:insert', 0, 1, NULL, NULL, '', 'admin', '2024-03-14 14:23:40', '', NULL);
INSERT INTO `sso_menu` VALUES ('43f57faab0a1d54cc5130d8a3cc9594a', 'f16ec999ec440bee68dd4e01a650fd54', '000020001400003', 3, '修改', '#', 3, 2, '', NULL, 'sys:dictCategory:query,sys:dictCategory:update', 0, 1, NULL, NULL, '', 'admin', '2024-03-14 14:24:22', '', NULL);
INSERT INTO `sso_menu` VALUES ('f4a9dee8dbff56cd15ffafa815c84a27', 'f16ec999ec440bee68dd4e01a650fd54', '000020001400004', 3, '删除', '#', 4, 2, '', NULL, 'sys:dictCategory:query,sys:dictCategory:delete', 0, 1, NULL, NULL, '', 'admin', '2024-03-14 14:24:57', '', NULL);
INSERT INTO `sso_menu` VALUES ('6e491486dc4cb475e4bd037d06ab2801', '', '00003', 1, '项目文档', 'ion:book-outline', 5, 0, '/doc', NULL, NULL, 1, 1, NULL, NULL, '', 'admin', '2022-11-08 17:08:24', 'admin', '2023-02-14 17:13:51');
INSERT INTO `sso_menu` VALUES ('1a0aee8380c525e7c4802b1c4d587fa8', '6e491486dc4cb475e4bd037d06ab2801', '0000300001', 2, '接口文档', 'simple-icons:swagger', 1, 1, '/swagger', 'http://app.mfish.com.cn:11116/swagger-ui/index.html', NULL, 0, 1, NULL, 0, '', 'admin', '2022-12-14 10:25:31', 'admin', '2023-09-01 14:58:24');
INSERT INTO `sso_menu` VALUES ('1ce2ac44228e37c063e9cd55ed8f0a49', '6e491486dc4cb475e4bd037d06ab2801', '0000300002', 2, 'GitHub地址', 'ion:logo-github', 2, 1, '/git', 'https://github.com/mfish-qf/mfish-nocode', NULL, 1, 1, NULL, 1, '', 'admin', '2022-12-14 15:27:03', 'admin', '2024-09-05 18:17:06');
INSERT INTO `sso_menu` VALUES ('5c723efc132b50c0284d79eaafed5a0f', '6e491486dc4cb475e4bd037d06ab2801', '0000300003', 2, 'AntDesign文档', 'ion:social-angular-outline', 4, 1, '/ant', 'https://www.antdv.com/docs/vue/introduce-cn/', NULL, 1, 1, NULL, 1, '', 'admin', '2022-12-14 15:38:22', 'admin', '2023-11-20 15:43:46');
INSERT INTO `sso_menu` VALUES ('0526179a70fca38a69dd709dec2f1a81', '6e491486dc4cb475e4bd037d06ab2801', '0000300004', 2, 'Vben文档', 'ion:social-vimeo-outline', 5, 1, '/vben', 'https://doc.vvbin.cn/guide/introduction.html', NULL, 0, 1, NULL, 1, '', 'admin', '2022-12-15 09:14:09', 'admin', '2023-03-09 10:36:52');
INSERT INTO `sso_menu` VALUES ('fa2211276b7b84a141667ec9ea8d33a4', '6e491486dc4cb475e4bd037d06ab2801', '0000300005', 2, 'Gitee地址', 'simple-icons:gitee', 3, 1, '/gitee', 'https://gitee.com/qiufeng9862/mfish-nocode', NULL, 1, 1, NULL, 0, '', 'admin', '2023-03-09 10:36:36', '', NULL);
INSERT INTO `sso_menu` VALUES ('ee34f5aec6a2220f57fa151a147ede3c', '6e491486dc4cb475e4bd037d06ab2801', '0000300006', 2, '开发文档', 'ion:fish-outline', 0, 1, '/mfish', 'https://www.mfish.com.cn', NULL, 0, 1, NULL, 1, '', 'admin', '2023-04-30 11:33:21', '', NULL);
INSERT INTO `sso_menu` VALUES ('0aa9f017545ec947a075f76e34c075c0', '', '00007', 1, '系统监控', 'ion:fitness-outline', 7, 0, '/monitor', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2023-01-27 13:55:58', 'admin', '2023-09-01 15:14:23');
INSERT INTO `sso_menu` VALUES ('e159379c94b8fcc58ebc38cf8b322772', '0aa9f017545ec947a075f76e34c075c0', '0000700001', 2, '监控中心', 'ion:fitness-sharp', 1, 1, '/center', 'http://localhost:9223', NULL, 0, 1, NULL, 0, '', 'admin', '2023-01-27 13:56:32', 'admin', '2024-02-19 22:46:14');
INSERT INTO `sso_menu` VALUES ('ca0a3c9ae9cd551ee4e1b727861b7c78', '', '00009', 1, '任务调度', 'ion:calendar-outline', 6, 0, '/scheduler', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2023-02-14 17:13:20', 'admin', '2023-09-01 15:14:15');
INSERT INTO `sso_menu` VALUES ('9d5397b6ddb4d194a95b05f42b80445b', 'ca0a3c9ae9cd551ee4e1b727861b7c78', '0000900001', 2, '任务管理', 'ion:caret-forward-circle-outline', 1, 1, '/job', '/scheduler/job/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-03-10 17:11:24', '', NULL);
INSERT INTO `sso_menu` VALUES ('238132a09b6f761374dfd205b6388245', '9d5397b6ddb4d194a95b05f42b80445b', '000090000100001', 3, '查询', '#', 1, 2, '', NULL, 'sys:job:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:11:24', '', NULL);
INSERT INTO `sso_menu` VALUES ('6e02e0e621140968dc62a2ce3dfa198d', '9d5397b6ddb4d194a95b05f42b80445b', '000090000100002', 3, '新增', '#', 2, 2, '', NULL, 'sys:job:insert,sys:job:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:11:24', '', NULL);
INSERT INTO `sso_menu` VALUES ('ad2ab62e13c7750dcb5b41b00cbdcf66', '9d5397b6ddb4d194a95b05f42b80445b', '000090000100003', 3, '修改', '#', 3, 2, '', NULL, 'sys:job:update,sys:job:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:11:24', '', NULL);
INSERT INTO `sso_menu` VALUES ('4cabb05e97ea7a738a2f7ce3c9d224d8', '9d5397b6ddb4d194a95b05f42b80445b', '000090000100004', 3, '删除', '#', 4, 2, '', NULL, 'sys:job:delete,sys:job:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:11:24', '', NULL);
INSERT INTO `sso_menu` VALUES ('c36420436629884000e73b158166f260', '9d5397b6ddb4d194a95b05f42b80445b', '000090000100005', 3, '执行', '#', 5, 2, '', NULL, 'sys:job:execute,sys:job:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:11:24', '', NULL);
INSERT INTO `sso_menu` VALUES ('a43c057f48b54c9038719179cf9e284d', 'ca0a3c9ae9cd551ee4e1b727861b7c78', '0000900002', 2, '任务日志', 'ion:ios-paper-outline', 2, 1, '/jobLog', '/scheduler/job-log/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-03-10 17:11:32', 'admin', '2023-04-11 22:24:06');
INSERT INTO `sso_menu` VALUES ('1e1b7d50ab93ffdeca33fe5b7006eb01', 'a43c057f48b54c9038719179cf9e284d', '000090000200001', 3, '查询', '#', 1, 2, '', NULL, 'sys:jobLog:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:11:32', '', NULL);
INSERT INTO `sso_menu` VALUES ('e1996e92ac6cf37c0c2e40825a7af472', 'a43c057f48b54c9038719179cf9e284d', '000090000200002', 3, '删除', '#', 2, 2, '', NULL, 'sys:jobLog:delete,sys:jobLog:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:11:32', '', NULL);
INSERT INTO `sso_menu` VALUES ('70943d8248fd8f77ade038d9afa0bf33', '', '00011', 1, '低代码', 'ant-design:code-outlined', 2, 0, '/low-code', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2023-03-29 10:26:55', 'admin', '2024-04-16 17:48:08');
INSERT INTO `sso_menu` VALUES ('a606083b203d32915c4d0e649c7b7c6b', '70943d8248fd8f77ade038d9afa0bf33', '0001100001', 2, '代码生成', 'ion:code-slash-outline', 4, 1, '/code-build', '/sys/code-build/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-04-11 22:19:43', 'admin', '2024-01-09 11:47:36');
INSERT INTO `sso_menu` VALUES ('85cd52250e435c555622c268262f4c02', 'a606083b203d32915c4d0e649c7b7c6b', '000110000100001', 3, '查询', '#', 1, 2, '', NULL, 'sys:codeBuild:query,sys:dict:query', 0, 1, NULL, NULL, '', 'mfish', '2023-07-11 09:56:58', 'admin', '2024-04-20 21:14:03');
INSERT INTO `sso_menu` VALUES ('8360ab9544a00dc7d9f15594dd69e2ff', 'a606083b203d32915c4d0e649c7b7c6b', '000110000100002', 3, '新增', '#', 2, 2, '', NULL, 'sys:codeBuild:query,sys:codeBuild:insert', 0, 1, NULL, NULL, '', 'mfish', '2023-07-11 09:57:12', '', NULL);
INSERT INTO `sso_menu` VALUES ('6cedf51376c8998eb49e280cdba6c533', 'a606083b203d32915c4d0e649c7b7c6b', '000110000100003', 3, '删除', '#', 3, 2, '', NULL, 'sys:codeBuild:query,sys:codeBuild:delete', 0, 1, NULL, NULL, '', 'mfish', '2023-07-11 09:57:45', '', NULL);
INSERT INTO `sso_menu` VALUES ('6aee07bfe60f4ee4021bfce397a8f4df', '70943d8248fd8f77ade038d9afa0bf33', '0001100003', 2, '数据源', 'ant-design:database-outlined', 3, 1, '/database', '/sys/database/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-07-25 17:49:02', 'admin', '2024-01-09 11:47:32');
INSERT INTO `sso_menu` VALUES ('81ecfe903cb3116f00c367678059c87c', '6aee07bfe60f4ee4021bfce397a8f4df', '000110000300001', 3, '查询', '#', 1, 2, '', NULL, 'sys:database:query', 0, 1, NULL, NULL, '', 'admin', '2023-07-25 17:49:02', '', NULL);
INSERT INTO `sso_menu` VALUES ('5ad16d38964bf541b6417b07ddf33d9b', '6aee07bfe60f4ee4021bfce397a8f4df', '000110000300002', 3, '新增', '#', 2, 2, '', NULL, 'sys:database:query,sys:database:insert', 0, 1, NULL, NULL, '', 'admin', '2023-07-25 17:49:02', '', NULL);
INSERT INTO `sso_menu` VALUES ('369de1bef8d1e964414f25ec6d3156bc', '6aee07bfe60f4ee4021bfce397a8f4df', '000110000300003', 3, '修改', '#', 3, 2, '', NULL, 'sys:database:query,sys:database:update', 0, 1, NULL, NULL, '', 'admin', '2023-07-25 17:49:02', '', NULL);
INSERT INTO `sso_menu` VALUES ('a2cdbac934bae3da9987df0655db2455', '6aee07bfe60f4ee4021bfce397a8f4df', '000110000300004', 3, '删除', '#', 4, 2, '', NULL, 'sys:database:query,sys:database:delete', 0, 1, NULL, NULL, '', 'admin', '2023-07-25 17:49:02', '', NULL);
INSERT INTO `sso_menu` VALUES ('292791abfbf63b35ad9ce510c0a5823e', '70943d8248fd8f77ade038d9afa0bf33', '0001100004', 2, '自助API', 'ant-design:api-outlined', 2, 1, '/mf-api', '/nocode/api-folder/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-08-02 10:39:11', 'admin', '2024-01-09 11:47:18');
INSERT INTO `sso_menu` VALUES ('eda0153e492e86ade2fe6702d267fef3', '292791abfbf63b35ad9ce510c0a5823e', '000110000400001', 3, '查询', '#', 1, 2, '', NULL, 'sys:mfApi:query', 0, 1, NULL, NULL, '', 'admin', '2023-08-02 10:39:11', '', NULL);
INSERT INTO `sso_menu` VALUES ('afbf598d2290b24a5e87e7547c05515d', '292791abfbf63b35ad9ce510c0a5823e', '000110000400002', 3, '新增', '#', 2, 2, '', NULL, 'sys:mfApi:query,sys:mfApi:insert', 0, 1, NULL, NULL, '', 'admin', '2023-08-02 10:39:11', '', NULL);
INSERT INTO `sso_menu` VALUES ('285bb37f32e878e24d5a22445c1bc5af', '292791abfbf63b35ad9ce510c0a5823e', '000110000400003', 3, '修改', '#', 3, 2, '', NULL, 'sys:mfApi:query,sys:mfApi:update', 0, 1, NULL, NULL, '', 'admin', '2023-08-02 10:39:11', '', NULL);
INSERT INTO `sso_menu` VALUES ('01ee419d3dcc28acf1920428391720b7', '292791abfbf63b35ad9ce510c0a5823e', '000110000400004', 3, '删除', '#', 4, 2, '', NULL, 'sys:mfApi:query,sys:mfApi:delete', 0, 1, NULL, NULL, '', 'admin', '2023-08-02 10:39:11', '', NULL);
INSERT INTO `sso_menu` VALUES ('e909edc44910691dedc2c5338ec0e603', '70943d8248fd8f77ade038d9afa0bf33', '0001100005', 2, '自助大屏', 'ant-design:fund-projection-screen-outlined', 1, 1, '/mf-screen', '/nocode/screen-folder/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2024-01-09 11:47:10', 'admin', '2024-01-09 11:54:24');
INSERT INTO `sso_menu` VALUES ('40bf4846599bf8dbd307f77bf51a7dad', '', '00012', 1, '租户管理', 'ion:home-outline', 4, 0, '/tenant', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2023-05-31 22:41:48', 'admin', '2023-09-01 15:14:04');
INSERT INTO `sso_menu` VALUES ('74ce27a1e94091da2efa714e32680e7a', '40bf4846599bf8dbd307f77bf51a7dad', '0001200001', 2, '租户配置', 'ant-design:home-outlined', 0, 1, '/config', '/sys/sso-tenant/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-05-31 22:43:26', 'admin', '2024-04-09 17:08:21');
INSERT INTO `sso_menu` VALUES ('3a92fd411f0a70bf477e6dc354f4e29e', '74ce27a1e94091da2efa714e32680e7a', '000120000100001', 3, '查询', '#', 1, 2, '', NULL, 'sys:ssoTenant:query', 0, 1, NULL, NULL, '', 'mfish', '2023-06-28 13:02:17', '', NULL);
INSERT INTO `sso_menu` VALUES ('442f89857faa6bc929ef4f422b8c4b99', '74ce27a1e94091da2efa714e32680e7a', '000120000100002', 3, '新增', '#', 2, 2, '', NULL, 'sys:ssoTenant:query,sys:ssoTenant:insert', 0, 1, NULL, NULL, '', 'mfish', '2023-06-28 13:02:42', '', NULL);
INSERT INTO `sso_menu` VALUES ('520873645d565776988d81481d8b0d26', '74ce27a1e94091da2efa714e32680e7a', '000120000100003', 3, '修改', '#', 3, 2, '', NULL, 'sys:ssoTenant:query,sys:ssoTenant:update', 0, 1, NULL, NULL, '', 'mfish', '2023-06-28 13:02:59', '', NULL);
INSERT INTO `sso_menu` VALUES ('d9832cd3aefbb5f99267edb995ff8c75', '74ce27a1e94091da2efa714e32680e7a', '000120000100004', 3, '删除', '#', 4, 2, '', NULL, 'sys:ssoTenant:query,sys:ssoTenant:delete', 0, 1, NULL, NULL, '', 'mfish', '2023-06-28 13:03:15', '', NULL);
INSERT INTO `sso_menu` VALUES ('eb5f513d5430597d3ea312e1bf760b23', '40bf4846599bf8dbd307f77bf51a7dad', '0001200002', 2, '租户信息', 'ant-design:info-circle-outlined', 2, 1, '/info/3', '/sys/account/setting/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-06-25 20:21:51', 'admin', '2023-06-25 21:15:54');
INSERT INTO `sso_menu` VALUES ('7e690410346c4d3a1610d85e8c9f906b', '40bf4846599bf8dbd307f77bf51a7dad', '0001200003', 2, '个人信息', 'ion:person', 1, 1, '/info/1', '/sys/account/setting/index.vue', NULL, 0, 1, '0000200002', 1, '', 'admin', '2023-06-29 10:54:29', 'admin', '2024-04-09 17:08:26');
INSERT INTO `sso_menu` VALUES ('6d71e92a5a3712acbcfcc58f65b93f4f', '7e690410346c4d3a1610d85e8c9f906b', '000120000300001', 3, '上传图像', '#', 1, 2, '', NULL, 'sys:file:upload', 0, 1, NULL, NULL, '', 'admin', '2023-06-29 10:54:29', '', NULL);
INSERT INTO `sso_menu` VALUES ('604277501ebb1c92d70912c49188eaa5', '7e690410346c4d3a1610d85e8c9f906b', '000120000300002', 3, '密码修改', '#', 2, 2, '', NULL, 'sys:password:update', 0, 1, NULL, 1, '', 'admin', '2024-10-31 22:06:28', '', NULL);
INSERT INTO `sso_menu` VALUES ('5d9ff19ac80d4899ea469e4d4b9a6aeb', '40bf4846599bf8dbd307f77bf51a7dad', '0001200004', 2, '租户组织', 'ion:ios-people', 4, 1, '/info/4', '/sys/account/setting/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2024-04-09 03:06:43', 'admin', '2024-04-09 11:07:27');
INSERT INTO `sso_menu` VALUES ('204217f09c1206f9d5b9725c1e05604a', '5d9ff19ac80d4899ea469e4d4b9a6aeb', '000120000400001', 3, '查询', '#', 1, 2, '', NULL, 'sys:tenantOrg:query', 0, 1, NULL, NULL, '', 'admin', '2024-04-09 03:18:00', '', NULL);
INSERT INTO `sso_menu` VALUES ('573c955ed2c2ffdea2654487cdfdc520', '5d9ff19ac80d4899ea469e4d4b9a6aeb', '000120000400002', 3, '新增', '#', 2, 2, '', NULL, 'sys:tenantOrg:query,sys:tenantOrg:insert', 0, 1, NULL, NULL, '', 'admin', '2024-04-09 03:19:11', '', NULL);
INSERT INTO `sso_menu` VALUES ('b33532a555157cddf48628879433e844', '5d9ff19ac80d4899ea469e4d4b9a6aeb', '000120000400003', 3, '修改', '#', 3, 2, '', NULL, 'sys:tenantOrg:query,sys:tenantOrg:update', 0, 1, NULL, NULL, '', 'admin', '2024-04-09 03:19:44', '', NULL);
INSERT INTO `sso_menu` VALUES ('bb26a48e941f30b9453bf90cba8eb785', '5d9ff19ac80d4899ea469e4d4b9a6aeb', '000120000400004', 3, '删除', '#', 4, 2, '', NULL, 'sys:tenantOrg:query,sys:tenantOrg:delete', 0, 1, NULL, NULL, '', 'admin', '2024-04-09 03:20:10', '', NULL);
INSERT INTO `sso_menu` VALUES ('eeda5a7b6e2868e5e25a1e794d5786db', '40bf4846599bf8dbd307f77bf51a7dad', '0001200005', 2, '租户角色', 'ion:ios-key', 5, 1, '/info/5', '/sys/account/setting/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2024-04-09 03:07:58', '', NULL);
INSERT INTO `sso_menu` VALUES ('a455186424982517f128da41f2c159d9', 'eeda5a7b6e2868e5e25a1e794d5786db', '000120000500001', 3, '查询', '#', 1, 2, '', NULL, 'sys:tenantRole:query', 0, 1, NULL, NULL, '', 'admin', '2024-04-09 03:20:55', '', NULL);
INSERT INTO `sso_menu` VALUES ('58c1fc9fde9ad2a37fa366a9a91f094d', 'eeda5a7b6e2868e5e25a1e794d5786db', '000120000500002', 3, '新增', '#', 2, 2, '', NULL, 'sys:tenantRole:query,sys:tenantRole:insert', 0, 1, NULL, NULL, '', 'admin', '2024-04-09 03:21:27', '', NULL);
INSERT INTO `sso_menu` VALUES ('c331e32be60017fc936703d3da235ddb', 'eeda5a7b6e2868e5e25a1e794d5786db', '000120000500003', 3, '修改', '#', 3, 2, '', NULL, 'sys:tenantRole:query,sys:tenantRole:update', 0, 1, NULL, NULL, '', 'admin', '2024-04-09 03:22:06', '', NULL);
INSERT INTO `sso_menu` VALUES ('cea175c6cef1506173dcc579fe42572c', 'eeda5a7b6e2868e5e25a1e794d5786db', '000120000500004', 3, '删除', '#', 4, 2, '', NULL, 'sys:tenantRole:query,sys:tenantRole:delete', 0, 1, NULL, NULL, '', 'admin', '2024-04-09 03:22:47', '', NULL);
INSERT INTO `sso_menu` VALUES ('7b5fc202b06024a9e3920bc57820a954', '40bf4846599bf8dbd307f77bf51a7dad', '0001200006', 2, '租户人员', 'ion:people-sharp', 6, 1, '/info/6', '/sys/account/setting/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2024-04-09 03:11:13', 'admin', '2024-04-09 11:11:38');
INSERT INTO `sso_menu` VALUES ('494d219f20ef782becd6b75c899ba4b5', '7b5fc202b06024a9e3920bc57820a954', '000120000600001', 3, '查询', '#', 1, 2, '', NULL, 'sys:tenantUser:query', 0, 1, NULL, NULL, '', 'admin', '2024-04-09 03:23:19', '', NULL);
INSERT INTO `sso_menu` VALUES ('ac7eb51ce5e700aa268464b9fd052624', '7b5fc202b06024a9e3920bc57820a954', '000120000600002', 3, '新增', '#', 2, 2, '', NULL, 'sys:tenantUser:query,sys:tenantUser:insert', 0, 1, NULL, NULL, '', 'admin', '2024-04-09 03:24:27', '', NULL);
INSERT INTO `sso_menu` VALUES ('b8d42dd7c44f8bfba043ec2641aa0442', '7b5fc202b06024a9e3920bc57820a954', '000120000600003', 3, '删除', '#', 3, 2, '', NULL, 'sys:tenantUser:query,sys:tenantUser:delete', 0, 1, NULL, NULL, '', 'admin', '2024-04-09 03:25:08', '', NULL);
INSERT INTO `sso_menu` VALUES ('53e8eaceee36c1d54e43319fdd60811b', '', '00013', 1, '使用样例', 'ant-design:read-outlined', 8, 0, '/demo', NULL, NULL, 0, 1, NULL, 1, '', 'mfish', '2024-09-02 09:41:55', 'admin', '2024-09-02 16:06:00');
INSERT INTO `sso_menu` VALUES ('9c6f4eff70d7b2048f63adf229c5d30d', '53e8eaceee36c1d54e43319fdd60811b', '0001300002', 2, '多级目录', 'ion:folder-open-outline', 4, 0, '/level', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2024-09-02 16:06:17', 'admin', '2024-09-05 10:12:16');
INSERT INTO `sso_menu` VALUES ('5b543a83371c766788047a1a1907cffd', '9c6f4eff70d7b2048f63adf229c5d30d', '000130000200001', 3, '目录1', 'ion:folder-open-outline', 1, 0, '/menu1', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2024-09-02 16:06:17', '', NULL);
INSERT INTO `sso_menu` VALUES ('bcd18784374699438a215a9ab1e9b351', '5b543a83371c766788047a1a1907cffd', '00013000020000100002', 4, '多级菜单', 'ant-design:appstore-outlined', 1, 1, '/menu3', '/demo/level/LevelMenu.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2024-09-03 21:57:58', '', NULL);
INSERT INTO `sso_menu` VALUES ('e862cd0f17439fe26950e5ecb9492cc2', '53e8eaceee36c1d54e43319fdd60811b', '0001300003', 2, '导入导出', 'ant-design:export-outlined', 3, 1, '/demo-import-export', '/demo/demo-import-export/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2024-09-02 16:24:18', 'admin', '2024-09-09 21:12:38');
INSERT INTO `sso_menu` VALUES ('6858a7df953aa4a3bbab3bd77e03b1fb', 'e862cd0f17439fe26950e5ecb9492cc2', '000130000300001', 3, '查询', '#', 1, 2, '', NULL, 'demo:demoImportExport:query', 0, 1, NULL, NULL, '', 'admin', '2024-09-02 16:24:18', '', NULL);
INSERT INTO `sso_menu` VALUES ('37603c7afb87a9d94d03b06730cb0d29', 'e862cd0f17439fe26950e5ecb9492cc2', '000130000300002', 3, '导入', '#', 2, 2, '', NULL, 'demo:demoImportExport:query,demo:demoImportExport:import', 0, 1, NULL, NULL, '', 'admin', '2024-09-02 16:24:18', 'admin', '2024-09-03 23:10:01');
INSERT INTO `sso_menu` VALUES ('ec07264b984c531ee729eda12ada4c49', 'e862cd0f17439fe26950e5ecb9492cc2', '000130000300003', 3, '导出', '#', 3, 2, '', NULL, 'demo:demoImportExport:query,demo:demoImportExport:export', 0, 1, NULL, NULL, '', 'admin', '2024-09-02 16:24:18', 'admin', '2024-09-03 23:10:15');
INSERT INTO `sso_menu` VALUES ('bff2e13c8ef26ffc98cb4941b2390316', 'e862cd0f17439fe26950e5ecb9492cc2', '000130000300004', 3, '删除', '#', 4, 2, '', NULL, 'demo:demoImportExport:query,demo:demoImportExport:delete', 0, 1, NULL, NULL, '', 'admin', '2024-09-02 16:24:18', '', NULL);
INSERT INTO `sso_menu` VALUES ('1eb28fcc2c9e009ddf9accec8be4665d', '53e8eaceee36c1d54e43319fdd60811b', '0001300004', 2, '数据权限', 'ant-design:filter-outlined', 10, 1, '/demo-data-scope', '/demo/demo-data-scope/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2024-09-04 17:29:18', 'admin', '2024-09-09 21:12:50');
INSERT INTO `sso_menu` VALUES ('0744327e98f399788bb030efb1c0b637', '1eb28fcc2c9e009ddf9accec8be4665d', '000130000400001', 3, '查询', '#', 1, 2, '', NULL, 'demo:demoDataScope:query', 0, 1, NULL, NULL, '', 'admin', '2024-09-04 17:29:18', '', NULL);
INSERT INTO `sso_menu` VALUES ('5a5a25e8923ace2700133ab997af0e80', '53e8eaceee36c1d54e43319fdd60811b', '0001300006', 2, '上传下载', 'ant-design:cloud-upload-outlined', 2, 1, '/demo-up-down', '/demo/demo-up-down/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2024-09-08 21:20:18', 'admin', '2024-09-08 21:20:41');
INSERT INTO `sso_menu` VALUES ('0913e14334f59671ae6abfdfce5e0559', '5a5a25e8923ace2700133ab997af0e80', '000130000600001', 3, '上传', '#', 1, 2, '', NULL, 'demo:demoImportExport:query,demo:demoImportExport:update', 0, 1, NULL, 1, '', 'admin', '2024-09-09 21:59:21', 'admin', '2024-09-09 21:59:35');
INSERT INTO `sso_menu` VALUES ('23f38246b88faf0826c17d57c0d846af', '53e8eaceee36c1d54e43319fdd60811b', '0001300007', 2, '主子表', 'ant-design:table-outlined', 5, 1, '/demo-order', '/demo/demo-order/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2024-09-13 14:22:11', 'admin', '2024-09-13 14:43:25');
INSERT INTO `sso_menu` VALUES ('ec812dc5df8d93e478bd22a04d3db770', '23f38246b88faf0826c17d57c0d846af', '000130000700001', 3, '查询', '#', 1, 2, '', NULL, 'demo:demoOrder:query', 0, 1, NULL, NULL, '', 'admin', '2024-09-13 14:22:11', '', NULL);
INSERT INTO `sso_menu` VALUES ('2bf2996b88ae8cd70e91685e5e95d4f7', '23f38246b88faf0826c17d57c0d846af', '000130000700002', 3, '新增', '#', 2, 2, '', NULL, 'demo:demoOrder:query,demo:demoOrder:insert', 0, 1, NULL, NULL, '', 'admin', '2024-09-13 14:22:11', '', NULL);
INSERT INTO `sso_menu` VALUES ('f193aa905e7aceb1e4b0187d8b63339f', '23f38246b88faf0826c17d57c0d846af', '000130000700003', 3, '修改', '#', 3, 2, '', NULL, 'demo:demoOrder:query,demo:demoOrder:update', 0, 1, NULL, NULL, '', 'admin', '2024-09-13 14:22:11', '', NULL);
INSERT INTO `sso_menu` VALUES ('250f8038be816e61956c792fc9ddc2c6', '23f38246b88faf0826c17d57c0d846af', '000130000700004', 3, '删除', '#', 4, 2, '', NULL, 'demo:demoOrder:query,demo:demoOrder:delete', 0, 1, NULL, NULL, '', 'admin', '2024-09-13 14:22:11', '', NULL);
INSERT INTO `sso_menu` VALUES ('baba7ecb504201d257ad7672de945b00', 'e909edc44910691dedc2c5338ec0e603', '000110000500006', 3, '发布', '#', 6, 2, '', NULL, 'sys:screen:release', 0, 1, NULL, 1, '', 'mfish', '2025-03-29 10:17:16', '', NULL);
INSERT INTO `sso_menu` VALUES ('779ad52f29818635098d05686185dd04', 'e909edc44910691dedc2c5338ec0e603', '000110000500005', 3, '分享', '#', 5, 2, '', NULL, 'sys:screen:query,sys:screen:share', 0, 1, NULL, 1, '', 'admin', '2025-03-04 00:10:41', 'admin', '2025-03-04 00:10:56');
INSERT INTO `sso_menu` VALUES ('2fe640a53e74186e5fd331e61ff19d0c', '4c44aafc06c373138638a7e17262225e', '000110000700001', 3, '查询', '#', 1, 2, '', NULL, 'nocode:formulaInfo:query', 0, 1, NULL, NULL, '', 'admin', '2025-02-11 10:01:46', '', NULL);
INSERT INTO `sso_menu` VALUES ('4c44aafc06c373138638a7e17262225e', '70943d8248fd8f77ade038d9afa0bf33', '0001100007', 2, '公式信息', 'ant-design:function-outlined', 6, 1, '/formula-info', '/nocode/formula-info/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2025-02-11 10:01:46', 'admin', '2025-02-11 10:24:34');
INSERT INTO `sso_menu` VALUES ('8859ae33d914338e58580f0b50004d87', '4c44aafc06c373138638a7e17262225e', '000110000700003', 3, '修改', '#', 3, 2, '', NULL, 'nocode:formulaInfo:query,nocode:formulaInfo:update', 0, 1, NULL, NULL, '', 'admin', '2025-02-11 10:01:46', '', NULL);
INSERT INTO `sso_menu` VALUES ('e3e24beb16f0781eb5d31e65c7530343', '4c44aafc06c373138638a7e17262225e', '000110000700004', 3, '删除', '#', 4, 2, '', NULL, 'nocode:formulaInfo:query,nocode:formulaInfo:delete', 0, 1, NULL, NULL, '', 'admin', '2025-02-11 10:01:46', '', NULL);
INSERT INTO `sso_menu` VALUES ('e5a70ba05bd4794a9633ad48a4ee0592', '4c44aafc06c373138638a7e17262225e', '000110000700002', 3, '新增', '#', 2, 2, '', NULL, 'nocode:formulaInfo:query,nocode:formulaInfo:insert', 0, 1, NULL, NULL, '', 'admin', '2025-02-11 10:01:46', '', NULL);
INSERT INTO `sso_menu` VALUES ('c30a1aacfae8f52e5db5ae0f81d3500a', 'e909edc44910691dedc2c5338ec0e603', '000110000500004', 3, '删除', '#', 4, 2, '', NULL, 'sys:screen:query,sys:screen:delete', 0, 1, NULL, 1, '', 'admin', '2024-12-25 11:34:09', '', NULL);
INSERT INTO `sso_menu` VALUES ('477eeed64a179632778ebd589c2d6430', 'e909edc44910691dedc2c5338ec0e603', '000110000500003', 3, '修改', '#', 3, 2, '', NULL, 'sys:screen:query,sys:screen:update', 0, 1, NULL, 1, '', 'admin', '2024-12-25 11:33:46', '', NULL);
INSERT INTO `sso_menu` VALUES ('866615fb9f44c80a49368a6dab3106bc', 'e909edc44910691dedc2c5338ec0e603', '000110000500002', 3, '新增', '#', 2, 2, '', NULL, 'sys:screen:query,sys:screen:insert', 0, 1, NULL, 1, '', 'admin', '2024-12-25 11:33:22', '', NULL);
INSERT INTO `sso_menu` VALUES ('d7586cc31860b54919c6f616fb070217', 'e909edc44910691dedc2c5338ec0e603', '000110000500001', 3, '查询', '#', 1, 2, '', NULL, 'sys:screen:query', 0, 1, NULL, 1, '', 'admin', '2024-12-25 11:33:03', '', NULL);
INSERT INTO `sso_menu` VALUES ('06d7451a8e2ed7ccdf6f3eb248d40798', '29db2a54d3226d1ec2f90c6498033c99', '000110000600004', 3, '删除', '#', 4, 2, '', NULL, 'nocode:screenCharts:query,nocode:screenCharts:delete', 0, 1, NULL, NULL, '', 'admin', '2024-11-19 09:27:16', '', NULL);
INSERT INTO `sso_menu` VALUES ('29db2a54d3226d1ec2f90c6498033c99', '70943d8248fd8f77ade038d9afa0bf33', '0001100006', 2, '组件管理', 'ant-design:product-outlined', 5, 1, '/screen-charts', '/nocode/screen-charts/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2024-11-19 09:27:16', 'admin', '2024-11-19 09:29:09');
INSERT INTO `sso_menu` VALUES ('312d2bc9ac4f1a7d2f2b5a61fa59ff64', '29db2a54d3226d1ec2f90c6498033c99', '000110000600003', 3, '修改', '#', 3, 2, '', NULL, 'nocode:screenCharts:query,nocode:screenCharts:update', 0, 1, NULL, NULL, '', 'admin', '2024-11-19 09:27:16', '', NULL);
INSERT INTO `sso_menu` VALUES ('77c042434b5bebf53cd89afbd04ca28a', '29db2a54d3226d1ec2f90c6498033c99', '000110000600001', 3, '查询', '#', 1, 2, '', NULL, 'nocode:screenCharts:query', 0, 1, NULL, NULL, '', 'admin', '2024-11-19 09:27:16', '', NULL);
INSERT INTO `sso_menu` VALUES ('d4257d0d8d32697a82718a312e51fcda', '29db2a54d3226d1ec2f90c6498033c99', '000110000600002', 3, '新增', '#', 2, 2, '', NULL, 'nocode:screenCharts:query,nocode:screenCharts:insert', 0, 1, NULL, NULL, '', 'admin', '2024-11-19 09:27:16', '', NULL);
INSERT INTO `sso_menu` VALUES ('8f6ed381a02535791c17521ff0475808', 'eb5f513d5430597d3ea312e1bf760b23', '000120000200001', 3, '查询', '#', 1, 2, '', NULL, 'sys:tenant:query', 0, 1, NULL, 1, '', 'admin', '2025-06-03 13:53:51', '', NULL);

-- ----------------------------
-- Table structure for sso_org
-- ----------------------------
DROP TABLE IF EXISTS `sso_org`;
CREATE TABLE `sso_org`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织ID',
  `parent_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '父组织ID',
  `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `org_fix_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组织固定编码',
  `org_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '组织编码(自动生成父子关系编码)',
  `org_level` tinyint(4) NULL DEFAULT NULL COMMENT '组织级别',
  `org_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '组织名称',
  `org_sort` int(4) NULL DEFAULT 0 COMMENT '排序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `org_code_index`(`org_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '组织结构表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_org
-- ----------------------------
INSERT INTO `sso_org` VALUES ('1', '', '1', 'admin', '00005', 1, '系统默认', 1, 'admin', '18900000001', 'mfish@qq.com', '租户组织', 0, 0, 'admin', '2023-06-20 23:30:00', 'admin', '2023-07-05 15:27:04');
INSERT INTO `sso_org` VALUES ('404542be62d21014451808aa67f1f5df', '', 'a480b6861ca4a44631af794a99e77265', 'mfish', '00006', 1, '南京XXX公司', 1, 'mfish', '18900000002', 'mfish2@qq.com', NULL, 0, 0, 'admin', '2023-06-28 11:17:08', 'admin', '2023-07-11 10:40:19');
INSERT INTO `sso_org` VALUES ('45ee8344ffb942d6f2ee1f868b584a99', '404542be62d21014451808aa67f1f5df', NULL, NULL, '0000600002', 2, 'XXX运维部', 2, 'aa', NULL, NULL, NULL, 0, 0, 'mfish', '2023-06-28 11:24:29', 'mfish', '2023-07-02 23:18:03');
INSERT INTO `sso_org` VALUES ('624edadc0562c7fe9b54f0e3ba5e658b', '8b7bbf1cac1af7faca6dd2686981fb22', NULL, '', '000050000100001', 3, '摸鱼开发部', 1, '', '', '', '', 0, 0, 'admin', '2023-06-28 11:47:54', 'mfish', '2023-07-03 15:48:22');
INSERT INTO `sso_org` VALUES ('76f3d1d15272858c2523f287aea955f9', '8b7bbf1cac1af7faca6dd2686981fb22', NULL, '', '000050000100002', 3, '摸鱼测试部', 2, '', '', '', '', 0, 0, 'admin', '2023-06-28 11:48:14', 'mfish', '2023-07-03 15:48:28');
INSERT INTO `sso_org` VALUES ('8b7bbf1cac1af7faca6dd2686981fb22', '1', NULL, 'mysyb', '0000500001', 2, '摸鱼事业部', 1, 'mfish', '18900000001', 'mfish@qq.com', '', 0, 0, 'admin', '2023-06-28 10:26:38', NULL, NULL);
INSERT INTO `sso_org` VALUES ('a2569f85d5cd316f5c7011e43dc86271', '404542be62d21014451808aa67f1f5df', NULL, NULL, '0000600001', 2, 'XXX开发部', 1, NULL, NULL, NULL, NULL, 0, 0, 'mfish', '2023-06-28 11:23:47', NULL, NULL);
INSERT INTO `sso_org` VALUES ('af30f104a622b5de76add210dd33b361', '8b7bbf1cac1af7faca6dd2686981fb22', NULL, '', '000050000100004', 3, '摸鱼运维部', 3, '', '', '', '', 0, 0, 'admin', '2023-06-28 11:48:25', 'mfish', '2023-07-03 15:48:34');

-- ----------------------------
-- Table structure for sso_org_role
-- ----------------------------
DROP TABLE IF EXISTS `sso_org_role`;
CREATE TABLE `sso_org_role`  (
  `role_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
  `org_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织ID',
  PRIMARY KEY (`role_id`, `org_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色组织关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_org_role
-- ----------------------------
INSERT INTO `sso_org_role` VALUES ('210297727b74ecb505c1b4d97f76daee', '76f3d1d15272858c2523f287aea955f9');
INSERT INTO `sso_org_role` VALUES ('4063404b06e967bcf619bf86e7fe6359', '45ee8344ffb942d6f2ee1f868b584a99');
INSERT INTO `sso_org_role` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '404542be62d21014451808aa67f1f5df');
INSERT INTO `sso_org_role` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', 'af30f104a622b5de76add210dd33b361');
INSERT INTO `sso_org_role` VALUES ('86ec59a2a3261fd6ba4098da034965ad', 'a2569f85d5cd316f5c7011e43dc86271');
INSERT INTO `sso_org_role` VALUES ('a787082d9b7c177439a114995e4caff1', '624edadc0562c7fe9b54f0e3ba5e658b');

-- ----------------------------
-- Table structure for sso_org_user
-- ----------------------------
DROP TABLE IF EXISTS `sso_org_user`;
CREATE TABLE `sso_org_user`  (
  `org_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织ID',
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`org_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色用户关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_org_user
-- ----------------------------
INSERT INTO `sso_org_user` VALUES ('1', '1');
INSERT INTO `sso_org_user` VALUES ('404542be62d21014451808aa67f1f5df', 'c51fde3955594074bb4db31e654a4483');
INSERT INTO `sso_org_user` VALUES ('624edadc0562c7fe9b54f0e3ba5e658b', 'c51fde3955594074bb4db31e654a4483');
INSERT INTO `sso_org_user` VALUES ('a2569f85d5cd316f5c7011e43dc86271', 'bd54e030ae204be8b77e36cf48583f35');

-- ----------------------------
-- Table structure for sso_role
-- ----------------------------
DROP TABLE IF EXISTS `sso_role`;
CREATE TABLE `sso_role`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
  `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  `role_sort` int(4) NULL DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态 0正常 1停用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_role
-- ----------------------------
INSERT INTO `sso_role` VALUES ('0', '1', '个人', 'person', NULL, 0, NULL, 0, 'admin', '2023-07-19 15:26:04', '', NULL);
INSERT INTO `sso_role` VALUES ('1', '1', '超级管理员', 'superAdmin', 1, 0, '超级管理员', 0, 'admin', '2022-09-19 10:21:49', 'mfish', '2023-07-04 11:22:32');
INSERT INTO `sso_role` VALUES ('210297727b74ecb505c1b4d97f76daee', '1', '测试', 'test', 3, 0, '测试角色', 0, 'admin', '2022-11-29 18:37:32', 'mfish', '2023-07-05 11:13:10');
INSERT INTO `sso_role` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'a480b6861ca4a44631af794a99e77265', 'XXX管理员', 'xxxgly', 1, 0, NULL, 0, 'mfish', '2023-06-28 11:22:07', '', NULL);
INSERT INTO `sso_role` VALUES ('4063404b06e967bcf619bf86e7fe6359', 'a480b6861ca4a44631af794a99e77265', 'XXX运维', 'xxxyw', 2, 0, NULL, 0, 'mfish', '2023-06-28 11:22:46', 'mfish', '2023-07-05 11:43:00');
INSERT INTO `sso_role` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '1', '管理', 'manage', 2, 0, '管理', 0, 'admin', '2023-05-30 23:41:46', 'admin', '2023-09-01 16:12:23');
INSERT INTO `sso_role` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '1', '运维', 'operate', 4, 0, '运维角色', 0, 'admin', '2022-11-30 16:18:51', 'mfish', '2023-07-04 17:25:48');
INSERT INTO `sso_role` VALUES ('86ec59a2a3261fd6ba4098da034965ad', 'a480b6861ca4a44631af794a99e77265', 'XXX开发', 'xxxkf', 3, 0, NULL, 0, 'mfish', '2023-06-28 11:23:10', 'mfish', '2023-09-14 16:08:27');
INSERT INTO `sso_role` VALUES ('a787082d9b7c177439a114995e4caff1', '1', '开发', 'develop', 5, 0, '开发角色', 0, 'admin', '2023-06-28 11:54:49', 'mfish', '2023-07-31 15:16:31');

-- ----------------------------
-- Table structure for sso_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sso_role_menu`;
CREATE TABLE `sso_role_menu`  (
  `role_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
  `menu_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_role_menu
-- ----------------------------
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', '6d71e92a5a3712acbcfcc58f65b93f4f');
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', '75882dc140444e061741fbd9f026dd2b');
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', '76f149981f1c86fce81f2f4cdb9674b9');
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', '7e690410346c4d3a1610d85e8c9f906b');
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', 'f4a0ed4ca7a609aa8268399bdffcecfb');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '0f5a85a6fd5bdc9df26b826eec3c17f1');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '101cb161536a5a80731a4d6db0b5eeac');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '1a73215261f568088e9adeef2dbd8e44');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '25a5783ea03e26d7844b9b7370576236');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '2628fb3c4166d6469f06fcea9b9c0c55');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '2a4e024fdc76063da32926c63ca9ead2');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '369de1bef8d1e964414f25ec6d3156bc');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '3a74d165bcd286f102e10a1be8c23eef');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '3c4b2d0f7558d7f45a29fd9c6a7edea7');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '3d1efa154266719e6322808064df4b13');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '43ca0b3ba70c7ba5a2f91882f618208b');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '4ef7029abe93c11601678ba16dac406f');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '5271166ced06a95d787dc049d3f19bd2');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '58efbcc5f46b95aeab069076031959e7');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '5ad16d38964bf541b6417b07ddf33d9b');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '60efb66a88ab33b339718eb0d052a033');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '67dfbce31013ada62800425f72997962');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '6ac6bc8054107436e24356e3466f00db');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '6aee07bfe60f4ee4021bfce397a8f4df');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '6d71e92a5a3712acbcfcc58f65b93f4f');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '6fd5cdaf86772d4db0587f3b9281f99b');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '731738ed9bbd2e36456b790dfadcb84e');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '75882dc140444e061741fbd9f026dd2b');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '76f149981f1c86fce81f2f4cdb9674b9');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '79cbc9e257ee8f44db6b133c584ff86a');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '7e690410346c4d3a1610d85e8c9f906b');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '80526081fb00ce5dbe629ef358231909');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '81ecfe903cb3116f00c367678059c87c');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '93190e5d426f69cd2712aac373542698');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '967795af502129d318899a60716da84f');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '9840528e4a1efd3a510d8a087baf4bdb');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '9a293c164762776e0a876323a3363dec');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '9b9139c09668bb22888201b7e8a812c4');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '9f46c219e3fc35b1c2ef3a95438b16bf');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'a27822a74728632e0e0ed10d8285bf54');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'a2cdbac934bae3da9987df0655db2455');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'a7145a05342033be0caa4a8f1e262f8a');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'a988f38821885f8f8aaffa49d681aaac');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'addeaf01bc278e216de75ad26a8f27b6');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'b40f985b74d1dd6e311e21369beea8ea');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'bf2e6661d9a4d115f018ffd4ff202d92');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'c46042d6e6d16ea95df6461648833675');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'c487023e85c9aaf5510a03e8017b768c');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'c52a49da263d57d2c89edcbc9ca70a0a');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'c9eb585420911ee18335d935d3872934');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'ce1a05cdedf2d0684574a30dd3ed14f9');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'ce71591e16d47d6b4ff1d52c2bb83ae7');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'cfbdf3ce5297cebf806ac116fc239558');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'cfc0ca0b3c9220fa3cae26420cf0c51f');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'e48e7b98d2c0331ff6241514e97dad8b');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'ee3ae3a2161e8d58e2c62f340c3d7b55');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'f4a0ed4ca7a609aa8268399bdffcecfb');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'f634ba1d6d840fb1f945b4f811dd928d');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'f87d8b297eb3650834048dba7c8d2d89');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'fb5dac5b0b9b610ed1e996108d6445b0');
INSERT INTO `sso_role_menu` VALUES ('4063404b06e967bcf619bf86e7fe6359', '0aa9f017545ec947a075f76e34c075c0');
INSERT INTO `sso_role_menu` VALUES ('4063404b06e967bcf619bf86e7fe6359', '6d71e92a5a3712acbcfcc58f65b93f4f');
INSERT INTO `sso_role_menu` VALUES ('4063404b06e967bcf619bf86e7fe6359', '7e690410346c4d3a1610d85e8c9f906b');
INSERT INTO `sso_role_menu` VALUES ('4063404b06e967bcf619bf86e7fe6359', 'e159379c94b8fcc58ebc38cf8b322772');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '0526179a70fca38a69dd709dec2f1a81');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '0744327e98f399788bb030efb1c0b637');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '08a567f09c8d90660c23f2b432e0e1d9');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '0913e14334f59671ae6abfdfce5e0559');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '0aa9f017545ec947a075f76e34c075c0');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '101cb161536a5a80731a4d6db0b5eeac');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '1a0aee8380c525e7c4802b1c4d587fa8');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '1ce2ac44228e37c063e9cd55ed8f0a49');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '1e1b7d50ab93ffdeca33fe5b7006eb01');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '1eb28fcc2c9e009ddf9accec8be4665d');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '204217f09c1206f9d5b9725c1e05604a');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '234dc900ad6502579a51784f9ddb05d5');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '238132a09b6f761374dfd205b6388245');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '23f38246b88faf0826c17d57c0d846af');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '250f8038be816e61956c792fc9ddc2c6');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '25a5783ea03e26d7844b9b7370576236');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '2628fb3c4166d6469f06fcea9b9c0c55');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '268d140daddc00dc77823c7d7c2025fb');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '285bb37f32e878e24d5a22445c1bc5af');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '2bf2996b88ae8cd70e91685e5e95d4f7');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '369de1bef8d1e964414f25ec6d3156bc');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '37603c7afb87a9d94d03b06730cb0d29');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '3a74d165bcd286f102e10a1be8c23eef');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '3a92fd411f0a70bf477e6dc354f4e29e');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '3c4b2d0f7558d7f45a29fd9c6a7edea7');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '43f57faab0a1d54cc5130d8a3cc9594a');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '442f89857faa6bc929ef4f422b8c4b99');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '494d219f20ef782becd6b75c899ba4b5');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '4cabb05e97ea7a738a2f7ce3c9d224d8');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '4d1e926fd6767a786b1cab58e3bc5624');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '503e3ac379a2e17e99105b77a727e6db');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '5271166ced06a95d787dc049d3f19bd2');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '53e8eaceee36c1d54e43319fdd60811b');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '573c955ed2c2ffdea2654487cdfdc520');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '58c1fc9fde9ad2a37fa366a9a91f094d');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '5a5a25e8923ace2700133ab997af0e80');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '5ad16d38964bf541b6417b07ddf33d9b');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '5b543a83371c766788047a1a1907cffd');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '5c723efc132b50c0284d79eaafed5a0f');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '5d9ff19ac80d4899ea469e4d4b9a6aeb');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '67dfbce31013ada62800425f72997962');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '6858a7df953aa4a3bbab3bd77e03b1fb');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '6a38a3847b66cc690c3a2eacedb4e81f');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '6ac6bc8054107436e24356e3466f00db');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '6d71e92a5a3712acbcfcc58f65b93f4f');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '6e02e0e621140968dc62a2ce3dfa198d');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '6e491486dc4cb475e4bd037d06ab2801');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '731738ed9bbd2e36456b790dfadcb84e');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '75882dc140444e061741fbd9f026dd2b');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '76f149981f1c86fce81f2f4cdb9674b9');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '76f68d05f5054818762718ee85d6d0fe');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '79cbc9e257ee8f44db6b133c584ff86a');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '7b5fc202b06024a9e3920bc57820a954');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '7e690410346c4d3a1610d85e8c9f906b');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '7e87849f80699ad24292fd9908f5aeb8');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '80526081fb00ce5dbe629ef358231909');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '81ecfe903cb3116f00c367678059c87c');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '8360ab9544a00dc7d9f15594dd69e2ff');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '85cd52250e435c555622c268262f4c02');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '93190e5d426f69cd2712aac373542698');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '967795af502129d318899a60716da84f');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '9c6f4eff70d7b2048f63adf229c5d30d');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '9d5397b6ddb4d194a95b05f42b80445b');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '9f46c219e3fc35b1c2ef3a95438b16bf');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'a43c057f48b54c9038719179cf9e284d');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'a455186424982517f128da41f2c159d9');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'ac7eb51ce5e700aa268464b9fd052624');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'ad2ab62e13c7750dcb5b41b00cbdcf66');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'afbf598d2290b24a5e87e7547c05515d');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'b33532a555157cddf48628879433e844');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'b54e001d27b804b769564f35430193b2');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'b8d42dd7c44f8bfba043ec2641aa0442');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'bb26a48e941f30b9453bf90cba8eb785');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'bcd18784374699438a215a9ab1e9b351');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'bf2e6661d9a4d115f018ffd4ff202d92');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'bff2e13c8ef26ffc98cb4941b2390316');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'c331e32be60017fc936703d3da235ddb');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'c36420436629884000e73b158166f260');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'c52a49da263d57d2c89edcbc9ca70a0a');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'ca0a3c9ae9cd551ee4e1b727861b7c78');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'ce1a05cdedf2d0684574a30dd3ed14f9');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'cea175c6cef1506173dcc579fe42572c');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'e159379c94b8fcc58ebc38cf8b322772');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'e1996e92ac6cf37c0c2e40825a7af472');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'e862cd0f17439fe26950e5ecb9492cc2');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'e909edc44910691dedc2c5338ec0e603');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'eb5f513d5430597d3ea312e1bf760b23');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'ec07264b984c531ee729eda12ada4c49');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'ec812dc5df8d93e478bd22a04d3db770');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'eda0153e492e86ade2fe6702d267fef3');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'ee34f5aec6a2220f57fa151a147ede3c');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'eeda5a7b6e2868e5e25a1e794d5786db');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'f193aa905e7aceb1e4b0187d8b63339f');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'f4a0ed4ca7a609aa8268399bdffcecfb');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'f634ba1d6d840fb1f945b4f811dd928d');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'f87d8b297eb3650834048dba7c8d2d89');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'fa2211276b7b84a141667ec9ea8d33a4');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'fb5dac5b0b9b610ed1e996108d6445b0');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '604277501ebb1c92d70912c49188eaa5');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '0aa9f017545ec947a075f76e34c075c0');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', 'e159379c94b8fcc58ebc38cf8b322772');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '01ee419d3dcc28acf1920428391720b7');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '285bb37f32e878e24d5a22445c1bc5af');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '292791abfbf63b35ad9ce510c0a5823e');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '369de1bef8d1e964414f25ec6d3156bc');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '5ad16d38964bf541b6417b07ddf33d9b');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '6aee07bfe60f4ee4021bfce397a8f4df');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '6cedf51376c8998eb49e280cdba6c533');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '6d71e92a5a3712acbcfcc58f65b93f4f');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '70943d8248fd8f77ade038d9afa0bf33');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '7e690410346c4d3a1610d85e8c9f906b');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '81ecfe903cb3116f00c367678059c87c');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '8360ab9544a00dc7d9f15594dd69e2ff');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '85cd52250e435c555622c268262f4c02');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', 'a606083b203d32915c4d0e649c7b7c6b');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', 'afbf598d2290b24a5e87e7547c05515d');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', 'eda0153e492e86ade2fe6702d267fef3');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '0526179a70fca38a69dd709dec2f1a81');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '1a0aee8380c525e7c4802b1c4d587fa8');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '1ce2ac44228e37c063e9cd55ed8f0a49');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '292791abfbf63b35ad9ce510c0a5823e');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '5c723efc132b50c0284d79eaafed5a0f');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '6cedf51376c8998eb49e280cdba6c533');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '6d71e92a5a3712acbcfcc58f65b93f4f');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '6e491486dc4cb475e4bd037d06ab2801');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '70943d8248fd8f77ade038d9afa0bf33');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '7e690410346c4d3a1610d85e8c9f906b');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '8360ab9544a00dc7d9f15594dd69e2ff');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '85cd52250e435c555622c268262f4c02');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', 'a606083b203d32915c4d0e649c7b7c6b');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', 'ee34f5aec6a2220f57fa151a147ede3c');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', 'fa2211276b7b84a141667ec9ea8d33a4');
INSERT INTO `sso_role_menu` VALUES ('e77a579aaf0844ba493cde1811b137a9', '6d71e92a5a3712acbcfcc58f65b93f4f');
INSERT INTO `sso_role_menu` VALUES ('e77a579aaf0844ba493cde1811b137a9', '7e690410346c4d3a1610d85e8c9f906b');
INSERT INTO `sso_role_menu` VALUES ('e77a579aaf0844ba493cde1811b137a9', 'eb5f513d5430597d3ea312e1bf760b23');

-- ----------------------------
-- Table structure for sso_tenant
-- ----------------------------
DROP TABLE IF EXISTS `sso_tenant`;
CREATE TABLE `sso_tenant`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `tenant_type` tinyint(4) NULL DEFAULT NULL COMMENT '租户类型 0 个人 1 企业',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户名称',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '城市',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省份',
  `county` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区县',
  `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址',
  `corp_size` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公司规模',
  `corp_years` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '营业年限',
  `trade` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属行业',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态 0正常 1注销',
  `logo` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'logo',
  `domain` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '域名',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除状态(0-正常,1-已删除)',
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ID，关联用户为管理员',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '租户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_tenant
-- ----------------------------
INSERT INTO `sso_tenant` VALUES ('1', 1, '系统默认', NULL, NULL, NULL, '南京市秦淮区', 'l', '10', '科技推广和应用服务业', 0, 'bcfe86a96a6543ccac0b1700ad3aa5ca.png', 'http://www.mfish.com.cn', 0, '1', 'admin', '2023-06-20 23:30:00', 'admin', '2023-07-05 15:27:04');
INSERT INTO `sso_tenant` VALUES ('a480b6861ca4a44631af794a99e77265', 1, '南京XXX公司', NULL, NULL, NULL, '江苏南京XXX区XX号', 'xs', '3', '软件和信息技术服务业', 0, '5131c2efe1c94c3489815ff7d1ad0bed.png', 'http://mfish.com.cn', 0, 'c51fde3955594074bb4db31e654a4483', 'admin', '2023-06-28 11:17:09', 'admin', '2023-07-05 16:12:20');

-- ----------------------------
-- Table structure for sso_user
-- ----------------------------
DROP TABLE IF EXISTS `sso_user`;
CREATE TABLE `sso_user`  (
                             `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
                             `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
                             `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
                             `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
                             `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
                             `old_password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '旧密码',
                             `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
                             `head_img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
                             `telephone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电话',
                             `birthday` date NULL DEFAULT NULL COMMENT '生日',
                             `sex` tinyint(1) NULL DEFAULT 1 COMMENT '性别(1男0女)',
                             `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态 0正常 1停用',
                             `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标记(0未删除1删除)',
                             `salt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '盐',
                             `openid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信唯一id',
                             `gitee` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'gitee账号',
                             `github` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'github账号',
                             `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                             `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
                             `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `account_index`(`account`) USING BTREE,
                             UNIQUE INDEX `openid_index`(`openid`) USING BTREE,
                             UNIQUE INDEX `phone_index`(`phone`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_user
-- ----------------------------
INSERT INTO `sso_user` VALUES ('1', 'admin', '18900000001', 'mfish@qq.com', '22d374999f108f1573aad145657ed698', '59a95bba2326ae3d8ac106c5b4b878bb,4fbc6a94d1a2b011c75b2131e15d804b,11417affd06d7fcac06ebfed5c5db529,bfb6e947f7c42a4310adc670e4a9dbd8,5917114d326346f25b8c8d92b6146b14', '管理员', '77f39fdc429142bf9b2b1fff631098ef.png', '02512345678', '1984-08-08', 1, 0, 0, '452187570f682f2ddb35a216fd32460d', 'olbL54qA8qAccFNtModx6dM-Ha6w', NULL, NULL, '超级管理员', '', '2017-04-10 15:21:38', 'admin', '2024-10-31 21:36:49');
INSERT INTO `sso_user` VALUES ('bd54e030ae204be8b77e36cf48583f35', 'xzh', '18911111111', 'liudehua@qq.com', 'b16e23b22364b73715fe678b8493a250', NULL, '新租户', '08170556fd804908812a2768380896e3.png', NULL, NULL, 1, 0, 0, '57c61f5a32e75728843b7f6d0cf38cb1', NULL, NULL, NULL, NULL, 'mfish', '2023-07-04 10:06:06', 'admin', '2024-04-20 21:03:42');
INSERT INTO `sso_user` VALUES ('c51fde3955594074bb4db31e654a4483', 'mfish', '18900000002', 'mfish2@qq.com', '997ba196adf807d32b030d2ee8e242f9', '6005f46907b74937313bb9a160be19cb,4fb38998631db300c31f4fff5e19fd6d,ad2671b4ae7c57182cb0378b58c6d122,6a4bee1d4715fb50761e05a6a8c6667e,ff596b034c5929a131c18cf2c124ccbf', '摸鱼', NULL, '', '2023-06-28', 1, 0, 0, '2833ba3ab03c7fb357cf5d71676146e8', NULL, NULL, NULL, '', 'admin', '2023-06-28 11:14:45', 'mfish', '2024-10-31 22:08:04');

-- ----------------------------
-- Table structure for sso_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sso_user_role`;
CREATE TABLE `sso_user_role`  (
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `role_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_user_role
-- ----------------------------
INSERT INTO `sso_user_role` VALUES ('1', '1');
INSERT INTO `sso_user_role` VALUES ('bd54e030ae204be8b77e36cf48583f35', 'e77a579aaf0844ba493cde1811b137a9');

-- ----------------------------
-- Table structure for sys_code_build
-- ----------------------------
DROP TABLE IF EXISTS `sys_code_build`;
CREATE TABLE `sys_code_build`  (
  `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '唯一ID',
  `connect_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库连接ID',
  `table_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表名',
  `api_prefix` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接口路径前缀 例如:/oauth2/user接口前缀为oauth2(不传会使用packageName，最底层包名 例如:cn.com.mfish.sys包会使用sys)',
  `entity_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '实体类名(不传会使用表名驼峰化)',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目包名(不传使用默认包名 cn.com.mfish.sys)',
  `table_comment` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表描述(不传会获取数据库表中的中文描述，如果也为空则使用表名)',
  `query_params` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Form查询条件(json串)',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码构建' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一ID',
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '配置信息',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '配置类型(0 样式配置 1表格配置)',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统皮肤及配置' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for sys_db_connect
-- ----------------------------
DROP TABLE IF EXISTS `sys_db_connect`;
CREATE TABLE `sys_db_connect`  (
                                   `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '库信息id',
                                   `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
                                   `db_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库标题',
                                   `db_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库名',
                                   `db_type` tinyint(1) NULL DEFAULT NULL COMMENT '数据库类型（0 mysql 1 oracle 2 pgsql）',
                                   `pool_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '连接池类型(Druid,Hikari)',
                                   `host` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主机',
                                   `port` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '端口号',
                                   `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库登录用户名',
                                   `password` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库登录密码',
                                   `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据源配置项(JSON格式）',
                                   `is_public` tinyint(1) NULL DEFAULT 0 COMMENT '是否公开连接 0为私有的  1公开的',
                                   `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                   `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
                                   `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库连接信息' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
                             `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
                             `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典名称',
                             `dict_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字典编码',
                             `status` int NULL DEFAULT 0 COMMENT '状态(0正常 1停用)',
                             `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                             `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
                             `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`id`, `dict_code`) USING BTREE,
                             UNIQUE INDEX `dict_code_index`(`dict_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES ('06907475960c4f544ccbe7f883b9387a', '租户状态', 'tenant_corp_status', 0, '租户状态', 'admin', '2023-06-13 17:33:43', '', NULL);
INSERT INTO `sys_dict` VALUES ('0bb92966ec419ed35b9cb46979867e69', '时区', 'sys_time_zone', 0, '所在时区', 'admin', '2023-02-21 18:05:37', 'admin', '2023-02-21 18:16:33');
INSERT INTO `sys_dict` VALUES ('193585c2417cfb3e4865501c2e608602', '数据库类型', 'sys_db_type', 0, '支持的数据库类型', 'admin', '2023-01-03 11:45:12', 'admin', '2023-01-03 11:46:12');
INSERT INTO `sys_dict` VALUES ('24f5894e66ca34b9d4aafa2604c94091', '支付类型', 'mall_pay_type', 0, NULL, 'admin', '2024-04-20 18:17:29', 'admin', '2024-04-20 18:17:29');
INSERT INTO `sys_dict` VALUES ('2627066fc33d283c7a1ca4123a31be2d', '请求来源', 'sys_req_source', 0, '请求来源信息', 'admin', '2023-02-02 11:40:32', '', NULL);
INSERT INTO `sys_dict` VALUES ('2fceca32c5098a60071574c61e0327fa', '调度任务状态', 'sys_job_status', 0, '任务执行状态', 'admin', '2023-02-28 16:57:48', '', NULL);
INSERT INTO `sys_dict` VALUES ('3be670398cf94f18c2631ab4df1e714e', '租户类型', 'tenant_corp_type', 0, '租户类型', 'admin', '2023-06-13 17:36:15', '', NULL);
INSERT INTO `sys_dict` VALUES ('4cc4c6a6c90c5534d11ae9d2db12902f', '数据库连接池', 'sys_db_pool', 0, '数据库连接池类型', 'admin', '2023-03-13 11:06:00', '', NULL);
INSERT INTO `sys_dict` VALUES ('55ccc731fe3958c4afca60cbd852a55f', '营业年限', 'tenant_corp_years', 0, '企业营业年限', 'admin', '2023-06-13 17:18:11', 'admin', '2023-06-13 17:33:55');
INSERT INTO `sys_dict` VALUES ('5665dd400700ebea77fcc6f8e39fc355', '请求类型', 'sys_req_type', 0, 'http请求类型', 'admin', '2023-01-09 15:53:31', 'admin', '2023-01-10 17:50:52');
INSERT INTO `sys_dict` VALUES ('5ed7c51e261ce27109e5f4948d40d6e2', 'Hikari连接池配置', 'db_pool_hikari', 0, 'Hikari连接池相关配置信息', 'admin', '2023-03-13 11:18:45', '', NULL);
INSERT INTO `sys_dict` VALUES ('6ddca50d9ddad44806ef18c3bf4721c9', '公式类型', 'nc_formula_type', 0, '对运算公式分类', 'admin', '2023-10-27 09:09:25', 'admin', '2023-10-27 09:09:25');
INSERT INTO `sys_dict` VALUES ('75ce8aab0fca2be5183770260d145c17', '认证方式', 'sso_grant_type', 0, '统一认证grant_type', 'admin', '2023-05-16 21:49:41', '', NULL);
INSERT INTO `sys_dict` VALUES ('81e4b91e932f0a671021a7eca2f665b9', '登录模式', 'sys_login_mode', 0, '登录模式 0 浏览器 1 微信', 'admin', '2023-03-09 14:48:59', '', NULL);
INSERT INTO `sys_dict` VALUES ('8a992d89a43f27fa2dbdf75871bd7e74', '所属行业', 'tenant_corp_trade', 0, '租户所属行业', 'admin', '2023-06-13 17:04:44', 'admin', '2023-06-15 22:32:41');
INSERT INTO `sys_dict` VALUES ('9bb402e2c751b3bf9358ef9acb3caf49', '调度任务', 'sys_job_type', 0, '调度任务类型', 'admin', '2023-02-21 10:44:15', 'admin', '2023-03-13 11:06:05');
INSERT INTO `sys_dict` VALUES ('ad7336dda270e6430565b313a741ffb7', '用户性别', 'sys_user_sex', 0, '用户性别字典', 'admin', '2023-01-04 16:08:35', 'admin', '2023-01-05 14:47:40');
INSERT INTO `sys_dict` VALUES ('c352267b4ec618c1745bdda85f12402e', '组件类型', 'vue_com_type', 0, '前端VUE组件类型', 'admin', '2024-04-19 14:11:33', 'admin', '2024-04-19 14:11:33');
INSERT INTO `sys_dict` VALUES ('c64a91de3cff06e70128938d7cfc2af4', '配送方式', 'mall_delivery_type', 0, NULL, 'admin', '2024-04-20 18:18:52', 'admin', '2024-04-20 18:18:52');
INSERT INTO `sys_dict` VALUES ('c694f0f6feba27026044839b77d24caa', '日志操作', 'sys_log_type', 0, '日志操作类型', 'admin', '2023-01-09 11:27:05', 'admin', '2023-03-13 11:06:13');
INSERT INTO `sys_dict` VALUES ('d2d1bfb80a89199abdedae4874311b69', '订单状态', 'mall_order_status', 0, NULL, 'admin', '2024-04-20 18:11:02', 'admin', '2024-04-20 18:11:02');
INSERT INTO `sys_dict` VALUES ('d42259143fdd344b439fe39d3fffdefe', '企业规模', 'tenant_corp_size', 0, '企业公司规模', 'admin', '2023-06-13 17:10:14', 'admin', '2023-06-13 17:29:14');
INSERT INTO `sys_dict` VALUES ('d42d4e365a7b3d43f8c5cf37a523dbd5', 'Druid连接池配置', 'db_pool_druid', 0, 'Druid连接池相关配置项', 'admin', '2023-03-13 11:24:35', '', NULL);
INSERT INTO `sys_dict` VALUES ('dce08f2d436e6582ec58301a76fe11fa', '任务过期策略', 'sys_job_misfire', 0, '任务过期处理策略 1立即处理一次 2放弃处理', 'admin', '2023-02-21 11:22:32', 'admin', '2023-02-21 18:07:12');
INSERT INTO `sys_dict` VALUES ('e8e6e4c3f8e2fb775d6d083883e41839', '代码生成查询条件', 'sys_code_condition', 0, '代码生成的查询条件', 'admin', '2023-05-10 16:11:44', '', NULL);
INSERT INTO `sys_dict` VALUES ('fb787f5398a1ad30463d4c6a263d3b89', 'API数据来源', 'api_data_source', 0, 'API数据来源', 'admin', '2024-09-25 14:23:14', 'admin', '2024-09-25 14:23:14');
INSERT INTO `sys_dict` VALUES ('b0f0a03f9e879c2ce3b7722a3cd01061', 'HTTP请求类型', 'http_content_type', 0, NULL, 'admin', '2025-07-22 14:33:21', 'admin', '2025-07-22 14:33:21');
INSERT INTO `sys_dict` VALUES ('4a9914bd42939163ceb9c5b4002cfa66', 'HTTP请求方式', 'http_request_method', 0, NULL, 'admin', '2025-07-22 14:30:25', 'admin', '2025-07-22 14:30:25');
INSERT INTO `sys_dict` VALUES ('220bbd3b1dd32fd37d0abbd279a14774', '工作流任务状态', 'workflow_task_status', 0, '工作流任务状态字典', 'admin', '2025-10-10 16:27:01', 'admin', '2025-10-10 16:27:01');

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`  (
                                  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
                                  `dict_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典ID',
                                  `dict_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字典编码',
                                  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典标签',
                                  `dict_value` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典键值',
                                  `value_type` tinyint(1) NULL DEFAULT 0 COMMENT '值类型(0字符 1数字 2布尔)',
                                  `dict_sort` int NULL DEFAULT 0 COMMENT '字典排序',
                                  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
                                  `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配色(用于前端显示)',
                                  `status` int NULL DEFAULT 0 COMMENT '状态(0正常 1停用)',
                                  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
                                  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
                                  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  INDEX `dict_code_index`(`dict_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO `sys_dict_item` VALUES ('026de48a5c41ce981d0f941b510120b0', '6ddca50d9ddad44806ef18c3bf4721c9', 'nc_formula_type', '数字运算', 'num_op', 0, 2, NULL, 'green', 0, '', 'admin', '2023-10-27 09:17:47', 'admin', '2023-10-27 14:21:53');
INSERT INTO `sys_dict_item` VALUES ('027eb9340eafdcf78cc9b96cc211b6b3', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'maxEvictableIdleTimeMillis', '900000', 1, 7, NULL, NULL, 0, '配置一个连接在池中最大生存的时间，单位是毫秒', 'admin', '2023-03-13 11:27:11', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('0475df296a072fdb2584162443049aef', 'c352267b4ec618c1745bdda85f12402e', 'vue_com_type', '下拉选择框', 'ApiSelect', 0, 3, NULL, NULL, 0, NULL, 'admin', '2024-04-19 14:13:55', 'admin', '2024-04-19 14:14:58');
INSERT INTO `sys_dict_item` VALUES ('0625b88c24a5cd98e045a58679370249', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '新增', '新增', 0, 3, NULL, 'blue', 0, NULL, 'admin', '2023-01-09 11:27:52', 'admin', '2023-01-09 16:11:41');
INSERT INTO `sys_dict_item` VALUES ('0737910a2d8f6eb55fe12e2a9be5032b', '0bb92966ec419ed35b9cb46979867e69', 'sys_time_zone', '日本/东京', 'Asia/Tokyo', 0, 3, NULL, '', 0, '', 'admin', '2023-02-21 18:09:59', 'admin', '2023-02-21 18:16:33');
INSERT INTO `sys_dict_item` VALUES ('07fb6036ee052bc745db8b58234159e9', 'e8e6e4c3f8e2fb775d6d083883e41839', 'sys_code_condition', '以开始', 'likeLeft', 0, 3, NULL, NULL, 0, NULL, 'admin', '2023-05-10 16:13:25', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('0844e38fdb4aabbac30f365812e8457f', '06907475960c4f544ccbe7f883b9387a', 'tenant_corp_status', '正常', '0', 1, 1, NULL, 'green', 0, NULL, 'admin', '2023-06-13 17:34:26', 'admin', '2023-06-13 17:37:06');
INSERT INTO `sys_dict_item` VALUES ('0cc6971a3031faf34525bafae3609f0d', '0bb92966ec419ed35b9cb46979867e69', 'sys_time_zone', '中国/香港', 'Asia/Hong_Kong', 0, 2, NULL, '', 0, '', 'admin', '2023-02-21 18:13:04', 'admin', '2023-02-21 18:16:33');
INSERT INTO `sys_dict_item` VALUES ('0f11bb06b93b05e27f8a45d78765a243', '193585c2417cfb3e4865501c2e608602', 'sys_db_type', 'mysql', '0', 1, 1, NULL, 'blue', 0, 'mysql数据库', 'admin', '2023-01-03 16:40:55', 'admin', '2023-03-13 11:16:59');
INSERT INTO `sys_dict_item` VALUES ('11ea1ab1605e7c6271a0ccdfb750242e', '8a992d89a43f27fa2dbdf75871bd7e74', 'tenant_corp_trade', '公共设施管理业', '公共设施管理业', 0, 4, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:07:56', 'admin', '2023-06-15 22:32:40');
INSERT INTO `sys_dict_item` VALUES ('1622fbd6504ffe02790b63540b8e693f', 'c352267b4ec618c1745bdda85f12402e', 'vue_com_type', '数字输入框', 'InputNumber', 0, 2, NULL, NULL, 0, NULL, 'admin', '2024-04-19 14:14:54', 'admin', '2024-04-19 14:14:54');
INSERT INTO `sys_dict_item` VALUES ('194522e5912d137781d6c2659d2c7c65', '8a992d89a43f27fa2dbdf75871bd7e74', 'tenant_corp_trade', '科技推广和应用服务业', '科技推广和应用服务业', 0, 2, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:06:31', 'admin', '2023-06-15 22:32:40');
INSERT INTO `sys_dict_item` VALUES ('1f00232f374a51bb9e38df469e95ca59', 'd42259143fdd344b439fe39d3fffdefe', 'tenant_corp_size', '500人以上', 'l', 0, 4, NULL, '', 0, '', 'admin', '2023-06-13 17:12:05', 'admin', '2023-06-13 17:29:14');
INSERT INTO `sys_dict_item` VALUES ('2065051b945805f8915ea68431c097ff', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '查询', '查询', 0, 2, NULL, 'green', 0, NULL, 'admin', '2023-01-09 11:27:46', 'admin', '2023-01-09 15:21:07');
INSERT INTO `sys_dict_item` VALUES ('21497ae862c8446d8721d21176fe677e', '2627066fc33d283c7a1ca4123a31be2d', 'sys_req_source', '其他', '0', 1, 3, NULL, 'orange', 0, '其他来源', 'admin', '2023-02-02 11:41:01', 'admin', '2023-02-21 18:00:30');
INSERT INTO `sys_dict_item` VALUES ('2495904b9a026be7eed628988cc584f7', '55ccc731fe3958c4afca60cbd852a55f', 'tenant_corp_years', '三年以内', '3', 0, 1, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:20:46', 'admin', '2023-06-13 17:31:53');
INSERT INTO `sys_dict_item` VALUES ('24e063bd1bd5e0dd6c433682c277d42e', 'dce08f2d436e6582ec58301a76fe11fa', 'sys_job_misfire', '放弃执行', '2', 1, 2, NULL, 'red', 0, '不执行过期任务', 'admin', '2023-02-21 11:23:42', 'admin', '2023-02-21 18:07:12');
INSERT INTO `sys_dict_item` VALUES ('261b1fdc50de63e495cdb757b6d430e2', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '删除', '删除', 0, 5, NULL, 'red', 0, NULL, 'admin', '2023-01-09 11:28:05', 'admin', '2023-01-09 15:23:00');
INSERT INTO `sys_dict_item` VALUES ('294a4554e02894c7dfe306df3051a03e', 'dce08f2d436e6582ec58301a76fe11fa', 'sys_job_misfire', '立即执行', '1', 1, 1, NULL, 'green', 0, '判断为过期后立即执行一次', 'admin', '2023-02-21 11:23:27', 'admin', '2023-02-21 18:07:12');
INSERT INTO `sys_dict_item` VALUES ('29cc4901b0f367b8f0f42582ae64c4c3', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'initialSize', '2', 1, 1, NULL, NULL, 0, '初始连接数', 'admin', '2023-03-13 11:25:14', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('2a0c3b13464ea03b8b9c3a9d07295dde', 'c64a91de3cff06e70128938d7cfc2af4', 'mall_delivery_type', '配送点', '0', 1, 0, NULL, 'green', 0, NULL, 'admin', '2024-04-20 18:19:18', 'admin', '2024-04-20 18:19:43');
INSERT INTO `sys_dict_item` VALUES ('2adbaf3195bbab50fbd100ed6185e513', '2627066fc33d283c7a1ca4123a31be2d', 'sys_req_source', '后台用户', '1', 1, 1, NULL, 'blue', 0, '后台用户', 'admin', '2023-02-02 11:41:19', 'admin', '2023-02-21 18:00:23');
INSERT INTO `sys_dict_item` VALUES ('31552cd65ae98d64265506ef05b00907', '4cc4c6a6c90c5534d11ae9d2db12902f', 'sys_db_pool', 'Hikari', 'db_pool_hikari', 0, 2, NULL, 'green', 0, 'spring默认连接池', 'admin', '2023-03-13 11:11:24', 'admin', '2023-03-17 10:20:23');
INSERT INTO `sys_dict_item` VALUES ('317328ed4f375a6591283f10e0e1cec6', '0bb92966ec419ed35b9cb46979867e69', 'sys_time_zone', '美国/纽约', 'America/New_York', 0, 5, NULL, '', 0, '', 'admin', '2023-02-21 18:11:07', 'admin', '2023-02-21 18:16:33');
INSERT INTO `sys_dict_item` VALUES ('32438cecdad31c99e71000e3097d266f', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'maxActive', '10', 1, 3, NULL, NULL, 0, '最大连接池数', 'admin', '2023-03-13 11:25:47', 'admin', '2023-03-17 16:27:50');
INSERT INTO `sys_dict_item` VALUES ('327e9856baf76954f9a968c7ccc0eb02', 'c64a91de3cff06e70128938d7cfc2af4', 'mall_delivery_type', '物流', '2', 1, 2, NULL, 'cyan', 0, '', 'admin', '2024-04-20 18:20:00', 'admin', '2024-04-20 18:20:00');
INSERT INTO `sys_dict_item` VALUES ('32a0802dae2dfb32857efed0f4a6663f', '2fceca32c5098a60071574c61e0327fa', 'sys_job_status', '执行成功', '3', 1, 4, NULL, 'green', 0, '执行成功', 'admin', '2023-03-01 17:59:34', 'admin', '2023-03-01 18:00:11');
INSERT INTO `sys_dict_item` VALUES ('34b308bff053bbff3f82554565e0f8f8', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'maxPoolPreparedStatementPerConnectionSize', '20', 1, 12, NULL, '', 0, '游标缓存大小', 'admin', '2023-03-13 11:30:31', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('3cd57f7e97d9bde410da9f2199dc35c5', '2fceca32c5098a60071574c61e0327fa', 'sys_job_status', '调度成功', '1', 1, 2, NULL, 'green', 0, NULL, 'admin', '2023-02-28 16:58:32', 'admin', '2023-03-01 17:59:00');
INSERT INTO `sys_dict_item` VALUES ('3cd68c5687d190e7b0a6242b162424a7', '5665dd400700ebea77fcc6f8e39fc355', 'sys_req_type', 'OPTIONS', 'OPTIONS', 0, 5, NULL, 'cyan', 0, '测试服务器的功能性', 'admin', '2023-01-10 18:01:42', 'admin', '2023-02-21 18:02:30');
INSERT INTO `sys_dict_item` VALUES ('3e34820c81d8a62752188740b22a3ebc', '9bb402e2c751b3bf9358ef9acb3caf49', 'sys_job_type', '本地任务', '0', 1, 1, NULL, 'green', 0, '实现方法写在调度中心的任务', 'admin', '2023-02-21 10:44:46', 'admin', '2023-02-21 17:44:35');
INSERT INTO `sys_dict_item` VALUES ('429ef23795434df72a0800b93399dd58', '9bb402e2c751b3bf9358ef9acb3caf49', 'sys_job_type', '消息任务', '2', 1, 3, NULL, 'orange', 0, '通过MQ消息发送任务，消费端处理', 'admin', '2023-02-21 10:48:33', 'admin', '2023-02-21 17:44:42');
INSERT INTO `sys_dict_item` VALUES ('42c2db7dc772a5d4026a9d5b59348417', '193585c2417cfb3e4865501c2e608602', 'sys_db_type', 'postgresql', '1', 1, 2, NULL, 'cyan', 0, 'pg数据库', 'admin', '2023-03-13 11:03:25', 'admin', '2023-03-24 23:02:04');
INSERT INTO `sys_dict_item` VALUES ('4447b34d5b8ac40fb762fdcd71dfe0b2', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'testOnBorrow', 'false', 2, 9, NULL, '', 0, '当应用向连接池申请连接时，连接池会判断这条连接是否是可用的', 'admin', '2023-03-13 11:28:13', 'admin', '2023-03-14 19:41:21');
INSERT INTO `sys_dict_item` VALUES ('449f7f3ff1bf55ddb8159f5d122e29c4', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '其他操作', '其他操作', 0, 1, NULL, 'default', 0, NULL, 'admin', '2023-01-09 11:27:39', 'admin', '2023-02-21 18:03:01');
INSERT INTO `sys_dict_item` VALUES ('4795a7072f4213eeeee4d8bca2531dc8', 'e8e6e4c3f8e2fb775d6d083883e41839', 'sys_code_condition', '以结束', 'likeRight', 0, 4, NULL, NULL, 0, NULL, 'admin', '2023-05-10 16:13:39', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('4a84be1783171797e3cf1712f014b461', '0bb92966ec419ed35b9cb46979867e69', 'sys_time_zone', '中国/上海', 'Asia/Shanghai', 0, 1, NULL, NULL, 0, NULL, 'admin', '2023-02-21 18:05:55', 'admin', '2023-02-21 18:16:33');
INSERT INTO `sys_dict_item` VALUES ('4f9003f0afecb5a68b22f05d1e1ea0e7', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'testOnReturn', 'false', 2, 10, NULL, '', 0, '归还连接时是否会进行检查，检查不通过，销毁', 'admin', '2023-03-13 11:28:34', 'admin', '2023-03-14 19:44:29');
INSERT INTO `sys_dict_item` VALUES ('515e0a34aec2beea8733d83083c53ea5', '2fceca32c5098a60071574c61e0327fa', 'sys_job_status', '开始', '0', 1, 1, NULL, 'blue', 0, NULL, 'admin', '2023-02-28 16:58:12', 'admin', '2023-02-28 17:04:30');
INSERT INTO `sys_dict_item` VALUES ('52789011153ea396227659c1bc18d594', 'd2d1bfb80a89199abdedae4874311b69', 'mall_order_status', '已废弃', '4', 1, 5, NULL, '#FF4D4F', 0, '', 'admin', '2024-04-20 18:13:34', 'admin', '2024-04-20 18:16:58');
INSERT INTO `sys_dict_item` VALUES ('577d84183407984f104f3b71509623b8', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'testWhileIdle', 'true', 2, 8, NULL, NULL, 0, '当应用向连接池申请连接，并且testOnBorrow为false时，连接池将会判断连接是否处于空闲状态，如果是，则验证这条连接是否可用。', 'admin', '2023-03-13 11:27:33', 'admin', '2023-03-14 19:41:17');
INSERT INTO `sys_dict_item` VALUES ('5bfc739984836091b77b298d9a605181', '5665dd400700ebea77fcc6f8e39fc355', 'sys_req_type', 'DELETE', 'DELETE', 0, 4, NULL, 'red', 0, '删除', 'admin', '2023-01-09 16:13:42', 'admin', '2023-02-21 18:02:06');
INSERT INTO `sys_dict_item` VALUES ('5c24ec491e462910d3123ac7efffdac2', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '登录', '登录', 0, 9, NULL, 'orange', 0, NULL, 'admin', '2023-01-09 11:28:39', 'admin', '2023-01-09 15:23:43');
INSERT INTO `sys_dict_item` VALUES ('60f7b458a56b372bcd4c8d797faf926e', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '登出', '登出', 0, 10, NULL, 'red', 0, NULL, 'admin', '2023-01-09 11:28:50', 'admin', '2023-01-09 15:23:36');
INSERT INTO `sys_dict_item` VALUES ('645349eb7dc061b452b1229c111eef20', '75ce8aab0fca2be5183770260d145c17', 'sso_grant_type', 'authorization_code', 'authorization_code', 0, 1, NULL, 'green', 0, 'authorization_code回调认证方式', 'admin', '2023-05-16 21:50:21', 'admin', '2024-04-15 10:18:40');
INSERT INTO `sys_dict_item` VALUES ('64bcbb1e944d09c0357e74d121e7f456', '55ccc731fe3958c4afca60cbd852a55f', 'tenant_corp_years', '十年以上', '100', 0, 4, NULL, '', 0, '', 'admin', '2023-06-13 17:22:07', 'admin', '2023-06-13 17:32:02');
INSERT INTO `sys_dict_item` VALUES ('655b8b163d3203409a2ef96ee34aa979', '8a992d89a43f27fa2dbdf75871bd7e74', 'tenant_corp_trade', '软件和信息技术服务业', '软件和信息技术服务业', 0, 1, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:05:33', 'admin', '2023-06-15 22:32:40');
INSERT INTO `sys_dict_item` VALUES ('6c4f687dc49bf0f4e881d5768fd9ed4d', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '修改', '修改', 0, 4, NULL, 'purple', 0, NULL, 'admin', '2023-01-09 11:27:58', 'admin', '2023-01-09 16:11:35');
INSERT INTO `sys_dict_item` VALUES ('6f45f21f466658f51bdbb942f3a679b7', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '导出', '导出', 0, 8, NULL, '#87d068', 0, NULL, 'admin', '2023-01-09 11:28:30', 'admin', '2023-01-09 15:26:03');
INSERT INTO `sys_dict_item` VALUES ('6f56bed8bc66030e416df6b4b5805e6e', '5ed7c51e261ce27109e5f4948d40d6e2', 'db_pool_hikari', 'autoCommit', 'true', 2, 3, NULL, '', 0, '自动提交', 'admin', '2023-03-13 11:21:18', 'admin', '2023-03-14 20:26:24');
INSERT INTO `sys_dict_item` VALUES ('6f685f59734340254967d4dbb9bfdfba', '3be670398cf94f18c2631ab4df1e714e', 'tenant_corp_type', '企业', '1', 1, 2, NULL, 'green', 0, NULL, 'admin', '2023-06-13 17:36:56', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('71ffe8bcad36849000f6e5e5124914ef', '0bb92966ec419ed35b9cb46979867e69', 'sys_time_zone', '英国/伦敦', 'Europe/London', 0, 4, NULL, '', 0, '', 'admin', '2023-02-21 18:10:31', 'admin', '2023-02-21 18:16:33');
INSERT INTO `sys_dict_item` VALUES ('731a23dbfd6ec285c9c81ab2a29b43b1', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '授权', '授权', 0, 6, NULL, 'cyan', 0, NULL, 'admin', '2023-01-09 11:28:16', 'admin', '2023-01-09 16:11:27');
INSERT INTO `sys_dict_item` VALUES ('738f704265393a8ac4cb5c536a5bc6e4', '4cc4c6a6c90c5534d11ae9d2db12902f', 'sys_db_pool', '无连接池', 'db_no_pool', 0, 1, NULL, 'red', 0, '不使用连接池', 'admin', '2023-03-13 11:12:14', 'admin', '2023-03-17 10:20:17');
INSERT INTO `sys_dict_item` VALUES ('750f4ff9eb1bdce42b04690d0abd929d', '81e4b91e932f0a671021a7eca2f665b9', 'sys_login_mode', '浏览器', '0', 1, 1, NULL, 'blue', 0, 'web浏览器登录', 'admin', '2023-03-09 14:49:31', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('75d8887dbeb5a0ca4d20ea445139e97a', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '导入', '导入', 0, 7, NULL, '#2db7f5', 0, NULL, 'admin', '2023-01-09 11:28:23', 'admin', '2023-01-09 15:25:53');
INSERT INTO `sys_dict_item` VALUES ('77a3eb2b9e104aded10cd904bd7a6de9', 'c64a91de3cff06e70128938d7cfc2af4', 'mall_delivery_type', '自提', '1', 1, 1, NULL, 'blue', 0, NULL, 'admin', '2024-04-20 18:19:38', 'admin', '2024-04-20 18:19:38');
INSERT INTO `sys_dict_item` VALUES ('83bb1fc1addafe499342df7033c957ce', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'minIdle', '1', 1, 2, NULL, NULL, 0, '最小连接池数', 'admin', '2023-03-13 11:25:31', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('86353dd3e85438e846b704c3af0e7726', '5ed7c51e261ce27109e5f4948d40d6e2', 'db_pool_hikari', 'minimumIdle', '2', 1, 1, NULL, NULL, 0, '连接池中允许的最小连接数最小连接数', 'admin', '2023-03-13 11:19:29', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('873f7ffd87720589519d1aded006d13f', '06907475960c4f544ccbe7f883b9387a', 'tenant_corp_status', '注销', '1', 1, 2, NULL, 'red', 0, NULL, 'admin', '2023-06-13 17:34:43', 'admin', '2023-06-13 17:37:10');
INSERT INTO `sys_dict_item` VALUES ('8afe69f996943d66179ff9063248f4f2', 'c352267b4ec618c1745bdda85f12402e', 'vue_com_type', '文本框', 'Input', 0, 1, NULL, NULL, 0, NULL, 'admin', '2024-04-19 14:12:22', 'admin', '2024-04-19 14:12:22');
INSERT INTO `sys_dict_item` VALUES ('8c75eb1f6f1cddafc1e39f53e0195221', '8a992d89a43f27fa2dbdf75871bd7e74', 'tenant_corp_trade', '医药制造业', '医药制造业', 0, 3, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:07:27', 'admin', '2023-06-15 22:32:40');
INSERT INTO `sys_dict_item` VALUES ('8caa80cc0c116e6407caa1efc5c567d5', 'e8e6e4c3f8e2fb775d6d083883e41839', 'sys_code_condition', '等于', 'eq', 0, 1, NULL, NULL, 0, NULL, 'admin', '2023-05-10 16:12:10', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('8d0e789e6e63e994b31750ad9cb637b4', '193585c2417cfb3e4865501c2e608602', 'sys_db_type', 'oracle', '2', 1, 3, NULL, 'green', 0, 'oracle数据库', 'admin', '2023-01-04 17:32:59', 'admin', '2023-03-24 23:02:09');
INSERT INTO `sys_dict_item` VALUES ('90bb0f58b82df5d13c66169fd748faf8', '2fceca32c5098a60071574c61e0327fa', 'sys_job_status', '调度失败', '2', 1, 3, NULL, 'red', 0, NULL, 'admin', '2023-02-28 16:58:45', 'admin', '2023-03-01 17:59:08');
INSERT INTO `sys_dict_item` VALUES ('93a1cdf50fe9f17e399cf663193a7a1f', '5665dd400700ebea77fcc6f8e39fc355', 'sys_req_type', 'GET', 'GET', 0, 1, NULL, 'green', 0, '查询', 'admin', '2023-01-09 16:10:49', 'admin', '2023-02-21 18:01:56');
INSERT INTO `sys_dict_item` VALUES ('9641bcf7cdd3f2625633d8fb9837f398', '3be670398cf94f18c2631ab4df1e714e', 'tenant_corp_type', '个人', '0', 1, 1, NULL, 'blue', 0, NULL, 'admin', '2023-06-13 17:36:41', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('99c1c3f642ddee2846220373b30d892d', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'validationQuery', 'SELECT 1 FROM DUAL', 0, 15, NULL, '', 0, '配置检测连接是否有效', 'admin', '2023-03-13 11:33:22', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('9b97258d3f744dfa941c1b1137ce7c86', '4cc4c6a6c90c5534d11ae9d2db12902f', 'sys_db_pool', 'Druid', 'db_pool_druid', 0, 3, NULL, 'blue', 0, '阿里德鲁伊连接池', 'admin', '2023-03-13 11:07:07', 'admin', '2023-03-17 10:20:25');
INSERT INTO `sys_dict_item` VALUES ('a1e906c9654de787edafbd05a147eed3', '9bb402e2c751b3bf9358ef9acb3caf49', 'sys_job_type', 'RPC远程任务', '1', 1, 2, NULL, 'blue', 0, 'RPC调度尽量不要调用时间过长的任务，一般用于服务之间简单通知', 'admin', '2023-02-21 10:47:13', 'admin', '2023-02-21 17:44:39');
INSERT INTO `sys_dict_item` VALUES ('a3095db9883574674d2f9fb366f10f52', '5ed7c51e261ce27109e5f4948d40d6e2', 'db_pool_hikari', 'connectionTestQuery', 'SELECT 1', 0, 7, NULL, '', 0, '数据库连接测试语句', 'admin', '2023-03-13 11:23:36', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('a8061138a5828b8fe18d302475928f2b', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'timeBetweenEvictionRunsMillis', '60000', 1, 5, NULL, NULL, 0, '配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒', 'admin', '2023-03-13 11:26:27', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('acbac95a5e0990f96ae6308ff68d693c', '75ce8aab0fca2be5183770260d145c17', 'sso_grant_type', 'refresh_token', 'refresh_token', 0, 3, NULL, 'cyan', 0, 'refresh_token刷新token方式', 'admin', '2023-05-16 21:51:01', 'admin', '2023-05-18 10:20:18');
INSERT INTO `sys_dict_item` VALUES ('ad248d1177e8e8ff9c775a0f2757dfec', '24f5894e66ca34b9d4aafa2604c94091', 'mall_pay_type', '微信', '1', 1, 2, NULL, '#52C41A', 0, '', 'admin', '2024-04-20 18:18:13', 'admin', '2024-04-20 18:18:24');
INSERT INTO `sys_dict_item` VALUES ('afe58b597c6c86edb328ab47e334d329', '24f5894e66ca34b9d4aafa2604c94091', 'mall_pay_type', '支付宝', '0', 1, 1, NULL, '#1677FF', 0, NULL, 'admin', '2024-04-20 18:17:50', 'admin', '2024-04-20 18:18:01');
INSERT INTO `sys_dict_item` VALUES ('b875ec52ef28c8e1c787e0b71a4c3b0f', 'd2d1bfb80a89199abdedae4874311b69', 'mall_order_status', '已完成', '3', 1, 4, NULL, '#52C41A', 0, '', 'admin', '2024-04-20 18:13:09', 'admin', '2024-04-20 18:16:18');
INSERT INTO `sys_dict_item` VALUES ('be3c4fd10bb7a6691ca9cc2dc55c038d', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'connectionProperties', 'druid.stat.mergeSql=false;druid.stat.slowSqlMillis=5000', 0, 14, NULL, '', 0, '打开mergeSql功能；慢SQL记录', 'admin', '2023-03-13 11:32:33', 'admin', '2023-03-13 11:32:58');
INSERT INTO `sys_dict_item` VALUES ('bf8ded2aadd49b2822ce6799070f8164', '81e4b91e932f0a671021a7eca2f665b9', 'sys_login_mode', '微信', '1', 1, 2, NULL, 'green', 0, '微信小程序登录', 'admin', '2023-03-09 14:49:57', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('bfc4e183ac3bd15a3cbc9b5cd5116763', '2627066fc33d283c7a1ca4123a31be2d', 'sys_req_source', '手机用户', '2', 1, 2, NULL, 'pink', 0, '手机用户', 'admin', '2023-02-02 11:41:49', 'admin', '2023-02-21 18:00:26');
INSERT INTO `sys_dict_item` VALUES ('c1135debd21467001fd69c21df6c6c10', '5ed7c51e261ce27109e5f4948d40d6e2', 'db_pool_hikari', 'connectionTimeout', '30000', 0, 6, NULL, '', 0, '待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException', 'admin', '2023-03-13 11:23:15', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('c1770d660a44ff2275615a8a329ee6db', 'd2d1bfb80a89199abdedae4874311b69', 'mall_order_status', '已支付', '1', 1, 2, NULL, '#ECC12B', 0, NULL, 'admin', '2024-04-20 18:12:07', 'admin', '2024-04-20 18:16:50');
INSERT INTO `sys_dict_item` VALUES ('c9ff23502c59bff661479755f842105b', '5ed7c51e261ce27109e5f4948d40d6e2', 'db_pool_hikari', 'maximumPoolSize', '10', 1, 2, NULL, NULL, 0, '最大连接数，包括闲置和使用中的连接， 如果maxPoolSize小于1，则会被重置，当minIdle0则重置为minIdle的值', 'admin', '2023-03-13 11:20:24', 'admin', '2023-03-17 16:44:36');
INSERT INTO `sys_dict_item` VALUES ('ce8059c1fb3b533339f6bdccc17e0046', 'ad7336dda270e6430565b313a741ffb7', 'sys_user_sex', '男', '1', 1, 1, NULL, 'green', 0, '男性', 'admin', '2023-01-04 17:31:35', 'admin', '2023-01-09 16:09:49');
INSERT INTO `sys_dict_item` VALUES ('cf2899ae2d41fd29b5cef8de005098e7', '5665dd400700ebea77fcc6f8e39fc355', 'sys_req_type', 'PUT', 'PUT', 0, 3, NULL, 'purple', 0, '修改', 'admin', '2023-01-09 16:12:11', 'admin', '2023-02-21 18:02:03');
INSERT INTO `sys_dict_item` VALUES ('d48596048bd2bd62c5844e4bc5d70208', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'maxWait', '60000', 1, 4, NULL, NULL, 0, '获取连接等待超时的时间', 'admin', '2023-03-13 11:26:06', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('d53cec8854c1a92c33059caffcb6af00', '5ed7c51e261ce27109e5f4948d40d6e2', 'db_pool_hikari', 'maxLifetime', '300000', 1, 5, NULL, '', 0, '一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired）； 缺省:30分钟，建议设置比数据库超时时长少30秒', 'admin', '2023-03-13 11:22:15', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('d563a2b3f99baff573193c81a96bacf6', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'filters', 'stat,wall', 0, 13, NULL, '', 0, '配置监控统计拦截的filters，去掉后监控界面sql无法统计，\'wall\'用于防火墙', 'admin', '2023-03-13 11:31:47', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('d781dcc93f41b65dd5f44bb48a69a57c', '55ccc731fe3958c4afca60cbd852a55f', 'tenant_corp_years', '五年以内', '5', 0, 2, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:21:00', 'admin', '2023-06-13 17:31:57');
INSERT INTO `sys_dict_item` VALUES ('d82a25d50542f74cd2fef5cc7f04e9aa', '6ddca50d9ddad44806ef18c3bf4721c9', 'nc_formula_type', '高级功能', 'advance_op', 0, 4, NULL, 'red', 0, NULL, 'admin', '2023-12-24 16:40:20', 'admin', '2023-12-24 16:40:20');
INSERT INTO `sys_dict_item` VALUES ('df72218dba18e67c91dc2e12493b15ed', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'poolPreparedStatements', 'true', 2, 11, NULL, '', 0, '是否缓存游标', 'admin', '2023-03-13 11:30:08', 'admin', '2023-03-14 20:26:08');
INSERT INTO `sys_dict_item` VALUES ('e18da53336b068c23d1b144e38d122b2', 'd2d1bfb80a89199abdedae4874311b69', 'mall_order_status', '待支付', '0', 1, 1, NULL, '#8F8F8F', 0, NULL, 'admin', '2024-04-20 18:11:43', 'admin', '2024-04-20 18:15:52');
INSERT INTO `sys_dict_item` VALUES ('e1ce7b2d2de8f28789a30cce64ebd5d3', 'd42259143fdd344b439fe39d3fffdefe', 'tenant_corp_size', '100到300人', 's', 0, 2, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:11:27', 'admin', '2023-06-13 17:29:14');
INSERT INTO `sys_dict_item` VALUES ('e27a2f5ecad5a50a7d788756da095245', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'minEvictableIdleTimeMillis', '300000', 1, 6, NULL, NULL, 0, '配置一个连接在池中最小生存的时间，单位是毫秒', 'admin', '2023-03-13 11:26:48', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('e27c1ecf50054eb7e0263d3b9580f469', '6ddca50d9ddad44806ef18c3bf4721c9', 'nc_formula_type', '日期处理', 'date_op', 0, 3, NULL, 'cyan', 0, '', 'admin', '2023-10-27 09:19:16', 'admin', '2023-10-27 14:22:00');
INSERT INTO `sys_dict_item` VALUES ('e3431f70eafa566d0aa0ced7a9b11552', 'd42259143fdd344b439fe39d3fffdefe', 'tenant_corp_size', '300到500人', 'm', 0, 3, NULL, '', 0, '', 'admin', '2023-06-13 17:11:48', 'admin', '2023-06-13 17:29:14');
INSERT INTO `sys_dict_item` VALUES ('e534f060f0969be49a93254796a2a24b', 'd42259143fdd344b439fe39d3fffdefe', 'tenant_corp_size', '100人以下', 'xs', 0, 1, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:10:45', 'admin', '2023-06-13 17:29:14');
INSERT INTO `sys_dict_item` VALUES ('eadbf511b6dfe30b72c4fcad6f10680c', '2fceca32c5098a60071574c61e0327fa', 'sys_job_status', '执行失败', '4', 0, 5, NULL, 'red', 0, '异步执行失败', 'admin', '2023-03-01 18:00:03', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('ec9cfc538a91de2f61829e61064d636b', 'ad7336dda270e6430565b313a741ffb7', 'sys_user_sex', '女', '0', 1, 2, NULL, 'red', 0, '女性', 'admin', '2023-01-04 17:31:53', 'admin', '2023-01-09 16:09:52');
INSERT INTO `sys_dict_item` VALUES ('f12c3e6c95f16dfd35e80c8c7101504e', '5ed7c51e261ce27109e5f4948d40d6e2', 'db_pool_hikari', 'idleTimeout', '60000', 1, 4, NULL, '', 0, '一个连接idle状态的最大时长（毫秒），超时则被释放（retired）', 'admin', '2023-03-13 11:21:46', 'admin', '2023-03-13 11:22:47');
INSERT INTO `sys_dict_item` VALUES ('f3b5faffba629cc8e07e5d57d76e4e49', '55ccc731fe3958c4afca60cbd852a55f', 'tenant_corp_years', '十年以内', '10', 0, 3, NULL, '', 0, '', 'admin', '2023-06-13 17:21:32', 'admin', '2023-06-13 17:32:00');
INSERT INTO `sys_dict_item` VALUES ('f442a8f38c5ce32b6b05c640cc3ecc2b', 'e8e6e4c3f8e2fb775d6d083883e41839', 'sys_code_condition', '包含', 'like', 0, 2, NULL, NULL, 0, NULL, 'admin', '2023-05-10 16:12:34', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('f485da6d28850874150e6bda1d565907', 'd2d1bfb80a89199abdedae4874311b69', 'mall_order_status', '已发货', '2', 0, 3, NULL, '#1677FF', 0, NULL, 'admin', '2024-04-20 18:12:35', 'admin', '2024-04-20 18:16:36');
INSERT INTO `sys_dict_item` VALUES ('f84c6e754d183f40a4b1d7a5b72371c7', '75ce8aab0fca2be5183770260d145c17', 'sso_grant_type', 'password', 'password', 0, 2, NULL, 'blue', 0, '帐号密码认证方式', 'admin', '2023-05-16 21:50:40', 'admin', '2023-05-18 10:20:12');
INSERT INTO `sys_dict_item` VALUES ('f88a8a4a391e3ae455ba6d8157f22260', '5665dd400700ebea77fcc6f8e39fc355', 'sys_req_type', 'POST', 'POST', 0, 2, NULL, 'blue', 0, '新增', 'admin', '2023-01-09 16:12:41', 'admin', '2023-02-21 18:02:00');
INSERT INTO `sys_dict_item` VALUES ('fae13a8680a44565b4ffab652c7c9d6b', '6ddca50d9ddad44806ef18c3bf4721c9', 'nc_formula_type', '字符处理', 'char_op', 0, 1, NULL, 'blue', 0, NULL, 'admin', '2023-10-27 09:12:06', 'admin', '2023-10-27 14:21:43');
INSERT INTO `sys_dict_item` VALUES ('606ed66c01af90f6474ccc761f4493e4', '980f701247526a1468f88122c687bee2', 'api_data_source', 'API', '2', 1, 2, 'ant-design:api-outlined', 'cyan', 0, NULL, 'admin', '2024-09-25 09:49:51', 'admin', '2024-11-15 17:15:25');
INSERT INTO `sys_dict_item` VALUES ('b6764e7cc7fe5e95bc4273dd0209403d', '980f701247526a1468f88122c687bee2', 'api_data_source', '文件', '1', 1, 1, 'ant-design:file-outlined', 'green', 0, NULL, 'admin', '2024-09-25 09:49:19', 'admin', '2024-11-15 17:14:47');
INSERT INTO `sys_dict_item` VALUES ('36280b43c3262c240a058256d8656af3', '980f701247526a1468f88122c687bee2', 'api_data_source', '数据库', '0', 1, 0, 'ant-design:database-outlined', 'blue', 0, NULL, 'admin', '2024-09-25 09:48:41', 'admin', '2024-11-15 17:14:56');
INSERT INTO `sys_dict_item` VALUES ('beec834fab73cd52190de4e07d1d9e84', '980f701247526a1468f88122c687bee2', 'api_data_source', '文件夹', '-1', 1, -1, 'ant-design:folder-outlined', 'primary', 0, NULL, 'admin', '2024-09-25 09:48:20', 'admin', '2024-11-15 17:15:10');
INSERT INTO `sys_dict_item` VALUES ('5f04a11a89bdee99fddb75c9275a4eff', 'b0f0a03f9e879c2ce3b7722a3cd01061', 'http_content_type', 'multipart/form-data', 'multipart/form-data; charset=utf-8', 0, 3, NULL, 'cyan', 0, NULL, 'admin', '2025-07-22 14:41:59', 'admin', '2025-07-22 14:41:59');
INSERT INTO `sys_dict_item` VALUES ('736b7180c22a642965310d19f9ea4de7', 'b0f0a03f9e879c2ce3b7722a3cd01061', 'http_content_type', 'application/x-www-form-urlencoded', 'application/x-www-form-urlencoded; charset=utf-8', 0, 1, NULL, 'blue', 0, NULL, 'admin', '2025-07-22 14:40:09', 'admin', '2025-07-23 14:24:46');
INSERT INTO `sys_dict_item` VALUES ('7d9f53a35c7014b646807cbbfb675489', 'b0f0a03f9e879c2ce3b7722a3cd01061', 'http_content_type', 'application/json', 'application/json; charset=utf-8', 0, 2, NULL, 'green', 0, NULL, 'admin', '2025-07-22 14:39:35', 'admin', '2025-07-23 14:24:52');
INSERT INTO `sys_dict_item` VALUES ('fe02e4db9660a1e05ea079634b352794', '4a9914bd42939163ceb9c5b4002cfa66', 'http_request_method', 'DELETE', 'DELETE', 0, 4, NULL, 'red', 0, NULL, 'admin', '2025-07-22 14:32:31', 'admin', '2025-07-25 10:59:03');
INSERT INTO `sys_dict_item` VALUES ('b9377a543e2156dd9b711782e9f93b71', '4a9914bd42939163ceb9c5b4002cfa66', 'http_request_method', 'PUT', 'PUT', 0, 3, NULL, 'orange', 0, NULL, 'admin', '2025-07-22 14:32:05', 'admin', '2025-07-25 10:58:59');
INSERT INTO `sys_dict_item` VALUES ('d62ef2a8527779999b0f3e32a4d43aab', '4a9914bd42939163ceb9c5b4002cfa66', 'http_request_method', 'POST', 'POST', 0, 2, NULL, 'green', 0, NULL, 'admin', '2025-07-22 14:31:51', 'admin', '2025-07-22 14:31:51');
INSERT INTO `sys_dict_item` VALUES ('844db10c10e41a1f7621bb5d5ef6b1b0', '4a9914bd42939163ceb9c5b4002cfa66', 'http_request_method', 'GET', 'GET', 0, 1, NULL, 'blue', 0, NULL, 'admin', '2025-07-22 14:31:37', 'admin', '2025-07-22 14:31:37');
INSERT INTO `sys_dict_item` VALUES ('2cdf5389b1d6a35e52c14486e04a3a57', '220bbd3b1dd32fd37d0abbd279a14774', 'workflow_task_status', '待审批', 'created', 0, 1, NULL, 'blue', 0, NULL, 'admin', '2025-10-10 16:27:42', 'admin', '2025-10-10 16:27:42');
INSERT INTO `sys_dict_item` VALUES ('261f489024bb90163e49ee85498df47e', '220bbd3b1dd32fd37d0abbd279a14774', 'workflow_task_status', '已审批', 'completed', 0, 2, NULL, 'green', 0, NULL, 'admin', '2025-10-10 16:28:02', 'admin', '2025-10-10 16:28:08');
INSERT INTO `sys_dict_item` VALUES ('07582f8cb8443cd3de3f5ab150386690', '220bbd3b1dd32fd37d0abbd279a14774', 'workflow_task_status', '已取消', 'terminated', 0, 3, '', 'red', 0, NULL, 'admin', '2025-10-10 16:29:10', 'admin', '2025-10-10 16:29:14');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '中文标题',
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '方法',
  `req_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求类型',
  `req_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求路径',
  `req_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求参数',
  `req_source` int(1) NULL DEFAULT 0 COMMENT '请求来源（0其它 1后台用户 2手机端用户）',
  `oper_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '操作类型（0其它 1查询 2新增 3修改 4删除 5授权 6导入 7导出...）',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作IP',
  `oper_status` int(1) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '返回信息',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `create_time_index`(`create_time`) USING BTREE COMMENT '操作时间索引'
) ENGINE = InnoDB AUTO_INCREMENT = 38051 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_storage
-- ----------------------------
DROP TABLE IF EXISTS `sys_storage`;
CREATE TABLE `sys_storage`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `file_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件的唯一索引',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名',
  `file_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件类型',
  `file_size` int(11) NULL DEFAULT NULL COMMENT '文件大小',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件访问链接',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '存储路径',
  `is_private` tinyint(1) NULL DEFAULT 0 COMMENT '是否私密文件 0为公开的  1为私密文件',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标记(0未删除1删除)',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `key`(`file_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件存储表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_dict_category
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_category`;
CREATE TABLE `sys_dict_category`  (
                                      `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
                                      `parent_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父分类id',
                                      `category_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类编码',
                                      `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
                                      `tree_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类树编码（系统自动编码）',
                                      `tree_level` tinyint NOT NULL COMMENT '分类树层级（自动生成）',
                                      `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
                                      `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                      `sort` int NULL DEFAULT 0 COMMENT '排序',
                                      `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建用户',
                                      `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                      `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新用户',
                                      `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `tree_code_index`(`tree_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '树形分类字典' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_category
-- ----------------------------
INSERT INTO `sys_dict_category` VALUES ('00491f886354990b6aef2e7a41f397ba', 'fe72c08cf082f8f1c6efef9d28119c45', '', '图片', '000040000400004', 3, 'ant-design:picture-outlined', NULL, 4, 'admin', '2025-01-23 20:51:41', 'admin', '2025-01-25 11:38:42');
INSERT INTO `sys_dict_category` VALUES ('063eafb8b1b481c1f2f99799ebc7ada7', '26c427a7125dd186ddc238647a7f4ba1', '', '苹果笔记本', '0000100004', 2, 'ant-design:apple-outlined', NULL, 3, 'admin', '2024-11-18 16:29:44', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('065b8cc29c9a64cb9ef4bd57880c2469', '9f2aec31293bbb9294db416e42f168b5', NULL, '其他', '000050000300005', 3, NULL, NULL, 5, 'admin', '2025-03-19 15:41:29', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('0e55dc203886fbce5cdb0dd3ba2023b3', '26c427a7125dd186ddc238647a7f4ba1', '', '联想笔记本', '0000100002', 2, NULL, NULL, 4, 'admin', '2024-11-18 16:29:44', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('0f4fea537f02a09bd8ef5f0ca8be49ad', '', 'screen_resource', '资源中心', '00005', 1, '', NULL, 3, 'admin', '2025-03-19 15:33:37', 'admin', '2025-03-19 21:51:21');
INSERT INTO `sys_dict_category` VALUES ('117acb01c6b53d0c39b94c00eb1d8e6d', '540d7a7ef84835275e227c6f0ec44dcf', NULL, '组件样式', '000050000200002', 3, '', NULL, 1, 'admin', '2025-03-19 15:40:18', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('1a6da2aa81926ce2621ef58de2bae5ae', '540d7a7ef84835275e227c6f0ec44dcf', NULL, '其他', '000050000200003', 3, '', NULL, 3, 'admin', '2025-03-19 15:40:35', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('2207ba37d3071c1e87a344c3f61a5155', '926abe03a1935946b43fdcc031c9d3c8', '', '小米', '0000200006', 2, NULL, NULL, 1, 'admin', '2024-03-14 14:38:19', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('26c427a7125dd186ddc238647a7f4ba1', '', 'notebook', '笔记本', '00001', 1, NULL, NULL, 0, 'admin', '2024-11-18 16:29:44', 'admin', '2025-04-27 22:48:48');
INSERT INTO `sys_dict_category` VALUES ('27c665c8c4c8a1ca378b8c2342e75cf6', 'b8726bb3b3323e3ed4039322d34dae7c', NULL, '地图', '000040000100007', 3, 'carbon:map', NULL, 5, 'admin', '2025-01-31 21:38:34', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('2c82edeefa228fbae69fa93e16f58912', 'aaf0612479eaffe56b8c3b2af7d3cb8f', NULL, '其他', '000040000500005', 3, 'ant-design:code-sandbox-outlined', NULL, 10, 'admin', '2025-07-10 23:24:36', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('3055c9e5ce00720bbba2e7a423eeeaa3', 'b8726bb3b3323e3ed4039322d34dae7c', NULL, '其他', '000040000100005', 3, 'ant-design:dot-chart-outlined', NULL, 6, 'admin', '2025-01-10 14:51:24', 'admin', '2025-01-31 21:38:41');
INSERT INTO `sys_dict_category` VALUES ('3c94138cae5b3a485858e589618e58d0', 'aaf0612479eaffe56b8c3b2af7d3cb8f', NULL, '按钮', '000040000500003', 3, 'carbon:button-centered', NULL, 2, 'admin', '2025-07-02 17:41:28', 'admin', '2025-07-02 18:07:10');
INSERT INTO `sys_dict_category` VALUES ('43a3d5cc5f41ec7de8b494f6c98adf16', 'a1c2ae510d0676d51b4313591d0d3c97', NULL, '数据运营', '000050000100004', 3, NULL, NULL, 4, 'admin', '2025-03-19 15:37:06', 'admin', '2025-03-19 15:37:23');
INSERT INTO `sys_dict_category` VALUES ('47eba0ed867cba5909503642bf10f2b4', '26c427a7125dd186ddc238647a7f4ba1', '', '华为笔记本', '0000100001', 2, NULL, NULL, 1, 'admin', '2024-11-18 16:29:44', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('4a012bfdcac45a75617c03b9fd0d6b2c', 'fe72c08cf082f8f1c6efef9d28119c45', NULL, '标头', '000040000400005', 3, 'carbon:open-panel-top', NULL, 3, 'admin', '2025-01-25 11:38:37', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('4d544061656fca47a6dca3896a9f94fb', 'da6694b2d969e270122ad2ee1847ba5d', NULL, '文本', '0000400002', 2, 'ant-design:file-text-outlined', NULL, 1, 'admin', '2024-11-18 17:14:45', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('540d7a7ef84835275e227c6f0ec44dcf', '0f4fea537f02a09bd8ef5f0ca8be49ad', 'mf_screen_demo', '大屏样例', '0000500002', 2, 'ant-design:crown-outlined', NULL, 2, 'admin', '2025-03-19 15:34:32', 'admin', '2025-03-26 18:04:55');
INSERT INTO `sys_dict_category` VALUES ('57edf426cf8dcab91f92667b0fd8376b', '4d544061656fca47a6dca3896a9f94fb', NULL, '所有', '000040000200001', 3, 'ant-design:ellipsis-outlined', NULL, 0, 'admin', '2024-11-18 17:18:33', 'admin', '2024-11-27 21:21:28');
INSERT INTO `sys_dict_category` VALUES ('58e6efc0b6a6441cf0a8081520085ccd', 'a1c2ae510d0676d51b4313591d0d3c97', NULL, '官方', '000050000100002', 3, NULL, NULL, 2, 'admin', '2025-03-19 15:36:21', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('6577e71c515e8014297ac7a9b2fb17af', '926abe03a1935946b43fdcc031c9d3c8', 'iPhone', '苹果', '0000200007', 2, NULL, NULL, 2, 'admin', '2024-03-14 14:38:23', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('688f4f25303f6ce601c99cc30db7dd5b', 'aaf0612479eaffe56b8c3b2af7d3cb8f', NULL, '选项', '000040000500002', 3, 'carbon:list-dropdown', NULL, 1, 'admin', '2025-03-17 15:23:28', 'admin', '2025-07-02 17:43:53');
INSERT INTO `sys_dict_category` VALUES ('69d2a8f8396fb6866be7e5e97de0e908', '9f2aec31293bbb9294db416e42f168b5', NULL, '行业', '000050000300002', 3, NULL, NULL, 2, 'admin', '2025-03-19 15:40:59', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('6fab782d03d564025274e46a8ec63668', '4d544061656fca47a6dca3896a9f94fb', NULL, '标签', '000040000200002', 3, 'ant-design:tag-outlined', '', 1, 'admin', '2024-11-18 17:38:23', 'admin', '2024-11-18 17:38:31');
INSERT INTO `sys_dict_category` VALUES ('862f955f4ba4cb11266a22d8ef023913', 'fe72c08cf082f8f1c6efef9d28119c45', NULL, '装饰', '000040000400003', 3, 'ant-design:skin-outlined', NULL, 2, 'admin', '2024-12-27 22:24:24', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('8fcbb99d3e9550f5e9d30dd592a67352', 'b8726bb3b3323e3ed4039322d34dae7c', NULL, '占比图', '000040000100006', 3, 'carbon:chart-ring', NULL, 4, 'admin', '2025-01-14 17:31:09', 'admin', '2025-01-14 17:31:20');
INSERT INTO `sys_dict_category` VALUES ('90329d2cd947b18ef46a02509873f81c', '926abe03a1935946b43fdcc031c9d3c8', '', 'vivio', '0000200008', 2, NULL, NULL, 4, 'admin', '2024-03-14 14:38:36', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('91d27de57bc24cec5369fb3ef0dd689f', '9f2aec31293bbb9294db416e42f168b5', NULL, '动画', '000050000300004', 3, NULL, NULL, 4, 'admin', '2025-03-19 15:41:23', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('926abe03a1935946b43fdcc031c9d3c8', '', 'phone', '手机', '00002', 1, NULL, NULL, 1, 'admin', '2024-03-14 14:36:08', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('964b67d7277654e21a7e3b03e1363300', '926abe03a1935946b43fdcc031c9d3c8', '', '华为', '0000200005', 2, NULL, NULL, 0, 'admin', '2024-03-14 14:38:15', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('9790b4ccb2e28f0d54c3aaa528ca3fea', 'b8726bb3b3323e3ed4039322d34dae7c', NULL, '饼状图', '000040000100004', 3, 'ant-design:pie-chart-outlined', NULL, 3, 'admin', '2024-11-19 16:57:06', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('9978cc20d5be070bcc8b6fdd6d78de5f', 'a1c2ae510d0676d51b4313591d0d3c97', NULL, '行业', '000050000100003', 3, NULL, NULL, 3, 'admin', '2025-03-19 15:36:38', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('9f2aec31293bbb9294db416e42f168b5', '0f4fea537f02a09bd8ef5f0ca8be49ad', 'mf_screen_pic', '图片资源', '0000500003', 2, 'ant-design:picture-outlined', NULL, 3, 'admin', '2025-03-19 15:34:51', 'admin', '2025-03-26 18:05:25');
INSERT INTO `sys_dict_category` VALUES ('a1c2ae510d0676d51b4313591d0d3c97', '0f4fea537f02a09bd8ef5f0ca8be49ad', 'mf_screen_template', '大屏模板', '0000500001', 2, 'ant-design:fund-projection-screen-outlined', NULL, 1, 'admin', '2025-03-19 15:34:14', 'admin', '2025-03-26 18:03:35');
INSERT INTO `sys_dict_category` VALUES ('a2579f2c7edd39f2b7e6758886b97c92', 'a1c2ae510d0676d51b4313591d0d3c97', NULL, '推荐', '000050000100001', 3, '', NULL, 1, 'admin', '2025-03-19 15:36:02', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('aa4c61f29c59e7c982ec2255a7ab6316', '4d544061656fca47a6dca3896a9f94fb', NULL, '日期', '000040000200003', 3, 'ant-design:calendar-outlined', NULL, 2, 'admin', '2024-11-18 17:40:55', 'admin', '2024-11-18 17:41:01');
INSERT INTO `sys_dict_category` VALUES ('aaf0612479eaffe56b8c3b2af7d3cb8f', 'da6694b2d969e270122ad2ee1847ba5d', NULL, '交互', '0000400005', 2, 'ant-design:star-outlined', NULL, 5, 'admin', '2025-03-17 12:52:19', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('af9d4abecbd728a49170f8f8ac46dfff', '26c427a7125dd186ddc238647a7f4ba1', 'mi', '小米笔记本', '0000100003', 2, NULL, NULL, 2, 'admin', '2024-11-18 16:29:44', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('afe955d350d5ac5ef541f145ffa7706f', 'b8726bb3b3323e3ed4039322d34dae7c', NULL, '折线图', '000040000100003', 3, 'ant-design:line-chart-outlined', NULL, 2, 'admin', '2024-11-19 16:56:46', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('b0def7750854c7fbf3469c9e4a1be303', '9f2aec31293bbb9294db416e42f168b5', NULL, '推荐', '000050000300001', 3, NULL, NULL, 1, 'admin', '2025-03-19 15:40:52', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('b5af416292a8659fcd2bc8d58164bf32', 'eeaab687ec80b493ab4725977ea975b9', NULL, '表格', '000040000300002', 3, 'ant-design:table-outlined', NULL, 1, 'admin', '2025-01-21 14:54:57', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('b8726bb3b3323e3ed4039322d34dae7c', 'da6694b2d969e270122ad2ee1847ba5d', '', '图表', '0000400001', 2, 'ant-design:bar-chart-outlined', NULL, 0, 'admin', '2024-11-18 16:46:37', 'admin', '2024-11-18 17:14:52');
INSERT INTO `sys_dict_category` VALUES ('c3d80728225d9b7662ac1d18383d20a1', 'a1c2ae510d0676d51b4313591d0d3c97', NULL, '其他', '000050000100005', 3, '', NULL, 5, 'admin', '2025-03-19 15:37:43', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('c6b295bc5369e7dd9a620c1cabcabd8d', 'fe72c08cf082f8f1c6efef9d28119c45', NULL, '所有', '000040000400001', 3, 'ant-design:ellipsis-outlined', NULL, 0, 'admin', '2024-11-18 17:44:51', 'admin', '2024-11-27 21:21:41');
INSERT INTO `sys_dict_category` VALUES ('cf4849acd006faac44fe2c40fae89939', 'aaf0612479eaffe56b8c3b2af7d3cb8f', NULL, '容器', '000040000500004', 3, 'ant-design:layout-outlined', NULL, 3, 'admin', '2025-07-10 23:21:35', 'admin', '2025-07-10 23:22:10');
INSERT INTO `sys_dict_category` VALUES ('d90b48828eaeda14e1ec6daca7fb20e7', '926abe03a1935946b43fdcc031c9d3c8', '', 'oppo', '0000200009', 2, NULL, NULL, 5, 'admin', '2024-03-14 14:38:48', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('d994104c1bfd6b4f7fa68d1cf8e72672', '540d7a7ef84835275e227c6f0ec44dcf', NULL, '联动交互', '000050000200001', 3, NULL, NULL, 2, 'admin', '2025-03-19 15:38:46', 'admin', '2025-03-19 15:40:24');
INSERT INTO `sys_dict_category` VALUES ('da6694b2d969e270122ad2ee1847ba5d', '', 'screen_charts_type', '大屏组件', '00004', 1, 'ant-design:fund-projection-screen-outlined', NULL, 2, 'admin', '2024-11-18 16:15:02', 'admin', '2024-11-18 23:19:49');
INSERT INTO `sys_dict_category` VALUES ('db50e28e60b924b850f637eb0a286a69', '9f2aec31293bbb9294db416e42f168b5', NULL, '小图标', '000050000300003', 3, NULL, NULL, 3, 'admin', '2025-03-19 15:41:09', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('ec135d3194956f5f98141c997c2e0ab1', 'b8726bb3b3323e3ed4039322d34dae7c', '', '所有', '000040000100001', 3, 'ant-design:ellipsis-outlined', NULL, 0, 'admin', '2024-11-18 17:43:50', 'admin', '2024-11-27 21:21:18');
INSERT INTO `sys_dict_category` VALUES ('eeaab687ec80b493ab4725977ea975b9', 'da6694b2d969e270122ad2ee1847ba5d', NULL, '列表', '0000400003', 2, 'ant-design:unordered-list-outlined', NULL, 2, 'admin', '2024-11-18 17:15:20', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('f80fb0216a6e083a896655efb37af1da', 'eeaab687ec80b493ab4725977ea975b9', NULL, '所有', '000040000300001', 3, 'ant-design:ellipsis-outlined', NULL, 0, 'admin', '2024-11-18 17:44:31', 'admin', '2024-11-27 21:21:35');
INSERT INTO `sys_dict_category` VALUES ('fafa6224eb9d3ebe49fe21a20171b05d', 'b8726bb3b3323e3ed4039322d34dae7c', NULL, '柱状图', '000040000100002', 3, 'ant-design:bar-chart-outlined', NULL, 1, 'admin', '2024-11-19 16:54:26', 'admin', '2024-11-28 11:23:28');
INSERT INTO `sys_dict_category` VALUES ('fb8d6852d275309854125a152f56e40b', 'fe72c08cf082f8f1c6efef9d28119c45', NULL, '边框', '000040000400002', 3, 'ant-design:border-outlined', NULL, 1, 'admin', '2024-12-27 14:26:03', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('fc74625ae4c3a53cbea0eb7bdd994a9a', '4d544061656fca47a6dca3896a9f94fb', NULL, '输入框', '000040000200004', 3, 'ant-design:edit-outlined', NULL, 3, 'admin', '2025-07-08 16:03:04', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('fe72c08cf082f8f1c6efef9d28119c45', 'da6694b2d969e270122ad2ee1847ba5d', NULL, '装饰', '0000400004', 2, 'carbon:color-palette', NULL, 3, 'admin', '2024-11-18 17:15:41', 'admin', '2024-11-18 17:15:52');
INSERT INTO `sys_dict_category` VALUES ('ff8792113848c4a2b03ea98d78381d7a', 'aaf0612479eaffe56b8c3b2af7d3cb8f', NULL, '所有', '000040000500001', 3, 'ant-design:ellipsis-outlined', NULL, 0, 'admin', '2025-03-17 14:42:03', 'admin', '2025-03-17 14:42:19');

-- ----------------------------
-- Table structure for demo_data_scope
-- ----------------------------
DROP TABLE IF EXISTS `demo_data_scope`;
CREATE TABLE `demo_data_scope`  (
                                    `id` int NOT NULL AUTO_INCREMENT,
                                    `role_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色id',
                                    `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户id',
                                    `org_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组织id',
                                    `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
                                    `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
                                    `create_time` datetime NULL DEFAULT NULL COMMENT '下单时间',
                                    `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
                                    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of demo_data_scope
-- ----------------------------
INSERT INTO `demo_data_scope` VALUES (1, NULL, NULL, '1', '系统默认', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (2, NULL, NULL, '404542be62d21014451808aa67f1f5df', '南京XXX公司', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (3, NULL, NULL, '45ee8344ffb942d6f2ee1f868b584a99', 'XXX运维部', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (4, NULL, NULL, '624edadc0562c7fe9b54f0e3ba5e658b', '摸鱼开发部', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (5, NULL, NULL, '76f3d1d15272858c2523f287aea955f9', '摸鱼测试部', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (6, NULL, NULL, '8b7bbf1cac1af7faca6dd2686981fb22', '摸鱼事业部', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (7, NULL, NULL, 'a2569f85d5cd316f5c7011e43dc86271', 'XXX开发部', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (8, NULL, NULL, 'af30f104a622b5de76add210dd33b361', '摸鱼运维部', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (9, NULL, '1', NULL, '系统默认', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (11, NULL, 'a480b6861ca4a44631af794a99e77265', NULL, '南京XXX公司', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (12, '1', NULL, NULL, '超级管理员', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (13, '210297727b74ecb505c1b4d97f76daee', NULL, NULL, '测试', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (14, '351591b8df6f3eed5d1c613aac6c5bc8', NULL, NULL, 'XXX管理员', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (15, '4063404b06e967bcf619bf86e7fe6359', NULL, NULL, 'XXX运维', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (16, '4b423f7b1ac0ed0b46a8e5ec3389ac14', NULL, NULL, '管理', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (17, '67e95f5e81b8da9a8f70db7540b7409d', NULL, NULL, '运维', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (18, '86ec59a2a3261fd6ba4098da034965ad', NULL, NULL, 'XXX开发', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (19, 'a787082d9b7c177439a114995e4caff1', NULL, NULL, '开发', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (20, 'e77a579aaf0844ba493cde1811b137a9', NULL, NULL, '个人', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (21, '4b423f7b1ac0ed0b46a8e5ec3389ac14', '1', NULL, '租户下的管理', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (22, '4b423f7b1ac0ed0b46a8e5ec3389ac14', 'a480b6861ca4a44631af794a99e77265', NULL, '租户下的管理', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (23, '1', '1', NULL, '租户下超级管理员', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (24, '1', 'a480b6861ca4a44631af794a99e77265', NULL, '租户下超级管理员', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (25, '351591b8df6f3eed5d1c613aac6c5bc8', 'a480b6861ca4a44631af794a99e77265', NULL, '租户下XXX管理员', '', NULL, '', NULL);

-- ----------------------------
-- Table structure for demo_import_export
-- ----------------------------
DROP TABLE IF EXISTS `demo_import_export`;
CREATE TABLE `demo_import_export`  (
                                       `id` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '订单ID',
                                       `user_name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
                                       `user_phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '联系电话',
                                       `user_address` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '收货地址',
                                       `order_status` tinyint(1) NULL DEFAULT NULL COMMENT '订单状态',
                                       `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '订单总价',
                                       `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
                                       `express_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '快递费用',
                                       `order_desc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '订单描述',
                                       `trade_no` varchar(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '支付流水',
                                       `pay_type` tinyint(1) NULL DEFAULT NULL COMMENT '支付类型',
                                       `pay_cert` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '支付凭证',
                                       `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
                                       `delivery_type` tinyint(1) NULL DEFAULT NULL COMMENT '配送方式',
                                       `confirm_time` datetime NULL DEFAULT NULL COMMENT '收货时间',
                                       `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
                                       `create_time` datetime NULL DEFAULT NULL COMMENT '下单时间',
                                       `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
                                       `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '导入导出Demo' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of demo_import_export
-- ----------------------------
INSERT INTO `demo_import_export` VALUES ('O16093782342730001', '金XX', '13851883538', 'XXX光里社XXXXXXX', 3, 117.30, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2020-12-31 09:30:34', '', '2021-01-16 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16093788550160001', '高XX', '15380890081', 'XXX光里社XXXXXXX', 4, 168.40, 168.40, 0.00, NULL, NULL, 0, NULL, '2020-12-31 09:41:39', 0, NULL, '', '2020-12-31 09:40:55', '', '2020-12-31 10:30:00');
INSERT INTO `demo_import_export` VALUES ('O16093788565220001', '高XX', '15380890081', 'XXX光里社XXXXXXX', 4, 168.40, 168.40, 0.00, NULL, NULL, 0, NULL, '2020-12-31 09:40:56', 0, NULL, '', '2020-12-31 09:40:56', '', '2020-12-31 10:30:00');
INSERT INTO `demo_import_export` VALUES ('O16093791675560001', '高XX', '15380890081', 'XXX光里社XXXXXXX', 3, 168.40, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2020-12-31 09:46:07', '', '2021-01-07 12:31:04');
INSERT INTO `demo_import_export` VALUES ('O16094872427670001', '张XX', '13951858098', 'XXX光里社XXXXXXX', 3, 33.60, 20.16, 0.00, NULL, NULL, 1, NULL, '2021-01-01 15:47:22', 0, NULL, '', '2021-01-01 15:47:22', '', '2021-01-17 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16094879186140001', '张XX', '13951858098', 'XXX光里社XXXXXXX', 3, 20.00, 12.00, 0.00, NULL, NULL, 1, NULL, '2021-01-01 15:58:38', 0, NULL, '', '2021-01-01 15:58:38', '', '2021-01-17 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16094880739450001', '张XX', '13951858098', 'XXX光里社XXXXXXX', 3, 24.20, 14.52, 0.00, NULL, NULL, 1, NULL, '2021-01-01 16:01:14', 0, NULL, '', '2021-01-01 16:01:13', '', '2021-01-17 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16097258989420001', '吴XX', '13818098030', 'XXX光里社XXXXXXX', 3, 77.80, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 10:04:58', '', '2021-01-20 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16097266717440001', '吴XX', '13818098030', 'XXX光里社XXXXXXX', 3, 24.50, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 10:17:51', '', '2021-01-20 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16097340136770001', '候XX', '18801588030', 'XXX花街道XXXXXXX', 3, 404.20, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 12:20:13', '', '2021-01-20 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16097378326300001', '候XX', '18801588030', 'XXX花街道XXXXXXX', 3, 31.00, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 13:23:52', '', '2021-01-20 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16097419847650001', '黑XX', '15105181188', 'XXX苏省南XXXXXXX', 3, 210.90, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-04 14:33:04', '', '2021-01-20 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16097456825210001', '吴XX', '13818098030', 'XXX光里社XXXXXXX', 3, 26.90, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 15:34:42', '', '2021-01-20 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16098118603040001', '蔡XX', '13918885385', 'XXX苏省南XXXXXXX', 3, 244.90, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 09:57:40', '', '2021-01-21 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16098122518450001', '蔡XX', '18388838889', 'XXX苏省南XXXXXXX', 3, 252.70, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 10:04:11', '', '2021-01-21 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16098137908580001', '毛XX', '13818038589', 'XXX苏省南XXXXXXX', 3, 512.10, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 10:29:50', '', '2021-01-21 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16098154108420001', '沈XX', '13951951893', 'XXX苏省南XXXXXXX', 3, 338.30, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 10:56:50', '', '2021-01-21 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16098160954260001', '李XX', '13951088900', 'XXX苏省南XXXXXXX', 3, 255.50, 0.50, 0.00, NULL, NULL, 1, NULL, '2021-01-05 11:19:45', 2, NULL, '', '2021-01-05 11:08:15', '', '2021-01-21 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16098165908470001', '李XX', '13951088900', 'XXX苏省南XXXXXXX', 3, 239.50, 239.50, 0.00, NULL, NULL, 1, NULL, '2021-01-05 11:16:30', 2, NULL, '', '2021-01-05 11:16:30', '', '2021-01-21 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16098343426360001', '许XX', '15150588888', 'XXX翔雅苑XXXXXXX', 3, 1245.40, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-05 16:12:22', '', '2021-01-21 01:00:00');

-- ----------------------------
-- Table structure for demo_order
-- ----------------------------
DROP TABLE IF EXISTS `demo_order`;
CREATE TABLE `demo_order`  (
                               `id` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '订单ID',
                               `user_name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
                               `user_phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '联系电话',
                               `user_address` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '收货地址',
                               `order_status` tinyint(1) NULL DEFAULT NULL COMMENT '订单状态',
                               `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '订单总价',
                               `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
                               `express_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '快递费用',
                               `order_desc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '订单描述',
                               `trade_no` varchar(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '支付流水',
                               `pay_type` tinyint(1) NULL DEFAULT NULL COMMENT '支付类型',
                               `pay_cert` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '支付凭证',
                               `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
                               `delivery_type` tinyint(1) NULL DEFAULT NULL COMMENT '配送方式',
                               `confirm_time` datetime NULL DEFAULT NULL COMMENT '收货时间',
                               `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '下单时间',
                               `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
                               `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '导入导出Demo' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of demo_order
-- ----------------------------
INSERT INTO `demo_order` VALUES ('O16093782342730001', '金XX', '13851883538', 'XXX光里社XXXXXXX', 3, 117.30, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2020-12-31 09:30:34', '', '2021-01-16 01:00:00');
INSERT INTO `demo_order` VALUES ('O16093788550160001', '高XX', '15380890081', 'XXX光里社XXXXXXX', 4, 168.40, 168.40, 0.00, NULL, NULL, 0, NULL, '2020-12-31 09:41:39', 0, NULL, '', '2020-12-31 09:40:55', '', '2020-12-31 10:30:00');
INSERT INTO `demo_order` VALUES ('O16093788565220001', '高XX', '15380890081', 'XXX光里社XXXXXXX', 4, 168.40, 168.40, 0.00, NULL, NULL, 0, NULL, '2020-12-31 09:40:56', 0, NULL, '', '2020-12-31 09:40:56', '', '2020-12-31 10:30:00');
INSERT INTO `demo_order` VALUES ('O16093791675560001', '高XX', '15380890081', 'XXX光里社XXXXXXX', 3, 168.40, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2020-12-31 09:46:07', '', '2021-01-07 12:31:04');
INSERT INTO `demo_order` VALUES ('O16094872427670001', '张XX', '13951858098', 'XXX光里社XXXXXXX', 3, 33.60, 20.16, 0.00, NULL, NULL, 1, NULL, '2021-01-01 15:47:22', 0, NULL, '', '2021-01-01 15:47:22', '', '2021-01-17 01:00:00');
INSERT INTO `demo_order` VALUES ('O16094879186140001', '张XX', '13951858098', 'XXX光里社XXXXXXX', 3, 20.00, 12.00, 0.00, NULL, NULL, 1, NULL, '2021-01-01 15:58:38', 0, NULL, '', '2021-01-01 15:58:38', '', '2021-01-17 01:00:00');
INSERT INTO `demo_order` VALUES ('O16094880739450001', '张XX', '13951858098', 'XXX光里社XXXXXXX', 3, 24.20, 14.52, 0.00, NULL, NULL, 1, NULL, '2021-01-01 16:01:14', 0, NULL, '', '2021-01-01 16:01:13', '', '2021-01-17 01:00:00');
INSERT INTO `demo_order` VALUES ('O16097258989420001', '吴XX', '13818098030', 'XXX光里社XXXXXXX', 3, 77.80, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 10:04:58', '', '2021-01-20 01:00:00');
INSERT INTO `demo_order` VALUES ('O16097266717440001', '吴XX', '13818098030', 'XXX光里社XXXXXXX', 3, 24.50, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 10:17:51', '', '2021-01-20 01:00:00');
INSERT INTO `demo_order` VALUES ('O16097340136770001', '候XX', '18801588030', 'XXX花街道XXXXXXX', 3, 404.20, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 12:20:13', '', '2021-01-20 01:00:00');
INSERT INTO `demo_order` VALUES ('O16097378326300001', '候XX', '18801588030', 'XXX花街道XXXXXXX', 3, 31.00, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 13:23:52', '', '2021-01-20 01:00:00');
INSERT INTO `demo_order` VALUES ('O16097419847650001', '黑XX', '15105181188', 'XXX苏省南XXXXXXX', 3, 210.90, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-04 14:33:04', '', '2021-01-20 01:00:00');
INSERT INTO `demo_order` VALUES ('O16097456825210001', '吴XX', '13818098030', 'XXX光里社XXXXXXX', 3, 26.90, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 15:34:42', '', '2021-01-20 01:00:00');
INSERT INTO `demo_order` VALUES ('O16098118603040001', '蔡XX', '13918885385', 'XXX苏省南XXXXXXX', 3, 244.90, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 09:57:40', '', '2021-01-21 01:00:00');
INSERT INTO `demo_order` VALUES ('O16098122518450001', '蔡XX', '18388838889', 'XXX苏省南XXXXXXX', 3, 252.70, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 10:04:11', '', '2021-01-21 01:00:00');
INSERT INTO `demo_order` VALUES ('O16098137908580001', '毛XX', '13818038589', 'XXX苏省南XXXXXXX', 3, 512.10, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 10:29:50', '', '2021-01-21 01:00:00');
INSERT INTO `demo_order` VALUES ('O16098154108420001', '沈XX', '13951951893', 'XXX苏省南XXXXXXX', 3, 338.30, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 10:56:50', '', '2021-01-21 01:00:00');
INSERT INTO `demo_order` VALUES ('O16098160954260001', '李XX', '13951088900', 'XXX苏省南XXXXXXX', 3, 255.50, 0.50, 0.00, NULL, NULL, 1, NULL, '2021-01-05 11:19:45', 2, NULL, '', '2021-01-05 11:08:15', '', '2021-01-21 01:00:00');
INSERT INTO `demo_order` VALUES ('O16098165908470001', '李XX', '13951088900', 'XXX苏省南XXXXXXX', 3, 239.50, 239.50, 0.00, NULL, NULL, 1, NULL, '2021-01-05 11:16:30', 2, NULL, '', '2021-01-05 11:16:30', '', '2021-01-21 01:00:00');
INSERT INTO `demo_order` VALUES ('O16098343426360001', '许XX', '15150588888', 'XXX翔雅苑XXXXXXX', 3, 1245.40, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-05 16:12:22', '', '2021-01-21 01:00:00');

-- ----------------------------
-- Table structure for demo_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `demo_order_detail`;
CREATE TABLE `demo_order_detail`  (
                                      `id` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '明细主键',
                                      `order_id` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '订单号',
                                      `goods_name` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '商品名称',
                                      `pic_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '商品货品图片或者商品图片',
                                      `goods_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品单价',
                                      `goods_price_pro` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品原价',
                                      `goods_count` smallint NULL DEFAULT NULL COMMENT '购买数量',
                                      `send_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
                                      `receive_time` datetime NULL DEFAULT NULL COMMENT '收货时间',
                                      `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际支付金额',
                                      `discount` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品优惠总金额',
                                      `coupon_discount` decimal(10, 2) NULL DEFAULT NULL COMMENT '优惠券扣减金额',
                                      `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
                                      `create_time` datetime NULL DEFAULT NULL COMMENT '下单时间',
                                      `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
                                      `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '销售订单明细' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of demo_order_detail
-- ----------------------------
INSERT INTO `demo_order_detail` VALUES ('08776afd4e4d11eb820300163e11f4a0', 'O16097378326300001', '天堂牌桂花莲子西湖藕粉 570克/袋', 'https://www.ecishan.com.cn/storage/681108-1.png', 31.00, 31.00, 1, NULL, NULL, 0.00, 31.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('0c0f30724e3311eb820300163e11f4a0', 'O16097266717440001', '李锦记草菇老抽1.75升/桶', 'https://www.ecishan.com.cn/storage/987076-1.png', 24.50, 24.50, 1, NULL, NULL, 0.00, 24.50, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c0fa4e4411eb820300163e11f4a0', 'O16097340136770001', '高露洁冰霜冰凉薄荷牙膏180克/支', 'https://www.ecishan.com.cn/storage/1141501-1.png', 13.80, 13.80, 1, NULL, NULL, 0.00, 13.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c12b4e4411eb820300163e11f4a0', 'O16097340136770001', '玉棠白砂糖1kg/袋', 'https://www.ecishan.com.cn/storage/file16057821768520001.png', 10.90, 10.90, 1, NULL, NULL, 0.00, 10.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c13c4e4411eb820300163e11f4a0', 'O16097340136770001', '淮牌低钠盐400克/袋', 'https://www.ecishan.com.cn/storage/1848905-1.png', 2.70, 2.70, 3, NULL, NULL, 0.00, 8.10, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c1484e4411eb820300163e11f4a0', 'O16097340136770001', '黑人超白牙膏190克/支', 'https://www.ecishan.com.cn/storage/20200324105431.jpg', 14.20, 14.20, 1, NULL, NULL, 0.00, 14.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c15a4e4411eb820300163e11f4a0', 'O16097340136770001', '黑人茶倍健牙膏 190克/支', 'https://www.ecishan.com.cn/storage/20200324105600.jpg', 14.50, 14.50, 1, NULL, NULL, 0.00, 14.50, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c1654e4411eb820300163e11f4a0', 'O16097340136770001', '金沙河富强高筋小麦粉 5千克/袋', 'https://www.ecishan.com.cn/storage/20200402131140.jpg', 31.70, 31.70, 1, NULL, NULL, 0.00, 31.70, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c1704e4411eb820300163e11f4a0', 'O16097340136770001', '燕庄黑芝麻香油 160毫升/瓶', 'https://www.ecishan.com.cn/storage/20191015154514.png', 18.30, 18.30, 1, NULL, NULL, 0.00, 18.30, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c17a4e4411eb820300163e11f4a0', 'O16097340136770001', '丹玉镇江香醋(一级) 550毫升/瓶', 'https://www.ecishan.com.cn/storage/20200318115045.jpg', 8.50, 8.50, 1, NULL, NULL, 0.00, 8.50, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c1864e4411eb820300163e11f4a0', 'O16097340136770001', '玉兰油美白润肤霜50g/瓶', 'https://www.ecishan.com.cn/storage/file16025749211490001.png', 69.00, 69.00, 2, NULL, NULL, 0.00, 138.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c1934e4411eb820300163e11f4a0', 'O16097340136770001', '丝宝宝无根肉厚黑木耳 160克/袋', 'https://www.ecishan.com.cn/storage/file16026434707580001.png', 39.80, 39.80, 1, NULL, NULL, 0.00, 39.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c19d4e4411eb820300163e11f4a0', 'O16097340136770001', '太古单晶冰糖 300g/袋', 'https://www.ecishan.com.cn/storage/file16027396632980001.png', 9.50, 9.50, 1, NULL, NULL, 0.00, 9.50, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c1a74e4411eb820300163e11f4a0', 'O16097340136770001', '鲁花压榨葵花仁油5升/瓶', 'https://www.ecishan.com.cn/storage/file16049105729540001.png', 96.90, 96.90, 1, NULL, NULL, 0.00, 96.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('281ae28e4c0711eb820300163e11f4a0', 'O16094879186140001', '汤达人日式豚骨拉面55克/杯', 'https://www.ecishan.com.cn/storage/file16039393209460001.png', 5.00, 5.00, 4, NULL, NULL, 12.00, 8.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('3921ff044b0911eb820300163e11f4a0', 'O16093788550160001', '雕牌超效加酶无磷洗衣粉2.68千克/袋', 'https://www.ecishan.com.cn/storage/714991-1.png', 24.30, 24.30, 2, NULL, NULL, 48.60, 0.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('3921ff394b0911eb820300163e11f4a0', 'O16093788550160001', '福临门苏软香 10kg/袋', 'https://www.ecishan.com.cn/storage/file16049116832980001.png', 59.90, 59.90, 2, NULL, NULL, 119.80, 0.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('3a07a7ce4b0911eb820300163e11f4a0', 'O16093788565220001', '雕牌超效加酶无磷洗衣粉2.68千克/袋', 'https://www.ecishan.com.cn/storage/714991-1.png', 24.30, 24.30, 2, NULL, NULL, 48.60, 0.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('3a07a7fb4b0911eb820300163e11f4a0', 'O16093788565220001', '福临门苏软香 10kg/袋', 'https://www.ecishan.com.cn/storage/file16049116832980001.png', 59.90, 59.90, 2, NULL, NULL, 119.80, 0.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('3f6f0ee84e3111eb820300163e11f4a0', 'O16097258989420001', '福临门东北优质大米', 'https://www.ecishan.com.cn/storage/file16024886579790001.jpg', 38.90, 38.90, 2, NULL, NULL, 0.00, 77.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('40baccd04f0311eb820300163e11f4a0', 'O16098160954260001', '【物流配送仅至楼下】金健东北水晶米 5kg/袋', 'https://www.ecishan.com.cn/storage/20200325112026.jpg', 39.10, 39.10, 1, NULL, NULL, 0.08, 39.02, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('40bacd034f0311eb820300163e11f4a0', 'O16098160954260001', '【物流配送仅至楼下】福临门东北优质大米', 'https://www.ecishan.com.cn/storage/file16024886579790001.jpg', 38.90, 38.90, 2, NULL, NULL, 0.15, 77.65, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('40bacd0f4f0311eb820300163e11f4a0', 'O16098160954260001', '【物流配送仅至楼下】金沙河富强高筋小麦粉 2.5千克/袋', 'https://www.ecishan.com.cn/storage/file16027314720870001.png', 18.80, 18.80, 1, NULL, NULL, 0.04, 18.76, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('40bacd1a4f0311eb820300163e11f4a0', 'O16098160954260001', '【物流配送仅至楼下】福临门苏软香 10kg/袋', 'https://www.ecishan.com.cn/storage/file16049116832980001.png', 59.90, 59.90, 2, NULL, NULL, 0.23, 119.57, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4dc6a5944efa11eb820300163e11f4a0', 'O16098122518450001', '洽洽山核桃味瓜子 108克/袋', 'https://www.ecishan.com.cn/storage/20200324095821.jpg', 6.50, 6.50, 5, NULL, NULL, 0.00, 32.50, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4dc6a5ce4efa11eb820300163e11f4a0', 'O16098122518450001', '中华健齿白清新薄荷味牙膏 200克/支', 'https://www.ecishan.com.cn/storage/20200324104151.jpg', 7.10, 7.10, 5, NULL, NULL, 0.00, 35.50, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4dc6a5de4efa11eb820300163e11f4a0', 'O16098122518450001', '上海防酸牙膏 178克/支', 'https://www.ecishan.com.cn/storage/20191028104552.jpg', 5.00, 5.00, 2, NULL, NULL, 0.00, 10.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4dc6a5e94efa11eb820300163e11f4a0', 'O16098122518450001', '洽洽小而香西瓜子奶油味 180克/袋', 'https://www.ecishan.com.cn/storage/20191128093507.jpg', 14.10, 14.10, 1, NULL, NULL, 0.00, 14.10, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4dc6a5f44efa11eb820300163e11f4a0', 'O16098122518450001', '洽洽香瓜子 380克/袋', 'https://www.ecishan.com.cn/storage/20191021101330.png', 17.90, 17.90, 1, NULL, NULL, 0.00, 17.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4dc6a5fe4efa11eb820300163e11f4a0', 'O16098122518450001', '华味亨纸皮核桃 225克/袋', 'https://www.ecishan.com.cn/storage/20191021101625.png', 23.70, 23.70, 1, NULL, NULL, 0.00, 23.70, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4dc6a6094efa11eb820300163e11f4a0', 'O16098122518450001', '【66折】洽洽每日坚果礼盒30日装', 'https://www.ecishan.com.cn/storage/file16088578690590001.jpg', 119.00, 119.00, 1, NULL, NULL, 0.00, 119.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4f5d98f94e5f11eb820300163e11f4a0', 'O16097456825210001', '旺旺仙贝经济装', 'https://www.ecishan.com.cn/storage/file16023185382890001.jpg', 26.90, 26.90, 1, NULL, NULL, 0.00, 26.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('646631954ef911eb820300163e11f4a0', 'O16098118603040001', '洽洽山核桃味瓜子 108克/袋', 'https://www.ecishan.com.cn/storage/20200324095821.jpg', 6.50, 6.50, 4, NULL, NULL, 0.00, 26.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('646631c64ef911eb820300163e11f4a0', 'O16098118603040001', '【87折】费列罗榛果威化巧克力（30粒装）375g/盒', 'https://www.ecishan.com.cn/storage/file16087866486720001.png', 99.90, 99.90, 1, NULL, NULL, 0.00, 99.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('646631d14ef911eb820300163e11f4a0', 'O16098118603040001', '【66折】洽洽每日坚果礼盒30日装', 'https://www.ecishan.com.cn/storage/file16088578690590001.jpg', 119.00, 119.00, 1, NULL, NULL, 0.00, 119.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('680527214f0411eb820300163e11f4a0', 'O16098165908470001', '【物流配送仅至楼下】香满园美味富强小麦粉 5千克/袋', 'https://www.ecishan.com.cn/storage/20191015143706.png', 29.90, 29.90, 2, NULL, NULL, 59.80, 0.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('680527544f0411eb820300163e11f4a0', 'O16098165908470001', '【物流配送仅至楼下】福临门苏软香 10kg/袋', 'https://www.ecishan.com.cn/storage/file16049116832980001.png', 59.90, 59.90, 3, NULL, NULL, 179.70, 0.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('84b0ed0f4c0711eb820300163e11f4a0', 'O16094880739450001', '洁云绒触感抽取式面巾S纤巧装', 'https://www.ecishan.com.cn/storage/20191028095402.jpg', 12.10, 12.10, 2, NULL, NULL, 14.52, 9.68, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('9544cdbf4c0511eb820300163e11f4a0', 'O16094872427670001', '香飘飘蓝莓奶茶 76克/杯', 'https://www.ecishan.com.cn/storage/file16040201903970001.png', 5.60, 5.60, 6, NULL, NULL, 20.16, 13.44, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af974e4f0111eb820300163e11f4a0', 'O16098154108420001', '金达日美锅铲', 'https://www.ecishan.com.cn/storage/20191022103807.jpg', 30.10, 30.10, 2, NULL, NULL, 0.00, 60.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af978b4f0111eb820300163e11f4a0', 'O16098154108420001', '太太乐蔬之鲜调味包 400克/包', 'https://www.ecishan.com.cn/storage/20191015152759.png', 15.90, 15.90, 1, NULL, NULL, 0.00, 15.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af97984f0111eb820300163e11f4a0', 'O16098154108420001', '特种兵生榨椰子汁植物蛋白饮料 245克*12罐/箱', 'https://www.ecishan.com.cn/storage/20191018104621.png', 48.60, 48.60, 1, NULL, NULL, 0.00, 48.60, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af97a54f0111eb820300163e11f4a0', 'O16098154108420001', '丝宝宝香菇150克/袋', 'https://www.ecishan.com.cn/storage/770796-1.png', 37.80, 37.80, 1, NULL, NULL, 0.00, 37.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af97b04f0111eb820300163e11f4a0', 'O16098154108420001', '【物流配送仅至楼下】福临门东北优质大米', 'https://www.ecishan.com.cn/storage/file16024886579790001.jpg', 38.90, 38.90, 1, NULL, NULL, 0.00, 38.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af97ba4f0111eb820300163e11f4a0', 'O16098154108420001', '丝宝宝无根肉厚黑木耳 160克/袋', 'https://www.ecishan.com.cn/storage/file16026434707580001.png', 39.80, 39.80, 1, NULL, NULL, 0.00, 39.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af97c44f0111eb820300163e11f4a0', 'O16098154108420001', '多力芥花油 5l/瓶', 'https://www.ecishan.com.cn/storage/file16027269216550001.png', 83.90, 83.90, 1, NULL, NULL, 0.00, 83.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af97d04f0111eb820300163e11f4a0', 'O16098154108420001', '双灯高级平版卫生纸 420张/包', 'https://www.ecishan.com.cn/storage/file16039555429720001.png', 6.60, 6.60, 2, NULL, NULL, 0.00, 13.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('b3550d5d4e5611eb820300163e11f4a0', 'O16097419847650001', '特种兵生榨椰子汁植物蛋白饮料 245克*12罐/箱', 'https://www.ecishan.com.cn/storage/20191018104621.png', 48.60, 48.60, 1, NULL, NULL, 0.00, 48.60, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('b3550d8b4e5611eb820300163e11f4a0', 'O16097419847650001', '蒙牛特仑苏纯牛奶 250ml*12盒/箱', 'https://www.ecishan.com.cn/storage/file16028316318400001.png', 65.40, 65.40, 1, NULL, NULL, 0.00, 65.40, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('b3550d9a4e5611eb820300163e11f4a0', 'O16097419847650001', '鲁花压榨葵花仁油5升/瓶', 'https://www.ecishan.com.cn/storage/file16049105729540001.png', 96.90, 96.90, 1, NULL, NULL, 0.00, 96.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('bce985864f2d11eb820300163e11f4a0', 'O16098343426360001', '康师傅经典红烧牛肉面量贩装5包入', 'https://www.ecishan.com.cn/storage/1032211-1.png', 12.30, 12.30, 8, NULL, NULL, 0.00, 98.40, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('bce985c04f2d11eb820300163e11f4a0', 'O16098343426360001', '康师傅老坛酸菜牛肉面量贩装5包入', 'https://www.ecishan.com.cn/storage/1061365-1.png', 12.30, 12.30, 8, NULL, NULL, 0.00, 98.40, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('bce985ce4f2d11eb820300163e11f4a0', 'O16098343426360001', '王府煮花生 560克/袋', 'https://www.ecishan.com.cn/storage/20191021102626.png', 19.10, 19.10, 22, NULL, NULL, 0.00, 420.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('bce985dd4f2d11eb820300163e11f4a0', 'O16098343426360001', '双汇王中王优级火腿肠400g', 'https://www.ecishan.com.cn/storage/20200318105550.jpg', 19.90, 19.90, 12, NULL, NULL, 0.00, 238.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('bce985e84f2d11eb820300163e11f4a0', 'O16098343426360001', '乐事马铃薯片（美国经典原味）75克/包', 'https://www.ecishan.com.cn/storage/file16023203489860001.jpg', 6.40, 6.40, 8, NULL, NULL, 0.00, 51.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('bce985f24f2d11eb820300163e11f4a0', 'O16098343426360001', '乐事马铃薯片（黄瓜味）75克/包', 'https://www.ecishan.com.cn/storage/file16023209673510001.jpg', 6.40, 6.40, 8, NULL, NULL, 0.00, 51.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('bce985fc4f2d11eb820300163e11f4a0', 'O16098343426360001', '【物流配送仅至楼下】荟尚东北珍珠米5kg/袋', 'https://www.ecishan.com.cn/storage/file16027286764320001.png', 35.90, 35.90, 8, NULL, NULL, 0.00, 287.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('c724864a4b0711eb820300163e11f4a0', 'O16093782342730001', '金健东北水晶米 5kg/袋', 'https://www.ecishan.com.cn/storage/20200325112026.jpg', 39.10, 39.10, 3, NULL, NULL, 0.00, 117.30, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('e319c64b4efd11eb820300163e11f4a0', 'O16098137908580001', '李锦记纯香芝麻油 410毫升/瓶', 'https://www.ecishan.com.cn/storage/20191015154621.png', 27.60, 27.60, 1, NULL, NULL, 0.00, 27.60, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('e319c67f4efd11eb820300163e11f4a0', 'O16098137908580001', '海天鲜味生抽 1.9升/桶', 'https://www.ecishan.com.cn/storage/20191015153445.png', 18.80, 18.80, 1, NULL, NULL, 0.00, 18.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('e319c68c4efd11eb820300163e11f4a0', 'O16098137908580001', '52度绵竹大曲 500ml/瓶', 'https://www.ecishan.com.cn/storage/20191021092400.jpg', 8.60, 8.60, 4, NULL, NULL, 0.00, 34.40, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('e319c6974efd11eb820300163e11f4a0', 'O16098137908580001', '海天草菇老抽 500ml/瓶', 'https://www.ecishan.com.cn/storage/file16026427299100001.png', 9.40, 9.40, 1, NULL, NULL, 0.00, 9.40, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('e319c6a24efd11eb820300163e11f4a0', 'O16098137908580001', '【物流配送仅至楼下】荟尚东北珍珠米5kg/袋', 'https://www.ecishan.com.cn/storage/file16027286764320001.png', 35.90, 35.90, 2, NULL, NULL, 0.00, 71.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('e319c6ae4efd11eb820300163e11f4a0', 'O16098137908580001', '鲁花压榨葵花仁油5升/瓶', 'https://www.ecishan.com.cn/storage/file16049105729540001.png', 96.90, 96.90, 1, NULL, NULL, 0.00, 96.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('e319c6b94efd11eb820300163e11f4a0', 'O16098137908580001', '【8折】皇冠丹麦曲奇饼干特别礼盒装 1.01kg/盒', 'https://www.ecishan.com.cn/storage/file16087791143760001.png', 126.60, 126.60, 2, NULL, NULL, 0.00, 253.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('f36c3a074b0911eb820300163e11f4a0', 'O16093791675560001', '雕牌超效加酶无磷洗衣粉2.68千克/袋', 'https://www.ecishan.com.cn/storage/714991-1.png', 24.30, 24.30, 2, NULL, NULL, 0.00, 48.60, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('f36c3a2f4b0911eb820300163e11f4a0', 'O16093791675560001', '福临门苏软香 10kg/袋', 'https://www.ecishan.com.cn/storage/file16049116832980001.png', 59.90, 59.90, 2, NULL, NULL, 0.00, 119.80, 0.00, '', NULL, '', NULL);

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
INSERT INTO `mf_screen_charts` VALUES ('067fc3b3686c79e3e8da6e0bd3eef261', '边框20', 'fb8d6852d275309854125a152f56e40b', 'MfBorder20', '215f01b567604507ba82a2db775e5aa4.png', 'ant-design:border-outlined', 20, NULL, 'admin', '2025-01-24 17:09:29', 'admin', '2025-03-07 12:03:13');
INSERT INTO `mf_screen_charts` VALUES ('074710f47f9fa4333ee479ac971a4b18', '标签选项', '688f4f25303f6ce601c99cc30db7dd5b', 'MfSegmented', '33f2a53f8ee54748b6f1c6e1507229fb.png', 'ant-design:select-outlined', 1, NULL, 'admin', '2025-03-17 15:24:50', 'admin', '2025-03-17 23:51:03');
INSERT INTO `mf_screen_charts` VALUES ('09c55daf8c0fade9dba7f1b6d0084f72', '标头2', '4a012bfdcac45a75617c03b9fd0d6b2c', 'MfHeader2', 'ed7bbdd231b04b98833fdaabda4731d8.png', 'carbon:open-panel-top', 2, NULL, 'admin', '2025-01-25 13:12:19', 'admin', '2025-03-07 11:57:40');
INSERT INTO `mf_screen_charts` VALUES ('0bd1545369c287336d3d4c27e80880b2', '圆形雷达图', '3055c9e5ce00720bbba2e7a423eeeaa3', 'MfRadarCircle', '7ff6e866f4af4a5dab7c4e2a8dfd61d2.png', 'ant-design:radar-chart-outlined', 3, NULL, 'admin', '2025-01-10 23:03:55', 'admin', '2025-03-07 11:53:04');
INSERT INTO `mf_screen_charts` VALUES ('109f453e507fd725275790426e966fa1', '普通标签', '6fab782d03d564025274e46a8ec63668', 'MfTag', 'd909cfda85ea4978b5a8075092ed2603.png', 'ant-design:tag-outlined', 1, NULL, 'admin', '2024-11-19 10:41:16', 'admin', '2025-03-07 11:56:24');
INSERT INTO `mf_screen_charts` VALUES ('167a0549cd9e6d7e9b3bc0be0d5648cf', '柱状图', 'fafa6224eb9d3ebe49fe21a20171b05d', 'MfBar', 'd3c4f94e9c2244b098b95cc233419626.png', 'ant-design:bar-chart-outlined', 1, NULL, 'admin', '2024-11-19 16:53:47', 'admin', '2025-03-07 12:05:34');
INSERT INTO `mf_screen_charts` VALUES ('1b162361b30689a1cfcf27b43e041749', '边框19', 'fb8d6852d275309854125a152f56e40b', 'MfBorder19', '4f2a831d03c14e35a47f9a1b64ae454b.png', 'ant-design:border-outlined', 19, NULL, 'admin', '2025-01-24 17:09:18', 'admin', '2025-03-07 12:03:05');
INSERT INTO `mf_screen_charts` VALUES ('25ae605d0ca38418dca22ad5b5b6e5a3', '边框15', 'fb8d6852d275309854125a152f56e40b', 'MfBorder15', '3cecf03270884610afcffd2947a1ed66.png', 'ant-design:border-outlined', 15, NULL, 'admin', '2025-01-24 17:08:06', 'admin', '2025-03-07 12:02:39');
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
INSERT INTO `mf_screen_charts` VALUES ('64c16f7b88d7691ce6285e39fc29aa50', '边框22', 'fb8d6852d275309854125a152f56e40b', 'MfBorder22', 'bdae320bc978404bbd20754498add4fe.png', 'ant-design:border-outlined', 22, NULL, 'admin', '2025-01-24 17:09:56', 'admin', '2025-03-07 12:03:27');
INSERT INTO `mf_screen_charts` VALUES ('74e749a05ce7da2003d021b4bdcb4762', '条形图', 'fafa6224eb9d3ebe49fe21a20171b05d', 'MfBarHorizontal', '0305185977d745c48786b311177f5f06.png', 'ant-design:bars-outlined', 2, NULL, 'admin', '2025-01-06 17:40:21', 'admin', '2025-03-07 11:55:51');
INSERT INTO `mf_screen_charts` VALUES ('75a5511ab31aa66db386a4c6916e5f0b', '饼状图', '9790b4ccb2e28f0d54c3aaa528ca3fea', 'MfPie', '30532a0099b2444c838acbe1b25e5a5a.png', 'ant-design:pie-chart-outlined', 1, NULL, 'admin', '2024-11-19 16:58:42', 'admin', '2025-03-07 11:53:38');
INSERT INTO `mf_screen_charts` VALUES ('79fa654f78f84ecf24900daf3d602b1e', '环形图', '9790b4ccb2e28f0d54c3aaa528ca3fea', 'MfPieCircular', 'eb6d4ceedc61442e8d6678e649d1d5df.png', 'ant-design:pie-chart-outlined', 2, NULL, 'admin', '2025-01-08 20:43:03', 'admin', '2025-03-07 11:53:44');
INSERT INTO `mf_screen_charts` VALUES ('7e2ba0b33a2a2cbdda6b5bb00a9d51cc', '玫瑰图', '9790b4ccb2e28f0d54c3aaa528ca3fea', 'MfPieRose', '463cf1f93390472e8af2f3721710f483.png', 'ant-design:pie-chart-outlined', 3, NULL, 'admin', '2025-01-08 20:33:43', 'admin', '2025-03-07 11:54:25');
INSERT INTO `mf_screen_charts` VALUES ('8277aec293e04b61e1c438775d6f4638', '折线图', 'afe955d350d5ac5ef541f145ffa7706f', 'MfLine', 'ed31d5a315c64c8bba47285288ec5f1d.png', 'ant-design:line-chart-outlined', 1, NULL, 'admin', '2024-11-19 16:58:18', 'admin', '2025-03-07 11:54:16');
INSERT INTO `mf_screen_charts` VALUES ('8ae2d15251ac3f27a481d9125bfca22e', '散点图', '3055c9e5ce00720bbba2e7a423eeeaa3', 'MfScatter', '92c1974d930145258308d063069bb2e9.png', 'ant-design:dot-chart-outlined', 1, NULL, 'admin', '2025-01-10 10:36:02', 'admin', '2025-03-07 11:52:52');
INSERT INTO `mf_screen_charts` VALUES ('8fa9b01c997857433b274b38beea3ee0', '边框17', 'fb8d6852d275309854125a152f56e40b', 'MfBorder17', '594e55ae822047d0ab9e3efe2f4440cd.png', 'ant-design:border-outlined', 17, NULL, 'admin', '2025-01-24 17:08:42', 'admin', '2025-03-07 12:02:53');
INSERT INTO `mf_screen_charts` VALUES ('97c8d5db2ceea7b59166ae89d8abecc8', '环形占比图', '8fcbb99d3e9550f5e9d30dd592a67352', 'MfWheel', 'd2b5ef51d32e4691a6df762837035d41.png', 'ant-design:pie-chart-outlined', 1, NULL, 'admin', '2025-01-14 10:13:06', 'admin', '2025-03-07 11:53:21');
INSERT INTO `mf_screen_charts` VALUES ('9ce666cb7016b37c39295ba808512a29', '边框14', 'fb8d6852d275309854125a152f56e40b', 'MfBorder14', '2279cd951b274ef0b054cedb7704cfbd.png', 'ant-design:border-outlined', 14, NULL, 'admin', '2025-01-24 17:07:29', 'admin', '2025-03-07 12:02:32');
INSERT INTO `mf_screen_charts` VALUES ('a4877e2f8a1499569af1ffd985674dd7', '条形占比图', '8fcbb99d3e9550f5e9d30dd592a67352', 'MfTireMarks', 'e509d6c4fe2b45a3b20b3a4d6008d16d.png', 'ant-design:box-plot-outlined', 2, NULL, 'admin', '2025-01-14 17:32:47', 'admin', '2025-03-07 11:53:29');
INSERT INTO `mf_screen_charts` VALUES ('a5b987add5a1bbae0cac6666519de77d', '边框16', 'fb8d6852d275309854125a152f56e40b', 'MfBorder16', 'cb68e96249d94c39b49b34cf9942ad7f.png', 'ant-design:border-outlined', 16, NULL, 'admin', '2025-01-24 17:08:26', 'admin', '2025-03-07 12:02:46');
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
INSERT INTO `mf_screen_charts` VALUES ('e1a2899ac43711efa95fb03cdc9cfd05', '边框7', 'fb8d6852d275309854125a152f56e40b', 'MfBorder7', '85a28d8186484e8fad91770704a4b8b7.png', 'ant-design:border-outlined', 7, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:01:30');
INSERT INTO `mf_screen_charts` VALUES ('e1a2f1a7c43711efa95fb03cdc9cfd05', '边框8', 'fb8d6852d275309854125a152f56e40b', 'MfBorder8', 'c25c1e5e557f4f4396efe9f4644d6f4a.gif', 'ant-design:border-outlined', 8, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:01:39');
INSERT INTO `mf_screen_charts` VALUES ('e1a36571c43711efa95fb03cdc9cfd05', '边框9', 'fb8d6852d275309854125a152f56e40b', 'MfBorder9', '8f98d14f29314630b056eb5324347d56.png', 'ant-design:border-outlined', 9, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:01:50');
INSERT INTO `mf_screen_charts` VALUES ('e1a3cf90c43711efa95fb03cdc9cfd05', '边框10', 'fb8d6852d275309854125a152f56e40b', 'MfBorder10', '4c137a6f45fb453281af0bb782841cae.png', 'ant-design:border-outlined', 10, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:01:58');
INSERT INTO `mf_screen_charts` VALUES ('e1a4484ac43711efa95fb03cdc9cfd05', '边框11', 'fb8d6852d275309854125a152f56e40b', 'MfBorder11', '02616e0fe2884bb1b161753477c3bdb8.png', 'ant-design:border-outlined', 11, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:02:06');
INSERT INTO `mf_screen_charts` VALUES ('e1a4c54ac43711efa95fb03cdc9cfd05', '边框12', 'fb8d6852d275309854125a152f56e40b', 'MfBorder12', '517f5a86efe94253b4071efbb947da0d.png', 'ant-design:border-outlined', 12, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:02:19');
INSERT INTO `mf_screen_charts` VALUES ('e1a547e0c43711efa95fb03cdc9cfd05', '边框13', 'fb8d6852d275309854125a152f56e40b', 'MfBorder13', '93be448ec74c47cabde12208ee36e175.png', 'ant-design:border-outlined', 13, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:02:26');
INSERT INTO `mf_screen_charts` VALUES ('e468e465106ce5b3518cc84cee249572', '日期时间', 'aa4c61f29c59e7c982ec2255a7ab6316', 'MfDateTime', '579308c0050b478499c29cb1c71628eb.png', 'ant-design:field-time-outlined', 1, NULL, 'admin', '2025-01-11 23:42:30', 'admin', '2025-03-07 11:56:46');
INSERT INTO `mf_screen_charts` VALUES ('e609613c1c85cedb8c2bf393f82de2c9', '折柱混合图', 'fafa6224eb9d3ebe49fe21a20171b05d', 'MfBarLine', '34a0407c9bf04aebbc56a1a9bb70c885.png', 'ant-design:bar-chart-outlined', 4, NULL, 'admin', '2025-01-09 10:50:24', 'admin', '2025-03-07 11:56:04');
INSERT INTO `mf_screen_charts` VALUES ('eb0531ebc45e11efae65b03cdc9cfd05', '装饰1', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration1', 'f2b7e141c5294c80857bb63ee64538db.gif', 'ant-design:skin-outlined', 1, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 11:58:47');
INSERT INTO `mf_screen_charts` VALUES ('eb06df29c45e11efae65b03cdc9cfd05', '装饰2', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration2', '00e9837eabb648038d0fa89bb98c7025.gif', 'ant-design:skin-outlined', 2, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 11:58:56');
INSERT INTO `mf_screen_charts` VALUES ('eb080345c45e11efae65b03cdc9cfd05', '装饰3', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration3', '20f35e75bd794d71833479766d173aa8.gif', 'ant-design:skin-outlined', 3, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 11:59:05');
INSERT INTO `mf_screen_charts` VALUES ('eb0931fac45e11efae65b03cdc9cfd05', '装饰4', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration4', '7b85680707a148b6b03e62415e5efcaa.gif', 'ant-design:skin-outlined', 4, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 11:59:17');
INSERT INTO `mf_screen_charts` VALUES ('eb0a6b3fc45e11efae65b03cdc9cfd05', '装饰5', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration5', '264345057cb64813b3be2ff5bed097ad.gif', 'ant-design:skin-outlined', 5, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 11:59:25');
INSERT INTO `mf_screen_charts` VALUES ('eb0be1bfc45e11efae65b03cdc9cfd05', '装饰6', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration6', 'c99678bbcae845758cbd1416bc69e720.gif', 'ant-design:skin-outlined', 6, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 11:59:33');
INSERT INTO `mf_screen_charts` VALUES ('eb0c7ba4c45e11efae65b03cdc9cfd05', '装饰7', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration7', 'cff28ab7d82144d4b7e437110e651a8d.png', 'ant-design:skin-outlined', 7, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 11:59:41');
INSERT INTO `mf_screen_charts` VALUES ('eb0d4bafc45e11efae65b03cdc9cfd05', '装饰8', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration8', 'a5b74d7cb10242db931a9fcd7289f6b2.png', 'ant-design:skin-outlined', 8, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 11:59:50');
INSERT INTO `mf_screen_charts` VALUES ('eb0de5b8c45e11efae65b03cdc9cfd05', '装饰9', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration9', 'bc8718bfef2448d8b13ea505c9d5a899.gif', 'ant-design:skin-outlined', 9, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 11:59:58');
INSERT INTO `mf_screen_charts` VALUES ('eb0e7c1ac45e11efae65b03cdc9cfd05', '装饰10', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration10', '267a39b74db944d4bb6e3fd12de92059.gif', 'ant-design:skin-outlined', 10, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:00:06');
INSERT INTO `mf_screen_charts` VALUES ('eb0f153ac45e11efae65b03cdc9cfd05', '装饰11', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration11', '17ae3222f60a4139b4c484189974f1fd.png', 'ant-design:skin-outlined', 11, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:00:14');
INSERT INTO `mf_screen_charts` VALUES ('eb0fab28c45e11efae65b03cdc9cfd05', '装饰12', '862f955f4ba4cb11266a22d8ef023913', 'MfDecoration12', '1e41c6aa0dd7414c9496067504ae2438.gif', 'ant-design:skin-outlined', 12, NULL, 'admin', '2024-12-27 14:55:51', 'admin', '2025-03-07 12:00:29');
INSERT INTO `mf_screen_charts` VALUES ('f1f1b74e7400eda0e46eacefdb3255bf', '滚动表格', 'b5af416292a8659fcd2bc8d58164bf32', 'MfScrollTable', '689726a0e4994843aad420650aead081.gif', 'ant-design:table-outlined', 1, NULL, 'admin', '2025-01-12 23:23:13', 'admin', '2025-03-07 11:56:57');
INSERT INTO `mf_screen_charts` VALUES ('f25f1316d6fc5fd3ebd24ba830f97c1b', '自定义图片', '00491f886354990b6aef2e7a41f397ba', 'MfPicture', 'bb5bb5f720d04596a7dbcfc230e83e02.png', 'ant-design:picture-outlined', 1, NULL, 'admin', '2025-01-23 20:53:39', 'admin', '2025-03-07 11:52:07');
INSERT INTO `mf_screen_charts` VALUES ('fc5e9fcefa429a41a2dfbde95bd8dec6', '边框21', 'fb8d6852d275309854125a152f56e40b', 'MfBorder21', '59dcee71787d459dbfe4a159c52f707a.png', 'ant-design:border-outlined', 21, NULL, 'admin', '2025-01-24 17:09:42', 'admin', '2025-03-07 12:03:19');
INSERT INTO `mf_screen_charts` VALUES ('fff5b31bc1f6f7cd72c301b52097698f', '边框18', 'fb8d6852d275309854125a152f56e40b', 'MfBorder18', '3106ba6c50c74ebea13db09226460126.png', 'ant-design:border-outlined', 18, NULL, 'admin', '2025-01-24 17:08:59', 'admin', '2025-03-07 12:02:59');
INSERT INTO `mf_screen_charts` VALUES ('aca0f3cfbc9963495240bf3fbb0d0e53', '普通表格', 'b5af416292a8659fcd2bc8d58164bf32', 'MfTable', '63d99e78414c4cc4bf67857f0dd3f5b5.png', 'ant-design:table-outlined', 1, NULL, 'admin', '2025-06-04 17:05:56', 'admin', '2025-06-05 10:31:16');
INSERT INTO `mf_screen_charts` VALUES ('a872143fdfc55e48f076f800cf716ce0', '下拉选择', '688f4f25303f6ce601c99cc30db7dd5b', 'MfSelect', '55b3e5199d4b4488aa62159ad9a9064d.png', 'carbon:list-dropdown', 2, NULL, 'admin', '2025-06-09 10:03:23', 'admin', '2025-07-02 17:47:31');
INSERT INTO `mf_screen_charts` VALUES ('af909fb2f353cda6d6ddbf53ac4091e1', '按钮', '3c94138cae5b3a485858e589618e58d0', 'MfButton', 'c7d4a99f49034feb81aab8f45ee3dca3.png', 'carbon:button-centered', 1, NULL, 'admin', '2025-06-20 17:59:13', 'admin', '2025-07-02 18:00:41');
INSERT INTO `mf_screen_charts` VALUES ('00f7615ab1def50072f5a63d080d379c', '分布图', '27c665c8c4c8a1ca378b8c2342e75cf6', 'MfMapChunks', 'fa8913b0237045cd9fa13999ee1a73ab.png', 'carbon:map', 3, NULL, 'admin', '2025-07-04 11:24:54', 'admin', '2025-07-04 16:14:00');
INSERT INTO `mf_screen_charts` VALUES ('fae4e2667e1309fe194ba967ed085a7f', '气泡图', '27c665c8c4c8a1ca378b8c2342e75cf6', 'MfMapScatter', 'bc3cb95c0bf949138268b565975bcefe.png', 'carbon:map', NULL, NULL, 'admin', '2025-07-04 17:42:46', 'admin', '2025-07-06 00:04:16');
INSERT INTO `mf_screen_charts` VALUES ('bec3552d3f3d7c7eed54ebcaebe8853f', '文本输入库', 'fc74625ae4c3a53cbea0eb7bdd994a9a', 'MfInputTextArea', '15ec81b4946e499ea435b4694724215b.png', 'ant-design:edit-outlined', 2, NULL, 'admin', '2025-07-08 15:52:24', 'admin', '2025-07-08 16:19:07');
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

SET FOREIGN_KEY_CHECKS = 1;
