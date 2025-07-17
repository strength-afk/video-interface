package com.example.video_interface.service.admin;

import com.example.video_interface.dto.admin.AdminOrderDTO;
import com.example.video_interface.dto.admin.AdminOrderRequest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 管理员订单管理服务接口
 * 提供订单查询、统计等管理功能
 */
public interface IAdminOrderService {
    /**
     * 分页查询订单列表
     * @param request 查询请求，包含分页、筛选、排序参数
     * @return 分页订单列表
     */
    Page<AdminOrderDTO> getOrderList(AdminOrderRequest request);

    /**
     * 根据订单ID获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     * @throws IllegalArgumentException 如果订单不存在
     */
    AdminOrderDTO getOrderById(Long orderId);

    /**
     * 根据订单号获取订单详情
     * @param orderNo 订单号
     * @return 订单详情
     * @throws IllegalArgumentException 如果订单不存在
     */
    AdminOrderDTO getOrderByOrderNo(String orderNo);

    /**
     * 取消订单
     * @param orderId 订单ID
     * @param reason 取消原因
     * @return 取消后的订单信息
     * @throws IllegalArgumentException 如果订单不存在或无法取消
     */
    AdminOrderDTO cancelOrder(Long orderId, String reason);

    /**
     * 退款订单
     * @param orderId 订单ID
     * @param refundAmount 退款金额
     * @param reason 退款原因
     * @return 退款后的订单信息
     * @throws IllegalArgumentException 如果订单不存在或无法退款
     */
    AdminOrderDTO refundOrder(Long orderId, BigDecimal refundAmount, String reason);

    /**
     * 获取订单统计信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单统计信息，包含总订单数、总金额、各状态订单数等
     */
    Map<String, Object> getOrderStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取今日订单统计
     * @return 今日订单统计信息
     */
    Map<String, Object> getTodayOrderStatistics();

    /**
     * 获取本周订单统计
     * @return 本周订单统计信息
     */
    Map<String, Object> getWeekOrderStatistics();

    /**
     * 获取本月订单统计
     * @return 本月订单统计信息
     */
    Map<String, Object> getMonthOrderStatistics();

    /**
     * 获取按日期分组的订单统计
     * @param days 天数（7、30、90）
     * @return 按日期分组的订单统计数据
     */
    Map<String, Object> getDailyOrderStatistics(int days);

    /**
     * 导出订单数据
     * @param request 查询请求
     * @return 订单数据列表
     */
    java.util.List<AdminOrderDTO> exportOrders(AdminOrderRequest request);
} 