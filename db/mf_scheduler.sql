/**
  调度策略相关表，使用系统提供调度服务需要用到相关表（采用微服务启动，需要创建该库）
 */
DROP DATABASE IF EXISTS `mf_scheduler`;
CREATE DATABASE  `mf_scheduler` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE `mf_scheduler`;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时调度任务';

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='定时任务调度日志';

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

SET FOREIGN_KEY_CHECKS = 1;
