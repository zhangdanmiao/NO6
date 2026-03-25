package com.cl.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cl.service.JiuzhentongzhiService;

/**
 * 通知重试定时任务
 * 定期扫描并重新发送失败的通知
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
@Component
public class NotificationRetryTask {

    private static final Logger logger = LoggerFactory.getLogger(NotificationRetryTask.class);

    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;

    /**
     * 每5分钟执行一次失败通知重试
     * 检查是否有需要重试的失败通知并进行重发
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5分钟
    public void retryFailedNotifications() {
        logger.info("开始执行通知重试任务...");
        try {
            jiuzhentongzhiService.batchRetryFailedNotifications();
            logger.info("通知重试任务执行完成");
        } catch (Exception e) {
            logger.error("通知重试任务执行失败：", e);
        }
    }
}
