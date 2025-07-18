package com.example.video_interface.service.h5.impl;

import com.example.video_interface.dto.h5.H5OrderDTO;
import com.example.video_interface.model.Order;
import com.example.video_interface.model.User;
import com.example.video_interface.model.Movie;
import com.example.video_interface.model.VipPackage;
import com.example.video_interface.repository.OrderRepository;
import com.example.video_interface.repository.UserRepository;
import com.example.video_interface.repository.MovieRepository;
import com.example.video_interface.repository.VipPackageRepository;
import com.example.video_interface.repository.UserMoviePurchaseRepository;
import com.example.video_interface.service.h5.IH5OrderService;
import com.example.video_interface.service.common.IIdrPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * H5订单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class H5OrderServiceImpl implements IH5OrderService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final VipPackageRepository vipPackageRepository;
    private final UserMoviePurchaseRepository userMoviePurchaseRepository;
    private final IIdrPaymentService idrPaymentService;
    
    @Override
    public Page<H5OrderDTO> getUserOrders(Long userId, Pageable pageable) {
        log.debug("获取用户订单列表，用户ID: {}", userId);
        
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return orders.map(this::convertToDTO);
    }
    
    @Override
    public Page<H5OrderDTO> getUserOrdersByType(Long userId, String orderType, Pageable pageable) {
        log.debug("根据订单类型获取用户订单，用户ID: {}, 订单类型: {}", userId, orderType);
        
        try {
            Order.OrderType type = Order.OrderType.valueOf(orderType.toUpperCase());
            Page<Order> orders = orderRepository.findByUserIdAndOrderTypeOrderByCreatedAtDesc(userId, type, pageable);
            return orders.map(this::convertToDTO);
        } catch (IllegalArgumentException e) {
            log.warn("无效的订单类型: {}", orderType);
            throw new IllegalArgumentException("无效的订单类型");
        }
    }
    
    @Override
    public Page<H5OrderDTO> getUserOrdersByStatus(Long userId, String orderStatus, Pageable pageable) {
        log.debug("根据订单状态获取用户订单，用户ID: {}, 订单状态: {}", userId, orderStatus);
        
        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(orderStatus.toUpperCase());
            Page<Order> orders = orderRepository.findByUserIdAndOrderStatusOrderByCreatedAtDesc(userId, status, pageable);
            return orders.map(this::convertToDTO);
        } catch (IllegalArgumentException e) {
            log.warn("无效的订单状态: {}", orderStatus);
            throw new IllegalArgumentException("无效的订单状态");
        }
    }
    
    @Override
    public H5OrderDTO getOrderByOrderNo(String orderNo, Long userId) {
        log.debug("根据订单号查询订单，订单号: {}, 用户ID: {}", orderNo, userId);
        
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        // 验证订单是否属于当前用户
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("无权访问此订单");
        }
        
        return convertToDTO(order);
    }
    
    @Override
    @Transactional
    public Order createOrder(String orderType, Long productId, String productName, 
                           java.math.BigDecimal amount, String paymentMethod, 
                           Long userId, String remark) {
        log.debug("创建订单，用户ID: {}, 订单类型: {}, 产品ID: {}, 金额: {}", 
                userId, orderType, productId, amount);
        
        // 验证用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        // 验证订单类型
        Order.OrderType type;
        try {
            type = Order.OrderType.valueOf(orderType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的订单类型");
        }
        
        // 根据订单类型进行业务校验
        if (type == Order.OrderType.MOVIE_PURCHASE) {
            // 购买电影单片：检查电影是否存在且状态为ACTIVE
            Movie movie = movieRepository.findByIdAndStatus(productId, Movie.MovieStatus.ACTIVE);
            if (movie == null) {
                log.warn("电影不存在或已下架，电影ID: {}", productId);
                throw new IllegalArgumentException("电影不存在或已下架");
            }
            
            // 检查用户是否已购买过该电影
            if (userMoviePurchaseRepository.existsByUserIdAndMovieId(userId, productId)) {
                log.warn("用户已购买过该电影，用户ID: {}, 电影ID: {}", userId, productId);
                throw new IllegalArgumentException("您已购买过此电影");
            }
            
        } else if (type == Order.OrderType.VIP_PURCHASE) {
            // 购买VIP会员：检查VIP套餐是否存在且状态为ACTIVE
            VipPackage vipPackage = vipPackageRepository.findById(productId)
                    .orElse(null);
            if (vipPackage == null || vipPackage.getStatus() != VipPackage.PackageStatus.ACTIVE) {
                log.warn("VIP套餐不存在或已下架，套餐ID: {}", productId);
                throw new IllegalArgumentException("VIP套餐不存在或已下架");
            }
        }
        
   
                // 生成订单号
        String orderNo = generateOrderNo();
        
        // 调用iDataRiver创建第三方订单，获取out_no
        String outNo = null;
        try {
            Map<String, Object> orderParams = new HashMap<>();
            orderParams.put("amount", amount);
            orderParams.put("private", false);
            orderParams.put("name", "");
            orderParams.put("message", "");
            Map<String, Object> idrResult = idrPaymentService.createOrder(orderParams);
            log.debug("iDataRiver API响应: {}", idrResult);
            // 从iDataRiver响应中获取out_no
            if (idrResult != null) {
                // 检查响应状态码
                Object codeObj = idrResult.get("code");
                if (codeObj instanceof Integer && (Integer) codeObj == 0) {
                    // 成功响应，获取result中的orderId作为out_no
                    Object resultObj = idrResult.get("result");
                    if (resultObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> result = (Map<String, Object>) resultObj;
                        Object orderIdObj = result.get("orderId");
                        if (orderIdObj instanceof String) {
                            outNo = (String) orderIdObj;
                            log.info("iDataRiver订单创建成功，orderId: {}", outNo);
                        }
                    }
                } else {
                    log.error("iDataRiver订单创建失败，响应码: {}", codeObj);
                    throw new RuntimeException("第三方订单创建失败，请稍后重试");
                }
            }
            if (outNo == null) {
                log.error("iDataRiver订单创建失败或未返回out_no");
                throw new RuntimeException("第三方订单创建失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("调用iDataRiver创建订单失败", e);
            throw new RuntimeException("第三方订单创建失败，请稍后重试", e);
        }
        
        // 创建订单
        Order order = Order.builder()
                .orderNo(orderNo)
                .user(user)
                .orderType(type)
                .productId(productId)
                .productName(productName)
                .amount(amount)
                .orderStatus(Order.OrderStatus.PENDING)
                .paymentMethod(null) // 创建时不赋值
                .outNo(outNo) // 设置第三方订单号
                .remark(remark)
                .build();
        
        Order savedOrder = orderRepository.save(order);
        log.info("订单创建成功，订单号: {}, 第三方订单号: {}, 订单类型: {}, 产品ID: {}", 
                orderNo, outNo, type, productId);
        
        return savedOrder;
    }
    
    @Override
    @Transactional
    public Order updateOrderStatus(String orderNo, String orderStatus) {
        log.debug("更新订单状态，订单号: {}, 新状态: {}", orderNo, orderStatus);
        
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        // 验证订单状态
        Order.OrderStatus status;
        try {
            status = Order.OrderStatus.valueOf(orderStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的订单状态");
        }
        
        order.setOrderStatus(status);
        
        // 如果状态为已支付，设置支付时间
        if (status == Order.OrderStatus.PAID && order.getPaidTime() == null) {
            order.setPaidTime(LocalDateTime.now());
        }
        
        // 如果不是已支付，清空支付方式
        if (status != Order.OrderStatus.PAID) {
            order.setPaymentMethod(null);
        }
        
        Order updatedOrder = orderRepository.save(order);
        log.info("订单状态更新成功，订单号: {}, 新状态: {}", orderNo, status);
        
        return updatedOrder;
    }
    
    @Override
    @Transactional
    public boolean cancelOrder(String orderNo, Long userId) {
        log.debug("取消订单，订单号: {}, 用户ID: {}", orderNo, userId);
        
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        // 验证订单是否属于当前用户
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此订单");
        }
        
        // 只能取消待支付的订单
        if (order.getOrderStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalArgumentException("只能取消待支付的订单");
        }
        
        order.setOrderStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
        
        log.info("订单取消成功，订单号: {}", orderNo);
        return true;
    }
    
    @Override
    @Transactional
    public Order updatePaymentMethod(String orderNo, String paymentMethod) {
        log.debug("更新订单支付方式，订单号: {}, 支付方式: {}", orderNo, paymentMethod);
        
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        // 验证支付方式
        Order.PaymentMethod payment;
        try {
            payment = Order.PaymentMethod.valueOf(paymentMethod.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的支付方式");
        }
        
        order.setPaymentMethod(payment);
        Order updatedOrder = orderRepository.save(order);
        
        log.info("订单支付方式更新成功，订单号: {}, 支付方式: {}", orderNo, payment);
        return updatedOrder;
    }
    
    @Override
    @Transactional
    public Order updatePaymentInfo(String orderNo, String outNo, String payNo, LocalDateTime paidTime) {
        log.debug("更新订单支付信息，订单号: {}, 第三方订单号: {}, 支付编号: {}", orderNo, outNo, payNo);
        
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        order.setOutNo(outNo);
        order.setPayNo(payNo);
        order.setPaidTime(paidTime);
        order.setOrderStatus(Order.OrderStatus.PAID);
        
        Order updatedOrder = orderRepository.save(order);
        
        log.info("订单支付信息更新成功，订单号: {}, 第三方订单号: {}, 支付编号: {}", orderNo, outNo, payNo);
        return updatedOrder;
    }
    
    @Override
    public Map<String, Object> getOrderStatistics(Long userId) {
        log.debug("获取用户订单统计，用户ID: {}", userId);
        
        long totalOrders = orderRepository.countByUserId(userId);
        long paidOrders = orderRepository.countByUserIdAndOrderStatus(userId, Order.OrderStatus.PAID);
        long pendingOrders = orderRepository.countByUserIdAndOrderStatus(userId, Order.OrderStatus.PENDING);
        long cancelledOrders = orderRepository.countByUserIdAndOrderStatus(userId, Order.OrderStatus.CANCELLED);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalOrders", totalOrders);
        statistics.put("paidOrders", paidOrders);
        statistics.put("pendingOrders", pendingOrders);
        statistics.put("cancelledOrders", cancelledOrders);
        
        return statistics;
    }
    
    /**
     * 转换为DTO
     */
    private H5OrderDTO convertToDTO(Order order) {
        return H5OrderDTO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .orderType(order.getOrderType().toString())
                .orderTypeDesc(order.getOrderType().getDescription())
                .productId(order.getProductId())
                .productName(order.getProductName())
                .amount(order.getAmount())
                .paymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().toString() : null)
                .paymentMethodDesc(order.getPaymentMethod() != null ? order.getPaymentMethod().getDescription() : null)
                .orderStatus(order.getOrderStatus().toString())
                .orderStatusDesc(order.getOrderStatus().getDescription())
                .paidTime(order.getPaidTime())
                .outNo(order.getOutNo())
                .payNo(order.getPayNo())
                .remark(order.getRemark())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        // 生成格式：年月日时分秒 + 4位随机数
        String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return timestamp + random;
    }
} 