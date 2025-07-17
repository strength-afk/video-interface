package com.example.video_interface.service.admin.impl;

import com.example.video_interface.dto.admin.AdminOrderDTO;
import com.example.video_interface.dto.admin.AdminOrderRequest;
import com.example.video_interface.model.Order;
import com.example.video_interface.repository.OrderRepository;
import com.example.video_interface.service.admin.IAdminOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员订单管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements IAdminOrderService {

    private final OrderRepository orderRepository;

    @Override
    public Page<AdminOrderDTO> getOrderList(AdminOrderRequest request) {
        try {
            // 构建分页和排序
            Sort sort = Sort.by(
                "desc".equalsIgnoreCase(request.getSortDirection()) ? 
                Sort.Direction.DESC : Sort.Direction.ASC, 
                request.getSortBy()
            );
            Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);

            // 构建查询条件
            Specification<Order> spec = buildSpecification(request);

            // 执行查询
            Page<Order> orderPage = orderRepository.findAll(spec, pageable);

            // 转换为DTO
            return orderPage.map(this::convertToDTO);
        } catch (Exception e) {
            log.error("查询订单列表失败: {}", e.getMessage());
            throw new RuntimeException("查询订单列表失败", e);
        }
    }

    @Override
    public AdminOrderDTO getOrderById(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));
            return convertToDTO(order);
        } catch (Exception e) {
            log.error("获取订单详情失败: {}", e.getMessage());
            throw new RuntimeException("获取订单详情失败", e);
        }
    }

    @Override
    public AdminOrderDTO getOrderByOrderNo(String orderNo) {
        try {
            Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderNo));
            return convertToDTO(order);
        } catch (Exception e) {
            log.error("根据订单号获取订单详情失败: {}", e.getMessage());
            throw new RuntimeException("根据订单号获取订单详情失败", e);
        }
    }

    @Override
    @Transactional
    public AdminOrderDTO cancelOrder(Long orderId, String reason) {
        try {
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));

            // 检查订单状态
            if (order.getOrderStatus() != Order.OrderStatus.PENDING) {
                throw new IllegalArgumentException("只能取消待支付的订单");
            }

            // 更新订单状态
            order.setOrderStatus(Order.OrderStatus.CANCELLED);
            order.setRemark(reason);
            order.setUpdatedAt(LocalDateTime.now());

            Order savedOrder = orderRepository.save(order);
            log.info("订单取消成功: {}, 原因: {}", orderId, reason);
            return convertToDTO(savedOrder);
        } catch (Exception e) {
            log.error("取消订单失败: {}", e.getMessage());
            throw new RuntimeException("取消订单失败", e);
        }
    }

    @Override
    @Transactional
    public AdminOrderDTO refundOrder(Long orderId, BigDecimal refundAmount, String reason) {
        try {
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));

            // 检查订单状态
            if (order.getOrderStatus() != Order.OrderStatus.PAID) {
                throw new IllegalArgumentException("只能退款已支付的订单");
            }

            // 检查退款金额
            if (refundAmount.compareTo(order.getAmount()) > 0) {
                throw new IllegalArgumentException("退款金额不能大于订单金额");
            }

            // 更新订单状态
            order.setOrderStatus(Order.OrderStatus.REFUNDED);
            order.setRemark(reason);
            order.setUpdatedAt(LocalDateTime.now());

            Order savedOrder = orderRepository.save(order);
            log.info("订单退款成功: {}, 退款金额: {}, 原因: {}", orderId, refundAmount, reason);
            return convertToDTO(savedOrder);
        } catch (Exception e) {
            log.error("退款订单失败: {}", e.getMessage());
            throw new RuntimeException("退款订单失败", e);
        }
    }

    @Override
    public Map<String, Object> getOrderStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 查询指定时间范围内的订单
            List<Order> orders = orderRepository.findAll(buildTimeRangeSpecification(startTime, endTime));

            // 计算统计数据
            long totalOrders = orders.size(); // 所有订单总数
            BigDecimal totalAmount = orders.stream()
                .filter(order -> order.getOrderStatus() == Order.OrderStatus.PAID)
                .map(Order::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            long pendingOrders = orders.stream()
                .filter(order -> order.getOrderStatus() == Order.OrderStatus.PENDING)
                .count();

            long paidOrders = orders.stream()
                .filter(order -> order.getOrderStatus() == Order.OrderStatus.PAID)
                .count();

            long cancelledOrders = orders.stream()
                .filter(order -> order.getOrderStatus() == Order.OrderStatus.CANCELLED)
                .count();

            long refundedOrders = orders.stream()
                .filter(order -> order.getOrderStatus() == Order.OrderStatus.REFUNDED)
                .count();

            statistics.put("totalOrders", totalOrders);
            statistics.put("totalAmount", totalAmount);
            statistics.put("pendingOrders", pendingOrders);
            statistics.put("paidOrders", paidOrders);
            statistics.put("cancelledOrders", cancelledOrders);
            statistics.put("refundedOrders", refundedOrders);
            statistics.put("startTime", startTime);
            statistics.put("endTime", endTime);

            return statistics;
        } catch (Exception e) {
            log.error("获取订单统计失败: {}", e.getMessage());
            throw new RuntimeException("获取订单统计失败", e);
        }
    }

    @Override
    public Map<String, Object> getTodayOrderStatistics() {
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return getOrderStatistics(startTime, endTime);
    }

    @Override
    public Map<String, Object> getWeekOrderStatistics() {
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now().minusDays(6), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return getOrderStatistics(startTime, endTime);
    }

    @Override
    public Map<String, Object> getMonthOrderStatistics() {
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now().minusDays(29), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return getOrderStatistics(startTime, endTime);
    }

    @Override
    public Map<String, Object> getDailyOrderStatistics(int days) {
        try {
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> dailyData = new ArrayList<>();
            
            LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            
            for (int i = days - 1; i >= 0; i--) {
                LocalDateTime dayStart = LocalDateTime.of(LocalDate.now().minusDays(i), LocalTime.MIN);
                LocalDateTime dayEnd = LocalDateTime.of(LocalDate.now().minusDays(i), LocalTime.MAX);
                
                // 查询当天的订单
                List<Order> dayOrders = orderRepository.findAll(buildTimeRangeSpecification(dayStart, dayEnd));
                
                // 计算当天统计数据 - 只统计支付成功的订单
                long dayTotalOrders = dayOrders.stream()
                    .filter(order -> order.getOrderStatus() == Order.OrderStatus.PAID)
                    .count();
                BigDecimal dayTotalAmount = dayOrders.stream()
                    .filter(order -> order.getOrderStatus() == Order.OrderStatus.PAID)
                    .map(Order::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                // 生成日期标签
                String dateLabel;
                if (days <= 7) {
                    dateLabel = String.format("%d/%d", dayStart.getMonthValue(), dayStart.getDayOfMonth());
                } else {
                    dateLabel = String.format("%d/%d", dayStart.getMonthValue(), dayStart.getDayOfMonth());
                }
                
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("label", dateLabel);
                dayData.put("value", dayTotalOrders);
                dayData.put("amount", dayTotalAmount);
                dayData.put("date", dayStart.toLocalDate().toString());
                
                dailyData.add(dayData);
            }
            
            result.put("dailyData", dailyData);
            result.put("totalDays", days);
            
            return result;
        } catch (Exception e) {
            log.error("获取按日期分组的订单统计失败: {}", e.getMessage());
            throw new RuntimeException("获取按日期分组的订单统计失败", e);
        }
    }

    @Override
    public List<AdminOrderDTO> exportOrders(AdminOrderRequest request) {
        try {
            // 构建查询条件
            Specification<Order> spec = buildSpecification(request);

            // 查询所有符合条件的订单
            List<Order> orders = orderRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));

            // 转换为DTO
            return orders.stream().map(this::convertToDTO).toList();
        } catch (Exception e) {
            log.error("导出订单数据失败: {}", e.getMessage());
            throw new RuntimeException("导出订单数据失败", e);
        }
    }

    /**
     * 构建查询条件
     */
    private Specification<Order> buildSpecification(AdminOrderRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键词搜索
            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = "%" + request.getKeyword() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(root.get("orderNo"), keyword),
                    criteriaBuilder.like(root.get("productName"), keyword),
                    criteriaBuilder.like(root.get("user").get("username"), keyword)
                );
                predicates.add(keywordPredicate);
            }

            // 订单类型筛选
            if (request.getOrderTypeFilter() != null) {
                predicates.add(criteriaBuilder.equal(root.get("orderType"), request.getOrderTypeFilter()));
            }

            // 订单状态筛选
            if (request.getOrderStatusFilter() != null) {
                predicates.add(criteriaBuilder.equal(root.get("orderStatus"), request.getOrderStatusFilter()));
            }

            // 支付方式筛选
            if (request.getPaymentMethodFilter() != null) {
                predicates.add(criteriaBuilder.equal(root.get("paymentMethod"), request.getPaymentMethodFilter()));
            }

            // 用户ID筛选
            if (request.getUserIdFilter() != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), request.getUserIdFilter()));
            }

            // 产品ID筛选
            if (request.getProductIdFilter() != null) {
                predicates.add(criteriaBuilder.equal(root.get("productId"), request.getProductIdFilter()));
            }



            // 创建时间范围筛选
            if (request.getStartTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), request.getStartTime()));
            }
            if (request.getEndTime() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), request.getEndTime()));
            }

            // 支付时间范围筛选
            if (request.getPaidStartTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("paidTime"), request.getPaidStartTime()));
            }
            if (request.getPaidEndTime() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("paidTime"), request.getPaidEndTime()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 构建时间范围查询条件
     */
    private Specification<Order> buildTimeRangeSpecification(LocalDateTime startTime, LocalDateTime endTime) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (startTime != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startTime));
            }
            if (endTime != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endTime));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 转换为DTO
     */
    private AdminOrderDTO convertToDTO(Order order) {
        return AdminOrderDTO.builder()
            .id(order.getId())
            .orderNo(order.getOrderNo())
            .userId(order.getUser().getId())
            .username(order.getUser().getUsername())
            .userEmail(order.getUser().getEmail())
            .orderType(order.getOrderType())
            .orderTypeDescription(order.getOrderType().getDescription())
            .productId(order.getProductId())
            .productName(order.getProductName())
            .amount(order.getAmount())
            .paymentMethod(order.getPaymentMethod())
            .paymentMethodDescription(order.getPaymentMethod() != null ? order.getPaymentMethod().getDescription() : null)
            .orderStatus(order.getOrderStatus())
            .orderStatusDescription(order.getOrderStatus().getDescription())
            .paidTime(order.getPaidTime())
            .outNo(order.getOutNo())
            .payNo(order.getPayNo())
            .remark(order.getRemark())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }
} 