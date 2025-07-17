package com.example.video_interface.controller.h5;

import com.example.video_interface.dto.h5.H5OrderDTO;
import com.example.video_interface.model.Order;
import com.example.video_interface.service.h5.IH5OrderService;
import com.example.video_interface.service.h5.IH5UserService;
import com.example.video_interface.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * H5订单控制器
 * 处理H5端订单相关的请求，包括订单查询、创建、取消等
 */
@Slf4j
@RestController
@RequestMapping("/h5/order")
@RequiredArgsConstructor
public class H5OrderController {
    
    private final IH5OrderService h5OrderService;
    private final IH5UserService h5UserService;
    
    /**
     * 创建订单
     * @param orderData 订单数据
     * @return 创建结果
     */
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderData) {
        log.info("接收到创建订单请求: {}", orderData);
        
        try {
            User currentUser = h5UserService.getCurrentUser();
            
            // 从请求中提取订单信息
            String orderType = (String) orderData.get("orderType");
            Long productId = Long.valueOf(orderData.get("productId").toString());
            String productName = (String) orderData.get("productName");
            BigDecimal amount = new BigDecimal(orderData.get("amount").toString());
            String remark = (String) orderData.get("remark");
            
            // 创建订单
            Order order = h5OrderService.createOrder(
                orderType,
                productId,
                productName,
                amount,
                null, // 不设置支付方式，用户还没有选择
                currentUser.getId(),
                remark
            );
            
            log.info("订单创建成功，订单号: {}, 用户ID: {}, 订单类型: {}", 
                    order.getOrderNo(), currentUser.getId(), orderType);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "订单创建成功",
                "orderNo", order.getOrderNo(),
                "orderId", order.getId(),
                "outNo", order.getOutNo()
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("创建订单失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (IllegalStateException e) {
            log.warn("创建订单失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("创建订单发生错误: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "创建订单失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 获取用户订单列表
     * @param page 页码，从0开始
     * @param size 每页大小
     * @return 订单分页结果
     */
    @GetMapping("/list")
    public ResponseEntity<?> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            User currentUser = h5UserService.getCurrentUser();
            Pageable pageable = PageRequest.of(page, size);
            Page<H5OrderDTO> orders = h5OrderService.getUserOrders(currentUser.getId(), pageable);
            
            log.debug("获取用户订单列表成功，用户ID: {}, 订单数量: {}", currentUser.getId(), orders.getTotalElements());
            return ResponseEntity.ok(orders);
            
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.warn("获取用户订单列表失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("获取用户订单列表发生错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取订单列表失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 根据订单类型获取用户订单
     * @param orderType 订单类型
     * @param page 页码，从0开始
     * @param size 每页大小
     * @return 订单分页结果
     */
    @GetMapping("/type")
    public ResponseEntity<?> getUserOrdersByType(
            @RequestParam String orderType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            User currentUser = h5UserService.getCurrentUser();
            Pageable pageable = PageRequest.of(page, size);
            Page<H5OrderDTO> orders = h5OrderService.getUserOrdersByType(currentUser.getId(), orderType, pageable);
            
            log.debug("根据订单类型获取用户订单成功，用户ID: {}, 订单类型: {}, 订单数量: {}", 
                    currentUser.getId(), orderType, orders.getTotalElements());
            return ResponseEntity.ok(orders);
            
        } catch (IllegalArgumentException e) {
            log.warn("根据订单类型获取用户订单失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (IllegalStateException e) {
            log.warn("根据订单类型获取用户订单失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("根据订单类型获取用户订单发生错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取订单列表失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 根据订单状态获取用户订单
     * @param orderStatus 订单状态
     * @param page 页码，从0开始
     * @param size 每页大小
     * @return 订单分页结果
     */
    @GetMapping("/status")
    public ResponseEntity<?> getUserOrdersByStatus(
            @RequestParam String orderStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            User currentUser = h5UserService.getCurrentUser();
            Pageable pageable = PageRequest.of(page, size);
            Page<H5OrderDTO> orders = h5OrderService.getUserOrdersByStatus(currentUser.getId(), orderStatus, pageable);
            
            log.debug("根据订单状态获取用户订单成功，用户ID: {}, 订单状态: {}, 订单数量: {}", 
                    currentUser.getId(), orderStatus, orders.getTotalElements());
            return ResponseEntity.ok(orders);
            
        } catch (IllegalArgumentException e) {
            log.warn("根据订单状态获取用户订单失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (IllegalStateException e) {
            log.warn("根据订单状态获取用户订单失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("根据订单状态获取用户订单发生错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取订单列表失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 获取订单详情
     * @param orderNo 订单号
     * @return 订单详情
     */
    @GetMapping("/detail")
    public ResponseEntity<?> getOrderDetail(@RequestParam String orderNo) {
        
        try {
            User currentUser = h5UserService.getCurrentUser();
            H5OrderDTO order = h5OrderService.getOrderByOrderNo(orderNo, currentUser.getId());
            
            log.debug("获取订单详情成功，订单号: {}, 用户ID: {}", orderNo, currentUser.getId());
            return ResponseEntity.ok(order);
            
        } catch (IllegalArgumentException e) {
            log.warn("获取订单详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (IllegalStateException e) {
            log.warn("获取订单详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("获取订单详情发生错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取订单详情失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 更新订单支付方式
     * @param orderNo 订单号
     * @param paymentMethod 支付方式
     * @return 更新结果
     */
    @PostMapping("/update-payment")
    public ResponseEntity<?> updatePaymentMethod(
            @RequestParam String orderNo,
            @RequestParam String paymentMethod) {
        
        try {
            User currentUser = h5UserService.getCurrentUser();
            
            // 验证订单是否属于当前用户
            H5OrderDTO order = h5OrderService.getOrderByOrderNo(orderNo, currentUser.getId());
            
            // 更新支付方式
            Order updatedOrder = h5OrderService.updatePaymentMethod(orderNo, paymentMethod);
            
            log.info("订单支付方式更新成功，订单号: {}, 支付方式: {}, 用户ID: {}", 
                    orderNo, paymentMethod, currentUser.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "支付方式更新成功",
                "orderNo", orderNo,
                "paymentMethod", paymentMethod
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("更新订单支付方式失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("更新订单支付方式发生错误: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "更新支付方式失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 取消订单
     * @param orderNo 订单号
     * @return 取消结果
     */
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@RequestParam String orderNo) {
        
        try {
            User currentUser = h5UserService.getCurrentUser();
            boolean result = h5OrderService.cancelOrder(orderNo, currentUser.getId());
            
            log.info("订单取消成功，订单号: {}, 用户ID: {}", orderNo, currentUser.getId());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "订单取消成功"
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("订单取消失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (IllegalStateException e) {
            log.warn("订单取消失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("订单取消发生错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "订单取消失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 获取用户订单统计信息
     * @return 订单统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getOrderStatistics() {
        
        try {
            User currentUser = h5UserService.getCurrentUser();
            Map<String, Object> statistics = h5OrderService.getOrderStatistics(currentUser.getId());
            
            log.debug("获取用户订单统计成功，用户ID: {}", currentUser.getId());
            return ResponseEntity.ok(statistics);
            
        } catch (IllegalStateException e) {
            log.warn("获取用户订单统计失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("获取用户订单统计发生错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取订单统计失败，请稍后重试"
            ));
        }
    }
} 