package com.cl.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

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

	/**
	 * 通知类型常量
	 */
	public static final int TYPE_CONFIRM = 1;
	public static final int TYPE_REMIND = 2;
	public static final int TYPE_PREPARE = 3;
	public static final int TYPE_OTHER = 4;
	
	/**
	 * 发送状态常量
	 */
	public static final int STATUS_PENDING = 0;
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_FAILED = 2;
	
	/**
	 * 最大重试次数
	 */
	public static final int MAX_RETRY_COUNT = 3;
	
	/**
	 * 重试间隔（分钟）
	 */
	public static final int[] RETRY_INTERVALS = {5, 15, 30};
	
    	
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
	@Transactional
	public List<JiuzhentongzhiEntity> createAndSendNotifications(YishengyuyueEntity yuyue) {
		if (yuyue == null) {
			throw new IllegalArgumentException("预约信息不能为空");
		}
		
		List<JiuzhentongzhiEntity> notifications = new ArrayList<>();
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		// 1. 创建预约确认通知
		JiuzhentongzhiEntity confirmNotify = new JiuzhentongzhiEntity();
		confirmNotify.setTongzhibianhao("TZ" + sdf.format(now) + "01");
		confirmNotify.setYishengzhanghao(yuyue.getYishengzhanghao());
		confirmNotify.setDianhua(yuyue.getDianhua());
		confirmNotify.setZhanghao(yuyue.getZhanghao());
		confirmNotify.setShouji(yuyue.getShouji());
		confirmNotify.setJiuzhenshijian(yuyue.getYuyueshijian());
		confirmNotify.setTongzhishijian(now);
		confirmNotify.setTongzhibeizhu("您的预约已成功确认，预约时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(yuyue.getYuyueshijian()) + "，请按时就诊。");
		confirmNotify.setTongzhileixing(TYPE_CONFIRM);
		confirmNotify.setFasongzhuangtai(STATUS_PENDING);
		confirmNotify.setChongshicishu(0);
		confirmNotify.setGuanlianyuyueid(yuyue.getId());
		confirmNotify.setAddtime(now);
		
		// 2. 创建就诊前提醒通知（提前1天）
		JiuzhentongzhiEntity remindNotify = new JiuzhentongzhiEntity();
		remindNotify.setTongzhibianhao("TZ" + sdf.format(now) + "02");
		remindNotify.setYishengzhanghao(yuyue.getYishengzhanghao());
		remindNotify.setDianhua(yuyue.getDianhua());
		remindNotify.setZhanghao(yuyue.getZhanghao());
		remindNotify.setShouji(yuyue.getShouji());
		remindNotify.setJiuzhenshijian(yuyue.getYuyueshijian());
		remindNotify.setTongzhishijian(getBeforeDay(yuyue.getYuyueshijian(), 1));
		remindNotify.setTongzhibeizhu("温馨提醒：您明天有预约就诊，时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(yuyue.getYuyueshijian()) + "，请提前做好准备。");
		remindNotify.setTongzhileixing(TYPE_REMIND);
		remindNotify.setFasongzhuangtai(STATUS_PENDING);
		remindNotify.setChongshicishu(0);
		remindNotify.setGuanlianyuyueid(yuyue.getId());
		remindNotify.setAddtime(now);
		
		// 3. 创建检查准备事项通知（提前2小时）
		JiuzhentongzhiEntity prepareNotify = new JiuzhentongzhiEntity();
		prepareNotify.setTongzhibianhao("TZ" + sdf.format(now) + "03");
		prepareNotify.setYishengzhanghao(yuyue.getYishengzhanghao());
		prepareNotify.setDianhua(yuyue.getDianhua());
		prepareNotify.setZhanghao(yuyue.getZhanghao());
		prepareNotify.setShouji(yuyue.getShouji());
		prepareNotify.setJiuzhenshijian(yuyue.getYuyueshijian());
		prepareNotify.setTongzhishijian(getBeforeHour(yuyue.getYuyueshijian(), 2));
		prepareNotify.setTongzhibeizhu("就诊准备：请携带身份证、医保卡、既往病历等相关资料，提前30分钟到达医院。如有特殊情况请提前联系。");
		prepareNotify.setTongzhileixing(TYPE_PREPARE);
		prepareNotify.setFasongzhuangtai(STATUS_PENDING);
		prepareNotify.setChongshicishu(0);
		prepareNotify.setGuanlianyuyueid(yuyue.getId());
		prepareNotify.setAddtime(now);
		
		// 保存到数据库
		this.insert(confirmNotify);
		this.insert(remindNotify);
		this.insert(prepareNotify);
		
		notifications.add(confirmNotify);
		notifications.add(remindNotify);
		notifications.add(prepareNotify);
		
		// 立即发送所有通知（一次性发送模式）
		for (JiuzhentongzhiEntity notification : notifications) {
			sendNotification(notification);
		}
		
		return notifications;
	}
	
	@Override
	public boolean sendNotification(JiuzhentongzhiEntity tongzhi) {
		if (tongzhi == null) {
			return false;
		}
		
		try {
			// 模拟发送通知（实际项目中这里调用短信/邮件/推送接口）
			boolean success = doSend(tongzhi);
			
			Date now = new Date();
			
			// 获取当前重试次数，防止null
			Integer currentRetryCount = tongzhi.getChongshicishu();
			if (currentRetryCount == null) {
				currentRetryCount = 0;
			}
			
			if (success) {
				tongzhi.setFasongzhuangtai(STATUS_SUCCESS);
				tongzhi.setFasongshijian(now);
				tongzhi.setShibaiyuanyin(null);
			} else {
				tongzhi.setFasongzhuangtai(STATUS_FAILED);
				tongzhi.setShibaiyuanyin("发送失败：网络异常或服务不可用");
				tongzhi.setChongshicishu(currentRetryCount + 1);
				
				// 计算下次重试时间
				int newRetryCount = currentRetryCount + 1;
				if (newRetryCount <= MAX_RETRY_COUNT) {
					int interval = RETRY_INTERVALS[Math.min(newRetryCount - 1, RETRY_INTERVALS.length - 1)];
					tongzhi.setXiacichongshishijian(getAfterMinute(now, interval));
				}
			}
			
			this.updateById(tongzhi);
			return success;
			
		} catch (Exception e) {
			// 发送异常，记录失败
			Integer currentRetryCount = tongzhi.getChongshicishu();
			if (currentRetryCount == null) {
				currentRetryCount = 0;
			}
			
			tongzhi.setFasongzhuangtai(STATUS_FAILED);
			tongzhi.setShibaiyuanyin("发送异常：" + e.getMessage());
			tongzhi.setChongshicishu(currentRetryCount + 1);
			
			int newRetryCount = currentRetryCount + 1;
			if (newRetryCount <= MAX_RETRY_COUNT) {
				int interval = RETRY_INTERVALS[Math.min(newRetryCount - 1, RETRY_INTERVALS.length - 1)];
				tongzhi.setXiacichongshishijian(getAfterMinute(new Date(), interval));
			}
			
			this.updateById(tongzhi);
			return false;
		}
	}
	
	/**
	 * 实际发送逻辑（模拟）
	 */
	private boolean doSend(JiuzhentongzhiEntity tongzhi) {
		// 实际项目中这里调用短信网关、邮件服务或推送服务
		// 返回true表示发送成功，false表示发送失败
		// 这里模拟随机成功/失败用于测试
		// return Math.random() > 0.3; // 70%成功率
		
		// 实际环境应该返回true，表示发送成功
		return true;
	}
	
	@Override
	public boolean resendNotification(Long id) {
		if (id == null || id <= 0) {
			return false;
		}
		
		JiuzhentongzhiEntity tongzhi = this.selectById(id);
		if (tongzhi == null) {
			return false;
		}
		
		// 重置状态为待发送
		tongzhi.setFasongzhuangtai(STATUS_PENDING);
		tongzhi.setShibaiyuanyin(null);
		
		return sendNotification(tongzhi);
	}
	
	@Override
	public List<JiuzhentongzhiEntity> getFailedNotificationsForRetry() {
		EntityWrapper<JiuzhentongzhiEntity> wrapper = new EntityWrapper<>();
		wrapper.eq("fasongzhuangtai", STATUS_FAILED);
		wrapper.le("chongshicishu", MAX_RETRY_COUNT);
		wrapper.le("xiacichongshishijian", new Date());
		wrapper.orderBy("xiacichongshishijian", true);
		
		return this.selectList(wrapper);
	}
	
	@Override
	public void batchRetryFailedNotifications() {
		List<JiuzhentongzhiEntity> failedList = getFailedNotificationsForRetry();
		for (JiuzhentongzhiEntity tongzhi : failedList) {
			sendNotification(tongzhi);
		}
	}
	
	@Override
	public void manualUpdateStatus(Long id, Integer status, String remark) {
		if (id == null || id <= 0) {
			return;
		}
		
		JiuzhentongzhiEntity tongzhi = this.selectById(id);
		if (tongzhi != null) {
			tongzhi.setFasongzhuangtai(status);
			if (remark != null && !remark.isEmpty()) {
				tongzhi.setShibaiyuanyin(remark);
			}
			this.updateById(tongzhi);
		}
	}
	
	@Override
	public List<Map<String, Object>> selectValue(Map<String, Object> params, Wrapper<JiuzhentongzhiEntity> wrapper) {
		return baseMapper.selectValue(params, wrapper);
	}

	@Override
	public List<Map<String, Object>> selectTimeStatValue(Map<String, Object> params, Wrapper<JiuzhentongzhiEntity> wrapper) {
		return baseMapper.selectTimeStatValue(params, wrapper);
	}

	@Override
	public List<Map<String, Object>> selectGroup(Map<String, Object> params, Wrapper<JiuzhentongzhiEntity> wrapper) {
		return baseMapper.selectGroup(params, wrapper);
	}
	
	/**
	 * 获取指定天数前的时间
	 */
	private Date getBeforeDay(Date date, int days) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -days);
		return calendar.getTime();
	}
	
	/**
	 * 获取指定小时前的时间
	 */
	private Date getBeforeHour(Date date, int hours) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, -hours);
		return calendar.getTime();
	}
	
	/**
	 * 获取指定分钟后的时间
	 */
	private Date getAfterMinute(Date date, int minutes) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}


}
