package com.example.video_interface.service.common;

/**
 * 系统自动任务服务接口
 * 统一处理自动解锁用户、自动关闭订单等定时任务
 */
public interface ISystemAutoTaskService {
    /**
     * 定时任务统一入口，自动执行所有系统自动任务（如自动解锁、自动关单等）
     */
    void runAllAutoTasks();

    /**
     * 手动触发所有系统自动任务
     */
    void runAllAutoTasksManually();
} 