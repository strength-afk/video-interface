package com.example.video_interface.service.common.impl;

import com.example.video_interface.model.User;
import com.example.video_interface.model.Order;
import com.example.video_interface.repository.UserRepository;
import com.example.video_interface.repository.OrderRepository;
import com.example.video_interface.service.common.ISystemAutoTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统自动任务服务实现类
 * 统一处理自动解锁用户、自动关闭订单等定时任务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemAutoTaskServiceImpl implements ISystemAutoTaskService {
    
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    
    /**
     * 统一定时任务：自动解锁用户、自动关闭订单
     * 默认5分钟执行一次
     */
    @Override
    @Scheduled(fixedRateString = "${app.scheduling.auto-unlock.interval:300000}")
    @Transactional
    public void runAllAutoTasks() {
        checkAndAutoUnlockTasks();
        checkAndAutoCloseOrders();
    }

    /**
     * 手动触发自动任务检查
     */
    @Override
    @Transactional
    public void runAllAutoTasksManually() {
        log.info("手动触发系统自动任务检查");
        runAllAutoTasks();
    }

    /**
     * 开发环境快速测试任务：每1分钟执行一次
     */
    @Profile("dev")
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void devAutoTask() {
        log.info("=== 开发环境自动任务测试 ===");
        checkAndAutoUnlockTasks();
        checkAndAutoCloseOrders();
    }

    /**
     * 自动解锁用户逻辑
     */
    private void checkAndAutoUnlockTasks() {
        log.info("开始执行用户自动解锁检查任务");
        try {
            List<User> lockedUsers = userRepository.findByIsLockedTrueAndUnlockTimeBefore(LocalDateTime.now());
            if (lockedUsers.isEmpty()) {
                log.info("没有需要自动解锁的用户");
                return;
            }
            log.info("发现 {} 个用户需要自动解锁", lockedUsers.size());
            for (User user : lockedUsers) {
                try {
                    log.info("正在解锁用户: {}, 解锁时间: {}, 当前时间: {}", 
                        user.getUsername(), user.getUnlockTime(), LocalDateTime.now());
                    user.setIsLocked(false);
                    user.setLockReason(null);
                    user.setLockTime(null);
                    user.setUnlockTime(null);
                    user.setFailedLoginAttempts(0);
                    user.setLastFailedLoginTime(null);
                    User savedUser = userRepository.save(user);
                    log.info("用户 {} 已自动解锁", savedUser.getUsername());
                } catch (Exception e) {
                    log.error("自动解锁用户 {} 失败: {}", user.getUsername(), e.getMessage(), e);
                }
            }
            log.info("用户自动解锁检查任务完成，成功解锁 {} 个用户", lockedUsers.size());
        } catch (Exception e) {
            log.error("执行用户自动解锁检查任务时发生错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 自动关闭超时未支付订单逻辑
     * 关闭下单30分钟未支付的订单
     */
    private void checkAndAutoCloseOrders() {
        log.info("开始执行订单自动关闭检查任务");
        try {
            LocalDateTime deadline = LocalDateTime.now().minusMinutes(30);
            // 查询30分钟未支付的订单
            List<Order> timeoutOrders = orderRepository.findAll((root, query, cb) -> cb.and(
                cb.equal(root.get("orderStatus"), Order.OrderStatus.PENDING),
                cb.lessThan(root.get("createdAt"), deadline)
            ));
            if (timeoutOrders.isEmpty()) {
                log.info("没有需要自动关闭的超时订单");
                return;
            }
            log.info("发现 {} 个超时未支付订单需要关闭", timeoutOrders.size());
            for (Order order : timeoutOrders) {
                try {
                    log.info("正在关闭订单: {}, 下单时间: {}, 当前时间: {}", 
                        order.getOrderNo(), order.getCreatedAt(), LocalDateTime.now());
                    order.setOrderStatus(Order.OrderStatus.CANCELLED);
                    order.setRemark("系统自动关闭：下单30分钟未支付");
                    orderRepository.save(order);
                    log.info("订单 {} 已自动关闭", order.getOrderNo());
                } catch (Exception e) {
                    log.error("自动关闭订单 {} 失败: {}", order.getOrderNo(), e.getMessage(), e);
                }
            }
            log.info("订单自动关闭检查任务完成，成功关闭 {} 个订单", timeoutOrders.size());
        } catch (Exception e) {
            log.error("执行订单自动关闭检查任务时发生错误: {}", e.getMessage(), e);
        }
    }
} 