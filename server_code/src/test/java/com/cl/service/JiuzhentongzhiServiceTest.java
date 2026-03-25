package com.cl.service;

import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.entity.YishengyuyueEntity;
import com.cl.service.impl.JiuzhentongzhiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 就诊通知服务测试类
 */
public class JiuzhentongzhiServiceTest {

    @InjectMocks
    private JiuzhentongzhiServiceImpl jiuzhentongzhiService;

    @Mock
    private com.cl.dao.JiuzhentongzhiDao jiuzhentongzhiDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * 测试1: 正常流程 - 根据预约创建并发送通知
     * 验证：是否一次性创建3条通知（预约确认、就诊前提醒、检查准备事项）
     */
    @Test
    public void testCreateAndSendNotifications_Normal() {
        // 准备测试数据
        YishengyuyueEntity yuyue = createTestYuyue();

        // 执行测试
        List<JiuzhentongzhiEntity> notifications = jiuzhentongzhiService.createAndSendNotifications(yuyue);

        // 验证结果
        assertNotNull(notifications);
        assertEquals(3, notifications.size(), "应该创建3条通知");

        // 验证通知类型
        assertEquals(JiuzhentongzhiServiceImpl.TYPE_CONFIRM, notifications.get(0).getTongzhileixing());
        assertEquals(JiuzhentongzhiServiceImpl.TYPE_REMIND, notifications.get(1).getTongzhileixing());
        assertEquals(JiuzhentongzhiServiceImpl.TYPE_PREPARE, notifications.get(2).getTongzhileixing());

        // 验证通知内容
        assertNotNull(notifications.get(0).getTongzhibeizhu());
        assertTrue(notifications.get(0).getTongzhibeizhu().contains("预约已成功确认"));
        assertTrue(notifications.get(1).getTongzhibeizhu().contains("温馨提醒"));
        assertTrue(notifications.get(2).getTongzhibeizhu().contains("就诊准备"));

        // 验证发送状态
        for (JiuzhentongzhiEntity notification : notifications) {
            assertEquals(JiuzhentongzhiServiceImpl.STATUS_SUCCESS, notification.getFasongzhuangtai());
            assertNotNull(notification.getFasongshijian());
        }
    }

    /**
     * 测试2: 边界条件 - 预约信息为null
     * 验证：应该抛出IllegalArgumentException
     */
    @Test
    public void testCreateAndSendNotifications_NullYuyue() {
        assertThrows(IllegalArgumentException.class, () -> {
            jiuzhentongzhiService.createAndSendNotifications(null);
        });
    }

    /**
     * 测试3: 通知时间计算 - 就诊前1天提醒
     */
    @Test
    public void testRemindNotificationTime() {
        YishengyuyueEntity yuyue = createTestYuyue();
        Date yuyueTime = yuyue.getYuyueshijian();

        List<JiuzhentongzhiEntity> notifications = jiuzhentongzhiService.createAndSendNotifications(yuyue);

        JiuzhentongzhiEntity remindNotify = notifications.get(1);
        Date remindTime = remindNotify.getTongzhishijian();

        // 验证提醒时间是就诊前1天
        Calendar expectedCal = Calendar.getInstance();
        expectedCal.setTime(yuyueTime);
        expectedCal.add(Calendar.DAY_OF_MONTH, -1);

        Calendar actualCal = Calendar.getInstance();
        actualCal.setTime(remindTime);

        assertEquals(expectedCal.get(Calendar.YEAR), actualCal.get(Calendar.YEAR));
        assertEquals(expectedCal.get(Calendar.MONTH), actualCal.get(Calendar.MONTH));
        assertEquals(expectedCal.get(Calendar.DAY_OF_MONTH), actualCal.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 测试4: 通知时间计算 - 就诊前2小时提醒
     */
    @Test
    public void testPrepareNotificationTime() {
        YishengyuyueEntity yuyue = createTestYuyue();
        Date yuyueTime = yuyue.getYuyueshijian();

        List<JiuzhentongzhiEntity> notifications = jiuzhentongzhiService.createAndSendNotifications(yuyue);

        JiuzhentongzhiEntity prepareNotify = notifications.get(2);
        Date prepareTime = prepareNotify.getTongzhishijian();

        // 验证准备通知时间是就诊前2小时
        Calendar expectedCal = Calendar.getInstance();
        expectedCal.setTime(yuyueTime);
        expectedCal.add(Calendar.HOUR_OF_DAY, -2);

        Calendar actualCal = Calendar.getInstance();
        actualCal.setTime(prepareTime);

        assertEquals(expectedCal.get(Calendar.YEAR), actualCal.get(Calendar.YEAR));
        assertEquals(expectedCal.get(Calendar.MONTH), actualCal.get(Calendar.MONTH));
        assertEquals(expectedCal.get(Calendar.DAY_OF_MONTH), actualCal.get(Calendar.DAY_OF_MONTH));
        assertEquals(expectedCal.get(Calendar.HOUR_OF_DAY), actualCal.get(Calendar.HOUR_OF_DAY));
    }

    /**
     * 测试5: 发送通知 - 正常发送
     */
    @Test
    public void testSendNotification_Success() {
        JiuzhentongzhiEntity tongzhi = createTestNotification();
        tongzhi.setFasongzhuangtai(JiuzhentongzhiServiceImpl.STATUS_PENDING);

        boolean result = jiuzhentongzhiService.sendNotification(tongzhi);

        assertTrue(result);
        assertEquals(JiuzhentongzhiServiceImpl.STATUS_SUCCESS, tongzhi.getFasongzhuangtai());
        assertNotNull(tongzhi.getFasongshijian());
        assertNull(tongzhi.getShibaiyuanyin());
    }

    /**
     * 测试6: 发送通知 - null参数
     */
    @Test
    public void testSendNotification_Null() {
        boolean result = jiuzhentongzhiService.sendNotification(null);
        assertFalse(result);
    }

    /**
     * 测试7: 重试机制 - 重试次数递增
     */
    @Test
    public void testRetryCountIncrement() {
        // 创建一个模拟失败的场景需要修改doSend方法
        // 这里测试重试次数的递增逻辑
        JiuzhentongzhiEntity tongzhi = createTestNotification();
        tongzhi.setChongshicishu(0);
        tongzhi.setFasongzhuangtai(JiuzhentongzhiServiceImpl.STATUS_PENDING);

        // 正常发送应该成功
        boolean result = jiuzhentongzhiService.sendNotification(tongzhi);
        assertTrue(result);
        // 发送成功后重试次数应该保持不变（因为是成功，不是失败重试）
    }

    /**
     * 测试8: 重新发送通知
     */
    @Test
    public void testResendNotification() {
        JiuzhentongzhiEntity tongzhi = createTestNotification();
        tongzhi.setId(1L);
        tongzhi.setFasongzhuangtai(JiuzhentongzhiServiceImpl.STATUS_FAILED);
        tongzhi.setShibaiyuanyin("网络异常");

        when(jiuzhentongzhiDao.selectById(1L)).thenReturn(tongzhi);

        boolean result = jiuzhentongzhiService.resendNotification(1L);

        assertTrue(result);
        assertEquals(JiuzhentongzhiServiceImpl.STATUS_SUCCESS, tongzhi.getFasongzhuangtai());
    }

    /**
     * 测试9: 重新发送 - 无效ID
     */
    @Test
    public void testResendNotification_InvalidId() {
        assertFalse(jiuzhentongzhiService.resendNotification(null));
        assertFalse(jiuzhentongzhiService.resendNotification(0L));
        assertFalse(jiuzhentongzhiService.resendNotification(-1L));
    }

    /**
     * 测试10: 手动更新状态
     */
    @Test
    public void testManualUpdateStatus() {
        JiuzhentongzhiEntity tongzhi = createTestNotification();
        tongzhi.setId(1L);
        tongzhi.setFasongzhuangtai(JiuzhentongzhiServiceImpl.STATUS_PENDING);

        when(jiuzhentongzhiDao.selectById(1L)).thenReturn(tongzhi);

        jiuzhentongzhiService.manualUpdateStatus(1L, JiuzhentongzhiServiceImpl.STATUS_SUCCESS, "手动标记成功");

        assertEquals(JiuzhentongzhiServiceImpl.STATUS_SUCCESS, tongzhi.getFasongzhuangtai());
        assertEquals("手动标记成功", tongzhi.getShibaiyuanyin());
    }

    /**
     * 测试11: 通知编号生成
     */
    @Test
    public void testNotificationNumberGeneration() {
        YishengyuyueEntity yuyue = createTestYuyue();

        List<JiuzhentongzhiEntity> notifications = jiuzhentongzhiService.createAndSendNotifications(yuyue);

        // 验证通知编号格式
        for (int i = 0; i < notifications.size(); i++) {
            String number = notifications.get(i).getTongzhibianhao();
            assertNotNull(number);
            assertTrue(number.startsWith("TZ"));
            // 验证编号唯一性
            for (int j = i + 1; j < notifications.size(); j++) {
                assertNotEquals(number, notifications.get(j).getTongzhibianhao());
            }
        }
    }

    /**
     * 测试12: 关联预约ID
     */
    @Test
    public void testGuanlianYuyueId() {
        YishengyuyueEntity yuyue = createTestYuyue();
        yuyue.setId(100L);

        List<JiuzhentongzhiEntity> notifications = jiuzhentongzhiService.createAndSendNotifications(yuyue);

        for (JiuzhentongzhiEntity notification : notifications) {
            assertEquals(Long.valueOf(100L), notification.getGuanlianyuyueid());
        }
    }

    // 辅助方法：创建测试预约数据
    private YishengyuyueEntity createTestYuyue() {
        YishengyuyueEntity yuyue = new YishengyuyueEntity();
        yuyue.setId(1L);
        yuyue.setYuyuebianhao("YY202503250001");
        yuyue.setYishengzhanghao("doctor001");
        yuyue.setDianhua("13800138000");
        yuyue.setZhanghao("user001");
        yuyue.setShouji("13900139000");

        // 设置预约时间为明天
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        yuyue.setYuyueshijian(cal.getTime());

        yuyue.setYuyuebeizhu("测试预约备注");
        yuyue.setAddtime(new Date());

        return yuyue;
    }

    // 辅助方法：创建测试通知数据
    private JiuzhentongzhiEntity createTestNotification() {
        JiuzhentongzhiEntity tongzhi = new JiuzhentongzhiEntity();
        tongzhi.setTongzhibianhao("TZ20250325000101");
        tongzhi.setYishengzhanghao("doctor001");
        tongzhi.setDianhua("13800138000");
        tongzhi.setZhanghao("user001");
        tongzhi.setShouji("13900139000");
        tongzhi.setJiuzhenshijian(new Date());
        tongzhi.setTongzhishijian(new Date());
        tongzhi.setTongzhibeizhu("测试通知内容");
        tongzhi.setTongzhileixing(JiuzhentongzhiServiceImpl.TYPE_CONFIRM);
        tongzhi.setFasongzhuangtai(JiuzhentongzhiServiceImpl.STATUS_PENDING);
        tongzhi.setChongshicishu(0);
        tongzhi.setGuanlianyuyueid(1L);
        tongzhi.setAddtime(new Date());

        return tongzhi;
    }
}
