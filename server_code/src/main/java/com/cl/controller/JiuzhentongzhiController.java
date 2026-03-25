package com.cl.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import com.cl.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.annotation.SysLog;

import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.entity.view.JiuzhentongzhiView;

import com.cl.service.JiuzhentongzhiService;
import com.cl.service.TokenService;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import com.cl.utils.MPUtil;
import com.cl.utils.MapUtils;
import com.cl.utils.CommonUtil;

/**
 * 就诊通知
 * 后端接口
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
@RestController
@RequestMapping("/jiuzhentongzhi")
public class JiuzhentongzhiController {
    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;










    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,JiuzhentongzhiEntity jiuzhentongzhi,
                                                                                                                                                    HttpServletRequest request){
                    String tableName = request.getSession().getAttribute("tableName").toString();
                                                                        if(tableName.equals("yisheng")) {
                    jiuzhentongzhi.setYishengzhanghao((String)request.getSession().getAttribute("username"));
                                    }
                                                                                                                                                                    if(tableName.equals("yonghu")) {
                    jiuzhentongzhi.setZhanghao((String)request.getSession().getAttribute("username"));
                                    }
                                                                                                                        EntityWrapper<JiuzhentongzhiEntity> ew = new EntityWrapper<JiuzhentongzhiEntity>();
                                                                                                                                                                                                        
        
        
        PageUtils page = jiuzhentongzhiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jiuzhentongzhi), params), params));
        return R.ok().put("data", page);
    }









    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,JiuzhentongzhiEntity jiuzhentongzhi,
		HttpServletRequest request){
        EntityWrapper<JiuzhentongzhiEntity> ew = new EntityWrapper<JiuzhentongzhiEntity>();

		PageUtils page = jiuzhentongzhiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jiuzhentongzhi), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( JiuzhentongzhiEntity jiuzhentongzhi){
       	EntityWrapper<JiuzhentongzhiEntity> ew = new EntityWrapper<JiuzhentongzhiEntity>();
      	ew.allEq(MPUtil.allEQMapPre( jiuzhentongzhi, "jiuzhentongzhi")); 
        return R.ok().put("data", jiuzhentongzhiService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(JiuzhentongzhiEntity jiuzhentongzhi){
        EntityWrapper< JiuzhentongzhiEntity> ew = new EntityWrapper< JiuzhentongzhiEntity>();
 		ew.allEq(MPUtil.allEQMapPre( jiuzhentongzhi, "jiuzhentongzhi")); 
		JiuzhentongzhiView jiuzhentongzhiView =  jiuzhentongzhiService.selectView(ew);
		return R.ok("查询就诊通知成功").put("data", jiuzhentongzhiView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        JiuzhentongzhiEntity jiuzhentongzhi = jiuzhentongzhiService.selectById(id);
		jiuzhentongzhi = jiuzhentongzhiService.selectView(new EntityWrapper<JiuzhentongzhiEntity>().eq("id", id));
        return R.ok().put("data", jiuzhentongzhi);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        JiuzhentongzhiEntity jiuzhentongzhi = jiuzhentongzhiService.selectById(id);
		jiuzhentongzhi = jiuzhentongzhiService.selectView(new EntityWrapper<JiuzhentongzhiEntity>().eq("id", id));
        return R.ok().put("data", jiuzhentongzhi);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    @SysLog("新增就诊通知")
    public R save(@RequestBody JiuzhentongzhiEntity jiuzhentongzhi, HttpServletRequest request){
    	//ValidatorUtils.validateEntity(jiuzhentongzhi);
        jiuzhentongzhiService.insert(jiuzhentongzhi);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @SysLog("新增就诊通知")
    @RequestMapping("/add")
    public R add(@RequestBody JiuzhentongzhiEntity jiuzhentongzhi, HttpServletRequest request){
    	//ValidatorUtils.validateEntity(jiuzhentongzhi);
        jiuzhentongzhiService.insert(jiuzhentongzhi);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    @SysLog("修改就诊通知")
    public R update(@RequestBody JiuzhentongzhiEntity jiuzhentongzhi, HttpServletRequest request){
        //ValidatorUtils.validateEntity(jiuzhentongzhi);
        jiuzhentongzhiService.updateById(jiuzhentongzhi);//全部更新
        return R.ok();
    }



    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @SysLog("删除就诊通知")
    public R delete(@RequestBody Long[] ids){
        jiuzhentongzhiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	






    /**
     * （按值统计）
     */
    @RequestMapping("/value/{xColumnName}/{yColumnName}")
    public R value(@PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName,HttpServletRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("xColumn", MPUtil.camelToSnake(xColumnName));
        params.put("yColumn", MPUtil.camelToSnake(yColumnName));
        EntityWrapper<JiuzhentongzhiEntity> ew = new EntityWrapper<JiuzhentongzhiEntity>();
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("yisheng")) {
            ew.eq("yishengzhanghao", (String)request.getSession().getAttribute("username"));
		}
		if(tableName.equals("yonghu")) {
            ew.eq("zhanghao", (String)request.getSession().getAttribute("username"));
		}
        List<Map<String, Object>> result = MPUtil.snakeListToCamel(jiuzhentongzhiService.selectValue(params, ew));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(Map<String, Object> m : result) {
            for(String k : m.keySet()) {
                if(m.get(k) instanceof Date) {
                    m.put(k, sdf.format((Date)m.get(k)));
                }
            }
        }

        Collections.sort(result, (map1, map2) -> {
            Number total1 = (Number) map1.get("total");
            Number total2 = (Number) map2.get("total");
            if(total1==null)
            {
                total1 = 0;
            }
            if(total2==null)
            {
                total2 = 0;
            }
            String order = request.getParameter("order");
            if(StringUtils.isNotBlank(order)&&order.equals("desc")){
                return Double.compare(total2.doubleValue(), total1.doubleValue());
            }
            return Double.compare(total1.doubleValue(), total2.doubleValue());
        });

        return R.ok().put("data", result);
    }

    /**
     * （按值统计(多)）
     */
    @RequestMapping("/valueMul/{xColumnName}")
    public R valueMul(@PathVariable("xColumnName") String xColumnName,@RequestParam String yColumnNameMul, HttpServletRequest request) {
        String[] yColumnNames = MPUtil.camelToSnake(yColumnNameMul).split(",");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("xColumn", MPUtil.camelToSnake(xColumnName));
        List<List<Map<String, Object>>> result2 = new ArrayList<List<Map<String,Object>>>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        EntityWrapper<JiuzhentongzhiEntity> ew = new EntityWrapper<JiuzhentongzhiEntity>();
        String tableName = request.getSession().getAttribute("tableName").toString();
        if(tableName.equals("yisheng")) {
            ew.eq("yishengzhanghao", (String)request.getSession().getAttribute("username"));
        }
        if(tableName.equals("yonghu")) {
            ew.eq("zhanghao", (String)request.getSession().getAttribute("username"));
        }
        for(int i=0;i<yColumnNames.length;i++) {
            params.put("yColumn", yColumnNames[i]);
            List<Map<String, Object>> result = MPUtil.snakeListToCamel(jiuzhentongzhiService.selectValue(params, ew));
            for(Map<String, Object> m : result) {
                for(String k : m.keySet()) {
                    if(m.get(k) instanceof Date) {
                        m.put(k, sdf.format((Date)m.get(k)));
                    }
                }
            }
            result2.add(result);
        }
        return R.ok().put("data", result2);
    }

    /**
     * （按值统计）时间统计类型
     */
    @RequestMapping("/value/{xColumnName}/{yColumnName}/{timeStatType}")
    public R valueDay(@PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName, @PathVariable("timeStatType") String timeStatType,HttpServletRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("xColumn", MPUtil.camelToSnake(xColumnName));
        params.put("yColumn", MPUtil.camelToSnake(yColumnName));
        params.put("timeStatType", timeStatType);
        EntityWrapper<JiuzhentongzhiEntity> ew = new EntityWrapper<JiuzhentongzhiEntity>();
        String tableName = request.getSession().getAttribute("tableName").toString();
        if(tableName.equals("yisheng")) {
            ew.eq("yishengzhanghao", (String)request.getSession().getAttribute("username"));
        }
        if(tableName.equals("yonghu")) {
            ew.eq("zhanghao", (String)request.getSession().getAttribute("username"));
        }
        List<Map<String, Object>> result = MPUtil.snakeListToCamel(jiuzhentongzhiService.selectTimeStatValue(params, ew));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(Map<String, Object> m : result) {
            for(String k : m.keySet()) {
                if(m.get(k) instanceof Date) {
                    m.put(k, sdf.format((Date)m.get(k)));
                }
            }
        }
        return R.ok().put("data", result);
    }

    /**
     * （按值统计）时间统计类型(多)
     */
    @RequestMapping("/valueMul/{xColumnName}/{timeStatType}")
    public R valueMulDay(@PathVariable("xColumnName") String xColumnName, @PathVariable("timeStatType") String timeStatType,@RequestParam String yColumnNameMul,HttpServletRequest request) {
        String[] yColumnNames = MPUtil.camelToSnake(yColumnNameMul).split(",");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("xColumn", xColumnName);
        params.put("timeStatType", timeStatType);
        List<List<Map<String, Object>>> result2 = new ArrayList<List<Map<String,Object>>>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        EntityWrapper<JiuzhentongzhiEntity> ew = new EntityWrapper<JiuzhentongzhiEntity>();
        String tableName = request.getSession().getAttribute("tableName").toString();
        if(tableName.equals("yisheng")) {
            ew.eq("yishengzhanghao", (String)request.getSession().getAttribute("username"));
        }
        if(tableName.equals("yonghu")) {
            ew.eq("zhanghao", (String)request.getSession().getAttribute("username"));
        }
        for(int i=0;i<yColumnNames.length;i++) {
            params.put("yColumn", yColumnNames[i]);
            List<Map<String, Object>> result = MPUtil.snakeListToCamel(jiuzhentongzhiService.selectTimeStatValue(params, ew));
            for(Map<String, Object> m : result) {
                for(String k : m.keySet()) {
                    if(m.get(k) instanceof Date) {
                        m.put(k, sdf.format((Date)m.get(k)));
                    }
                }
            }
            result2.add(result);
        }
        return R.ok().put("data", result2);
    }

    /**
     * 分组统计
     */
    @RequestMapping("/group/{columnName}")
    public R group(@PathVariable("columnName") String columnName,HttpServletRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("column", MPUtil.camelToSnake(columnName));
        EntityWrapper<JiuzhentongzhiEntity> ew = new EntityWrapper<JiuzhentongzhiEntity>();
        String tableName = request.getSession().getAttribute("tableName").toString();
        if(tableName.equals("yisheng")) {
            ew.eq("yishengzhanghao", (String)request.getSession().getAttribute("username"));
        }
        if(tableName.equals("yonghu")) {
            ew.eq("zhanghao", (String)request.getSession().getAttribute("username"));
        }
        List<Map<String, Object>> result = MPUtil.snakeListToCamel(jiuzhentongzhiService.selectGroup(params, ew));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(Map<String, Object> m : result) {
            for(String k : m.keySet()) {
                if(m.get(k) instanceof Date) {
                    m.put(k, sdf.format((Date)m.get(k)));
                }
            }
        }
        return R.ok().put("data", result);
    }




    /**
     * 总数量
     */
    @RequestMapping("/count")
    public R count(@RequestParam Map<String, Object> params,JiuzhentongzhiEntity jiuzhentongzhi, HttpServletRequest request){
        String tableName = request.getSession().getAttribute("tableName").toString();
        if(tableName.equals("yisheng")) {
            jiuzhentongzhi.setYishengzhanghao((String)request.getSession().getAttribute("username"));
        }
        if(tableName.equals("yonghu")) {
            jiuzhentongzhi.setZhanghao((String)request.getSession().getAttribute("username"));
        }
        EntityWrapper<JiuzhentongzhiEntity> ew = new EntityWrapper<JiuzhentongzhiEntity>();
        int count = jiuzhentongzhiService.selectCount(MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jiuzhentongzhi), params), params));
        return R.ok().put("data", count);
    }



    /**
     * ==================== 管理员后台增强功能 ====================
     */
    
    /**
     * 手动重发通知
     */
    @RequestMapping("/resend/{id}")
    @SysLog("手动重发通知")
    public R resend(@PathVariable("id") Long id){
        boolean success = jiuzhentongzhiService.resendNotification(id);
        if(success) {
            return R.ok("通知重发成功");
        } else {
            return R.error("通知重发失败");
        }
    }
    
    /**
     * 批量重发失败通知
     */
    @RequestMapping("/batchResend")
    @SysLog("批量重发失败通知")
    public R batchResend(@RequestBody Long[] ids){
        int successCount = 0;
        int failCount = 0;
        for(Long id : ids) {
            boolean success = jiuzhentongzhiService.resendNotification(id);
            if(success) {
                successCount++;
            } else {
                failCount++;
            }
        }
        return R.ok("批量重发完成：成功" + successCount + "条，失败" + failCount + "条");
    }
    
    /**
     * 手动更新通知状态
     */
    @RequestMapping("/updateStatus")
    @SysLog("手动更新通知状态")
    public R updateStatus(@RequestParam Long id, @RequestParam Integer status, @RequestParam(required = false) String remark){
        jiuzhentongzhiService.manualUpdateStatus(id, status, remark);
        return R.ok("状态更新成功");
    }
    
    /**
     * 获取失败通知列表（用于管理员查看）
     */
    @RequestMapping("/failedList")
    public R failedList(@RequestParam Map<String, Object> params, HttpServletRequest request){
        EntityWrapper<JiuzhentongzhiEntity> ew = new EntityWrapper<JiuzhentongzhiEntity>();
        ew.eq("fasongzhuangtai", 2); // 发送失败状态
        
        // 按时间范围筛选
        if(params.get("startTime") != null && !"".equals(params.get("startTime"))) {
            ew.ge("addtime", params.get("startTime"));
        }
        if(params.get("endTime") != null && !"".equals(params.get("endTime"))) {
            ew.le("addtime", params.get("endTime"));
        }
        
        // 按用户账号筛选
        if(params.get("zhanghao") != null && !"".equals(params.get("zhanghao"))) {
            ew.like("zhanghao", (String)params.get("zhanghao"));
        }
        
        ew.orderBy("addtime", false);
        
        PageUtils page = jiuzhentongzhiService.queryPage(params, ew);
        return R.ok().put("data", page);
    }
    
    /**
     * 获取通知统计信息
     */
    @RequestMapping("/statistics")
    public R statistics(HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        
        // 统计各状态数量
        EntityWrapper<JiuzhentongzhiEntity> pendingWrapper = new EntityWrapper<>();
        pendingWrapper.eq("fasongzhuangtai", 0);
        int pendingCount = jiuzhentongzhiService.selectCount(pendingWrapper);
        
        EntityWrapper<JiuzhentongzhiEntity> successWrapper = new EntityWrapper<>();
        successWrapper.eq("fasongzhuangtai", 1);
        int successCount = jiuzhentongzhiService.selectCount(successWrapper);
        
        EntityWrapper<JiuzhentongzhiEntity> failedWrapper = new EntityWrapper<>();
        failedWrapper.eq("fasongzhuangtai", 2);
        int failedCount = jiuzhentongzhiService.selectCount(failedWrapper);
        
        result.put("pendingCount", pendingCount);
        result.put("successCount", successCount);
        result.put("failedCount", failedCount);
        result.put("totalCount", pendingCount + successCount + failedCount);
        
        return R.ok().put("data", result);
    }

}
