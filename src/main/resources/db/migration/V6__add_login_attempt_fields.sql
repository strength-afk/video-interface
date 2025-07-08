-- 添加登录失败次数控制相关字段
-- V6__add_login_attempt_fields.sql

-- 添加登录失败次数
ALTER TABLE users ADD COLUMN failed_login_attempts INT DEFAULT 0 COMMENT '连续登录失败次数';

-- 添加最后登录失败时间
ALTER TABLE users ADD COLUMN last_failed_login_time DATETIME COMMENT '最后一次登录失败时间';

-- 添加账户锁定时间
ALTER TABLE users ADD COLUMN lock_time DATETIME COMMENT '账户锁定时间';

-- 添加账户解锁时间
ALTER TABLE users ADD COLUMN unlock_time DATETIME COMMENT '账户自动解锁时间';

-- 更新现有用户的默认值
UPDATE users SET failed_login_attempts = 0 WHERE failed_login_attempts IS NULL; 