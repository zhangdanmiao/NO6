package com.cl.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cl.service.JiuzhentongzhiService;

@Component
public class NotificationRetryTask {

    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;

    @Scheduled(fixedRate = 60000)
    public void retryFailedNotifications() {
        System.out.println("执行通知重试任务 - " + new java.util.Date());
        jiuzhentongzhiService.retryFailedNotifications();
    }
}
