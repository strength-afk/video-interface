package com.example.video_interface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.video_interface.model.SystemConfig;

/**
 * 系统设置Repository
 * 用于操作system_config表
 */
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
} 