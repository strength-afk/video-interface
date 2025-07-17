package com.example.video_interface.service.h5;

import com.example.video_interface.dto.h5.H5OrderDTO;
import com.example.video_interface.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * H5订单服务接口
 * 提供H5端订单相关的业务操作，包括订单查询、创建等
 */
public interface IH5OrderService {
    
    /**
     * 根据用户ID分页查询订单
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<H5OrderDTO> getUserOrders(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和订单类型分页查询订单
     * @param userId 用户ID
     * @param orderType 订单类型
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<H5OrderDTO> getUserOrdersByType(Long userId, String orderType, Pageable pageable);
    
    /**
     * 根据用户ID和订单状态分页查询订单
     * @param userId 用户ID
     * @param orderStatus 订单状态
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<H5OrderDTO> getUserOrdersByStatus(Long userId, String orderStatus, Pageable pageable);
    
    /**
     * 根据订单号查询订单详情
     * @param orderNo 订单号
     * @param userId 用户ID
     * @return 订单详情
     */
    H5OrderDTO getOrderByOrderNo(String orderNo, Long userId);
    
    /**
     * 创建订单
     * @param orderType 订单类型
     * @param productId 产品ID
     * @param productName 产品名称
     * @param amount 订单金额
     * @param paymentMethod 支付方式（可为null，创建订单时临时设置为BALANCE，支付时更新为实际支付方式）
     * @param userId 用户ID
     * @param remark 备注
     * @return 创建的订单
     */
    Order createOrder(String orderType, Long productId, String productName, 
                     java.math.BigDecimal amount, String paymentMethod, 
                     Long userId, String remark);
    
    /**
     * 更新订单状态
     * @param orderNo 订单号
     * @param orderStatus 订单状态
     * @return 更新后的订单
     */
    Order updateOrderStatus(String orderNo, String orderStatus);
    
    /**
     * 更新订单支付方式
     * @param orderNo 订单号
     * @param paymentMethod 支付方式
     * @return 更新后的订单
     */
    Order updatePaymentMethod(String orderNo, String paymentMethod);
    
    /**
     * 更新订单支付信息
     * @param orderNo 订单号
     * @param outNo 第三方订单号
     * @param payNo 支付编号
     * @param paidTime 支付时间
     * @return 更新后的订单
     */
    Order updatePaymentInfo(String orderNo, String outNo, String payNo, java.time.LocalDateTime paidTime);
    
    /**
     * 取消订单
     * @param orderNo 订单号
     * @param userId 用户ID
     * @return 取消结果
     */
    boolean cancelOrder(String orderNo, Long userId);
    
    /**
     * 获取用户订单统计信息
     * @param userId 用户ID
     * @return 订单统计信息
     */
    java.util.Map<String, Object> getOrderStatistics(Long userId);
} 