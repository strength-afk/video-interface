-- 检查所有锁定用户的状态
SELECT 
    id,
    username,
    is_locked,
    lock_reason,
    lock_time,
    unlock_time,
    failed_login_attempts,
    NOW() as current_time,
    CASE 
        WHEN unlock_time IS NOT NULL AND unlock_time <= NOW() THEN '应该解锁'
        WHEN unlock_time IS NULL THEN '永久锁定'
        ELSE '未到解锁时间'
    END as unlock_status
FROM users 
WHERE is_locked = true
ORDER BY unlock_time ASC;

-- 检查需要自动解锁的用户
SELECT 
    id,
    username,
    is_locked,
    lock_reason,
    lock_time,
    unlock_time,
    failed_login_attempts
FROM users 
WHERE is_locked = true 
    AND unlock_time IS NOT NULL 
    AND unlock_time <= NOW()
ORDER BY unlock_time ASC; 