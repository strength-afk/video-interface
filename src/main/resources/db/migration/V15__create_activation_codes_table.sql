-- 创建激活码表
CREATE TABLE activation_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '激活码ID，主键，自增',
    code VARCHAR(32) NOT NULL UNIQUE COMMENT '激活码，唯一，不可为空',
    code_type VARCHAR(20) NOT NULL COMMENT '激活码类型：VIP-会员激活码，RECHARGE-充值激活码',
    code_status VARCHAR(20) NOT NULL DEFAULT 'UNUSED' COMMENT '激活码状态：UNUSED-未使用，USED-已使用，EXPIRED-已过期，DISABLED-已禁用',
    vip_duration INT COMMENT 'VIP时长（天），仅会员激活码有效',
    recharge_amount DECIMAL(10,2) COMMENT '充值金额，仅充值激活码有效',
    used_by BIGINT COMMENT '使用用户ID',
    used_at DATETIME COMMENT '使用时间',
    expire_at DATETIME COMMENT '过期时间',
    batch_number VARCHAR(50) COMMENT '批次号，用于批量管理',
    remark VARCHAR(200) COMMENT '备注信息',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_code (code),
    INDEX idx_code_type (code_type),
    INDEX idx_code_status (code_status),
    INDEX idx_batch_number (batch_number),
    INDEX idx_used_by (used_by),
    INDEX idx_expire_at (expire_at),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='激活码表';

-- 插入一些测试数据
INSERT INTO activation_codes (code, code_type, code_status, vip_duration, recharge_amount, batch_number, remark, expire_at) VALUES
('VIP001234567890', 'VIP', 'UNUSED', 30, NULL, 'BATCH001', '测试VIP激活码-30天', DATE_ADD(NOW(), INTERVAL 30 DAY)),
('VIP002345678901', 'VIP', 'UNUSED', 90, NULL, 'BATCH001', '测试VIP激活码-90天', DATE_ADD(NOW(), INTERVAL 30 DAY)),
('VIP003456789012', 'VIP', 'UNUSED', 365, NULL, 'BATCH001', '测试VIP激活码-365天', DATE_ADD(NOW(), INTERVAL 30 DAY)),
('RC001234567890', 'RECHARGE', 'UNUSED', NULL, 10.00, 'BATCH002', '测试充值激活码-10元', DATE_ADD(NOW(), INTERVAL 30 DAY)),
('RC002345678901', 'RECHARGE', 'UNUSED', NULL, 50.00, 'BATCH002', '测试充值激活码-50元', DATE_ADD(NOW(), INTERVAL 30 DAY)),
('RC003456789012', 'RECHARGE', 'UNUSED', NULL, 100.00, 'BATCH002', '测试充值激活码-100元', DATE_ADD(NOW(), INTERVAL 30 DAY)); 