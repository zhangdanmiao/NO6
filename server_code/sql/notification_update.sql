-- 就诊通知功能优化数据库更新脚本
-- 更新时间: 2025-03-27

-- 添加新字段到jiuzhentongzhi表
ALTER TABLE jiuzhentongzhi 
ADD COLUMN tongzhileixing INT(11) DEFAULT NULL COMMENT '通知类型：1-预约确认 2-就诊前提醒 3-检查准备事项 4-其他提醒',
ADD COLUMN fasongzhuangtai INT(11) DEFAULT 0 COMMENT '发送状态：0-待发送 1-发送成功 2-发送失败',
ADD COLUMN fasongshijian DATETIME DEFAULT NULL COMMENT '发送时间',
ADD COLUMN shibaiyuanyin VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
ADD COLUMN chongshicishu INT(11) DEFAULT 0 COMMENT '重试次数',
ADD COLUMN xiacichongshishijian DATETIME DEFAULT NULL COMMENT '下次重试时间',
ADD COLUMN guanlianyuyueid BIGINT(20) DEFAULT NULL COMMENT '关联预约ID';

-- 创建索引优化查询性能
CREATE INDEX idx_fasongzhuangtai ON jiuzhentongzhi(fasongzhuangtai);
CREATE INDEX idx_chongshicishu ON jiuzhentongzhi(chongshicishu);
CREATE INDEX idx_xiacichongshishijian ON jiuzhentongzhi(xiacichongshishijian);
CREATE INDEX idx_guanlianyuyueid ON jiuzhentongzhi(guanlianyuyueid);
CREATE INDEX idx_zhanghao ON jiuzhentongzhi(zhanghao);
CREATE INDEX idx_addtime ON jiuzhentongzhi(addtime);

-- 创建复合索引用于重试查询
CREATE INDEX idx_retry_query ON jiuzhentongzhi(fasongzhuangtai, chongshicishu, xiacichongshishijian);
