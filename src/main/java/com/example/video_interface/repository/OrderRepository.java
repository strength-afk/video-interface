package com.example.video_interface.repository;

import com.example.video_interface.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单Repository接口
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    
    /**
     * 根据订单号查询订单
     * @param orderNo 订单号
     * @return 订单信息
     */
    Optional<Order> findByOrderNo(String orderNo);
    
    /**
     * 根据用户ID分页查询订单
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和订单类型分页查询订单
     * @param userId 用户ID
     * @param orderType 订单类型
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<Order> findByUserIdAndOrderTypeOrderByCreatedAtDesc(Long userId, Order.OrderType orderType, Pageable pageable);
    
    /**
     * 根据用户ID和订单状态分页查询订单
     * @param userId 用户ID
     * @param orderStatus 订单状态
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<Order> findByUserIdAndOrderStatusOrderByCreatedAtDesc(Long userId, Order.OrderStatus orderStatus, Pageable pageable);
    
    /**
     * 根据用户ID和时间范围查询订单
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.createdAt BETWEEN :startTime AND :endTime ORDER BY o.createdAt DESC")
    Page<Order> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );
    
    /**
     * 根据用户ID查询所有订单
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 根据用户ID和产品ID查询订单
     * @param userId 用户ID
     * @param productId 产品ID
     * @return 订单列表
     */
    List<Order> findByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 根据用户ID、产品ID和订单状态查询订单
     * @param userId 用户ID
     * @param productId 产品ID
     * @param orderStatus 订单状态
     * @return 订单列表
     */
    List<Order> findByUserIdAndProductIdAndOrderStatus(Long userId, Long productId, Order.OrderStatus orderStatus);
    
    /**
     * 统计用户订单数量
     * @param userId 用户ID
     * @return 订单数量
     */
    long countByUserId(Long userId);
    
    /**
     * 统计用户已支付订单数量
     * @param userId 用户ID
     * @return 已支付订单数量
     */
    long countByUserIdAndOrderStatus(Long userId, Order.OrderStatus orderStatus);
    
    /**
     * 根据第三方订单号查询订单
     * @param outNo 第三方订单号
     * @return 订单信息
     */
    Optional<Order> findByOutNo(String outNo);
} 