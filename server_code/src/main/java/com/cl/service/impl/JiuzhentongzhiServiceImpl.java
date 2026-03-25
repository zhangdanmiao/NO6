package com.cl.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;


import com.cl.dao.JiuzhentongzhiDao;
import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.entity.YishengyuyueEntity;
import com.cl.service.JiuzhentongzhiService;
import com.cl.entity.view.JiuzhentongzhiView;

@Service("jiuzhentongzhiService")
public class JiuzhentongzhiServiceImpl extends ServiceImpl<JiuzhentongzhiDao, JiuzhentongzhiEntity> implements JiuzhentongzhiService {
    	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<JiuzhentongzhiEntity> page = this.selectPage(
                new Query<JiuzhentongzhiEntity>(params).getPage(),
                new EntityWrapper<JiuzhentongzhiEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<JiuzhentongzhiEntity> wrapper) {
		  Page<JiuzhentongzhiView> page =new Query<JiuzhentongzhiView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<JiuzhentongzhiView> selectListView(Wrapper<JiuzhentongzhiEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public JiuzhentongzhiView selectView(Wrapper<JiuzhentongzhiEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}
	
	@Override
	public void createAndSendAllNotifications(YishengyuyueEntity yuyue) {
		List<JiuzhentongzhiEntity> notifications = new ArrayList<>();
		
		Date now = new Date();
		String notificationNo = "TZ" + System.currentTimeMillis();
		
		JiuzhentongzhiEntity confirmNotification = createNotification(
			yuyue, 0, "预约确认通知", 
			"您已成功预约就诊，请准时到达。预约编号：" + yuyue.getYuyuebianhao(),
			notificationNo + "-01"
		);
		notifications.add(confirmNotification);
		
		JiuzhentongzhiEntity reminderNotification = createNotification(
			yuyue, 1, "就诊前提醒", 
			"就诊前提醒：请携带相关证件和既往病历资料，提前30分钟到达医院。",
			notificationNo + "-02"
		);
		notifications.add(reminderNotification);
		
		JiuzhentongzhiEntity prepNotification = createNotification(
			yuyue, 2, "检查准备事项", 
			"检查准备事项：如需空腹检查请在前一天晚上10点后禁食禁水；如需憋尿检查请适量饮水。",
			notificationNo + "-03"
		);
		notifications.add(prepNotification);
		
		this.insertBatch(notifications);
		
		for (JiuzhentongzhiEntity notification : notifications) {
			sendNotification(notification);
		}
	}
	
	private JiuzhentongzhiEntity createNotification(YishengyuyueEntity yuyue, int type, 
			String title, String content, String notificationNo) {
		JiuzhentongzhiEntity notification = new JiuzhentongzhiEntity();
		notification.setTongzhibianhao(notificationNo);
		notification.setYishengzhanghao(yuyue.getYishengzhanghao());
		notification.setDianhua(yuyue.getDianhua());
		notification.setJiuzhenshijian(yuyue.getYuyueshijian());
		notification.setZhanghao(yuyue.getZhanghao());
		notification.setShouji(yuyue.getShouji());
		notification.setTongzhibeizhu(content);
		notification.setTongzhileixing(type);
		notification.setFasongzhuangtai(0);
		notification.setChongshicishu(0);
		notification.setTongzhishijian(new Date());
		notification.setYuyueid(yuyue.getId());
		notification.setAddtime(new Date());
		return notification;
	}
	
	@Override
	public boolean sendNotification(JiuzhentongzhiEntity notification) {
		try {
			System.out.println("正在发送通知: " + notification.getTongzhibianhao() + 
				" 类型: " + notification.getTongzhileixing() + 
				" 接收人: " + notification.getZhanghao());
			
			notification.setFasongzhuangtai(1);
			notification.setTongzhishijian(new Date());
			this.updateById(notification);
			
			return true;
		} catch (Exception e) {
			notification.setFasongzhuangtai(2);
			notification.setShibaiyuanyin(e.getMessage());
			notification.setChongshicishu(notification.getChongshicishu() + 1);
			notification.setXiacichongshishijian(calculateNextRetryTime(notification.getChongshicishu()));
			this.updateById(notification);
			
			return false;
		}
	}
	
	private Date calculateNextRetryTime(int retryCount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		
		switch (retryCount) {
			case 1:
				calendar.add(Calendar.MINUTE, 5);
				break;
			case 2:
				calendar.add(Calendar.MINUTE, 15);
				break;
			case 3:
				calendar.add(Calendar.MINUTE, 30);
				break;
			default:
				return null;
		}
		
		return calendar.getTime();
	}
	
	@Override
	public void retryFailedNotifications() {
		List<JiuzhentongzhiEntity> failedNotifications = getFailedNotifications();
		
		Date now = new Date();
		for (JiuzhentongzhiEntity notification : failedNotifications) {
			if (notification.getXiacichongshishijian() != null && 
				notification.getXiacichongshishijian().before(now) &&
				notification.getChongshicishu() < 3) {
				retryNotification(notification.getId());
			}
		}
	}
	
	@Override
	public boolean retryNotification(Long id) {
		JiuzhentongzhiEntity notification = this.selectById(id);
		if (notification != null && notification.getFasongzhuangtai() == 2) {
			return sendNotification(notification);
		}
		return false;
	}
	
	@Override
	public List<JiuzhentongzhiEntity> getFailedNotifications() {
		EntityWrapper<JiuzhentongzhiEntity> wrapper = new EntityWrapper<>();
		wrapper.eq("fasongzhuangtai", 2);
		wrapper.lt("chongshicishu", 3);
		return this.selectList(wrapper);
	}
	
	
	
}
