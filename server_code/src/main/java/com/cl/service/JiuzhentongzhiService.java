package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.utils.PageUtils;
import com.cl.entity.JiuzhentongzhiEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.cl.entity.view.JiuzhentongzhiView;
import com.cl.entity.YishengyuyueEntity;


/**
 * 就诊通知
 *
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
public interface JiuzhentongzhiService extends IService<JiuzhentongzhiEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<JiuzhentongzhiView> selectListView(Wrapper<JiuzhentongzhiEntity> wrapper);
   	
   	JiuzhentongzhiView selectView(@Param("ew") Wrapper<JiuzhentongzhiEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<JiuzhentongzhiEntity> wrapper);
   	
   	/**
   	 * 根据预约信息创建并发送所有就诊通知（一次性发送）
   	 * @param yuyue 预约信息
   	 * @return 创建的通知列表
   	 */
   	List<JiuzhentongzhiEntity> createAndSendNotifications(YishengyuyueEntity yuyue);
   	
   	/**
   	 * 发送通知（实际发送逻辑）
   	 * @param tongzhi 通知实体
   	 * @return 是否发送成功
   	 */
   	boolean sendNotification(JiuzhentongzhiEntity tongzhi);
   	
   	/**
   	 * 重新发送失败的通知
   	 * @param id 通知ID
   	 * @return 是否发送成功
   	 */
   	boolean resendNotification(Long id);
   	
   	/**
   	 * 获取需要重试的失败通知列表
   	 * @return 通知列表
   	 */
   	List<JiuzhentongzhiEntity> getFailedNotificationsForRetry();
   	
   	/**
   	 * 批量重试失败通知
   	 */
   	void batchRetryFailedNotifications();
   	
   	/**
	 * 手动标记通知状态
	 * @param id 通知ID
	 * @param status 状态
	 * @param remark 备注
	 */
	void manualUpdateStatus(Long id, Integer status, String remark);
	
	/**
	 * 按值统计
	 */
	List<Map<String, Object>> selectValue(Map<String, Object> params, Wrapper<JiuzhentongzhiEntity> wrapper);

	/**
	 * 按时间统计
	 */
	List<Map<String, Object>> selectTimeStatValue(Map<String, Object> params, Wrapper<JiuzhentongzhiEntity> wrapper);

	/**
	 * 分组统计
	 */
	List<Map<String, Object>> selectGroup(Map<String, Object> params, Wrapper<JiuzhentongzhiEntity> wrapper);
	
}
