package com.example.video_interface.controller.admin;

import com.example.video_interface.dto.admin.AdminOrderDTO;
import com.example.video_interface.dto.admin.AdminOrderRequest;
import com.example.video_interface.service.admin.IAdminOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理后台订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final IAdminOrderService adminOrderService;

    /**
     * 分页查询订单列表
     * @param request 查询请求
     * @return 分页订单列表
     */
    @PostMapping("/list")
    public ResponseEntity<?> getOrderList(@RequestBody AdminOrderRequest request) {
        try {
            Page<AdminOrderDTO> orderPage = adminOrderService.getOrderList(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", orderPage.getContent());
            response.put("totalElements", orderPage.getTotalElements());
            response.put("totalPages", orderPage.getTotalPages());
            response.put("currentPage", orderPage.getNumber() + 1);
            response.put("size", orderPage.getSize());
            
            log.debug("获取订单列表成功，共{}个订单", orderPage.getTotalElements());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", response
            ));
        } catch (Exception e) {
            log.error("获取订单列表失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取订单列表失败，请稍后重试"
            ));
        }
    }

    /**
     * 获取订单详情
     * @param requestBody 包含id的请求体
     * @return 订单详情
     */
    @PostMapping("/detail")
    public ResponseEntity<?> getOrderById(@RequestBody Map<String, Object> requestBody) {
        try {
            Long orderId = Long.valueOf(requestBody.get("id").toString());
            AdminOrderDTO order = adminOrderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "订单不存在"
                ));
            }
            log.debug("获取订单详情成功: {}", order.getOrderNo());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", order
            ));
        } catch (Exception e) {
            log.error("获取订单详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取订单详情失败，请稍后重试"
            ));
        }
    }

    /**
     * 根据订单号获取订单详情
     * @param requestBody 包含orderNo的请求体
     * @return 订单详情
     */
    @PostMapping("/detail-by-order-no")
    public ResponseEntity<?> getOrderByOrderNo(@RequestBody Map<String, Object> requestBody) {
        try {
            String orderNo = requestBody.get("orderNo").toString();
            AdminOrderDTO order = adminOrderService.getOrderByOrderNo(orderNo);
            if (order == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "订单不存在"
                ));
            }
            log.debug("根据订单号获取订单详情成功: {}", orderNo);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", order
            ));
        } catch (Exception e) {
            log.error("根据订单号获取订单详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取订单详情失败，请稍后重试"
            ));
        }
    }

    /**
     * 取消订单
     * @param requestBody 包含id和reason的请求体
     * @return 操作结果
     */
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@RequestBody Map<String, Object> requestBody) {
        try {
            Long orderId = Long.valueOf(requestBody.get("id").toString());
            String reason = requestBody.get("reason").toString();
            
            AdminOrderDTO order = adminOrderService.cancelOrder(orderId, reason);
            log.debug("取消订单成功: {}", order.getOrderNo());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", order,
                "message", "取消成功"
            ));
        } catch (Exception e) {
            log.error("取消订单失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "取消订单失败：" + e.getMessage()
            ));
        }
    }

    /**
     * 退款订单
     * @param requestBody 包含id、refundAmount和reason的请求体
     * @return 操作结果
     */
    @PostMapping("/refund")
    public ResponseEntity<?> refundOrder(@RequestBody Map<String, Object> requestBody) {
        try {
            Long orderId = Long.valueOf(requestBody.get("id").toString());
            BigDecimal refundAmount = new BigDecimal(requestBody.get("refundAmount").toString());
            String reason = requestBody.get("reason").toString();
            
            AdminOrderDTO order = adminOrderService.refundOrder(orderId, refundAmount, reason);
            log.debug("退款订单成功: {}", order.getOrderNo());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", order,
                "message", "退款成功"
            ));
        } catch (Exception e) {
            log.error("退款订单失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "退款订单失败：" + e.getMessage()
            ));
        }
    }

    /**
     * 获取订单统计信息
     * @param requestBody 包含startTime和endTime的请求体
     * @return 订单统计信息
     */
    @PostMapping("/statistics")
    public ResponseEntity<?> getOrderStatistics(@RequestBody Map<String, Object> requestBody) {
        try {
            String startTimeStr = requestBody.get("startTime").toString();
            String endTimeStr = requestBody.get("endTime").toString();
            
            java.time.LocalDateTime startTime = java.time.LocalDateTime.parse(startTimeStr);
            java.time.LocalDateTime endTime = java.time.LocalDateTime.parse(endTimeStr);
            
            Map<String, Object> statistics = adminOrderService.getOrderStatistics(startTime, endTime);
            log.debug("获取订单统计成功");
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", statistics
            ));
        } catch (Exception e) {
            log.error("获取订单统计失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取订单统计失败，请稍后重试"
            ));
        }
    }

    /**
     * 获取今日订单统计
     * @return 今日订单统计信息
     */
    @PostMapping("/statistics/today")
    public ResponseEntity<?> getTodayOrderStatistics() {
        try {
            Map<String, Object> statistics = adminOrderService.getTodayOrderStatistics();
            log.debug("获取今日订单统计成功");
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", statistics
            ));
        } catch (Exception e) {
            log.error("获取今日订单统计失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取今日订单统计失败，请稍后重试"
            ));
        }
    }

    /**
     * 获取本周订单统计
     * @return 本周订单统计信息
     */
    @PostMapping("/statistics/week")
    public ResponseEntity<?> getWeekOrderStatistics() {
        try {
            Map<String, Object> statistics = adminOrderService.getWeekOrderStatistics();
            log.debug("获取本周订单统计成功");
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", statistics
            ));
        } catch (Exception e) {
            log.error("获取本周订单统计失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取本周订单统计失败，请稍后重试"
            ));
        }
    }

    /**
     * 获取本月订单统计
     * @return 本月订单统计信息
     */
    @PostMapping("/statistics/month")
    public ResponseEntity<?> getMonthOrderStatistics() {
        try {
            Map<String, Object> statistics = adminOrderService.getMonthOrderStatistics();
            log.debug("获取本月订单统计成功");
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", statistics
            ));
        } catch (Exception e) {
            log.error("获取本月订单统计失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取本月订单统计失败，请稍后重试"
            ));
        }
    }

    /**
     * 获取按日期分组的订单统计
     * @param requestBody 包含days的请求体
     * @return 按日期分组的订单统计信息
     */
    @PostMapping("/statistics/daily")
    public ResponseEntity<?> getDailyOrderStatistics(@RequestBody Map<String, Object> requestBody) {
        try {
            Integer days = Integer.valueOf(requestBody.get("days").toString());
            Map<String, Object> statistics = adminOrderService.getDailyOrderStatistics(days);
            log.debug("获取按日期分组的订单统计成功，天数: {}", days);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", statistics
            ));
        } catch (Exception e) {
            log.error("获取按日期分组的订单统计失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取按日期分组的订单统计失败，请稍后重试"
            ));
        }
    }

    /**
     * 导出订单数据
     * @param request 查询请求
     * @return 订单数据列表
     */
    @PostMapping("/export")
    public ResponseEntity<?> exportOrders(@RequestBody AdminOrderRequest request) {
        try {
            List<AdminOrderDTO> orders = adminOrderService.exportOrders(request);
            log.debug("导出订单数据成功，共{}个订单", orders.size());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "导出成功",
                "data", orders
            ));
        } catch (Exception e) {
            log.error("导出订单数据失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "导出订单数据失败，请稍后重试"
            ));
        }
    }
} 