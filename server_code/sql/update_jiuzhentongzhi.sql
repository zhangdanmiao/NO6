-- 更新就诊通知表结构
-- 添加通知状态跟踪相关字段

USE `cl515882190`;

ALTER TABLE `jiuzhentongzhi` 
ADD COLUMN `tongzhileixing` INT DEFAULT 0 COMMENT '通知类型：0-预约确认，1-就诊前提醒，2-检查准备事项',
ADD COLUMN `fasongzhuangtai` INT DEFAULT 0 COMMENT '发送状态：0-待发送，1-发送成功，2-发送失败',
ADD COLUMN `shibaiyuanyin` VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
ADD COLUMN `chongshicishu` INT DEFAULT 0 COMMENT '重试次数',
ADD COLUMN `xiacichongshishijian` DATETIME DEFAULT NULL COMMENT '下次重试时间',
ADD COLUMN `yuyueid` BIGINT DEFAULT NULL COMMENT '关联预约ID';

-- 更新现有数据的发送状态为成功
UPDATE `jiuzhentongzhi` SET `fasongzhuangtai` = 1, `tongzhileixing` = 0;
