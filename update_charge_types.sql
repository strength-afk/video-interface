-- 更新收费类型：将PAY类型改为VIP类型
-- 执行前请备份数据库

-- 1. 更新电影表中的收费类型
UPDATE movies 
SET charge_type = 'VIP' 
WHERE charge_type = 'PAY';

-- 2. 更新相关字段
UPDATE movies 
SET is_vip = true, 
    is_free = false 
WHERE charge_type = 'VIP';

-- 3. 确保VIP电影有合理的价格设置
UPDATE movies 
SET price = 9.90 
WHERE charge_type = 'VIP' AND (price IS NULL OR price = 0);

-- 4. 验证更新结果
SELECT 
    charge_type,
    COUNT(*) as count,
    AVG(price) as avg_price
FROM movies 
GROUP BY charge_type;

-- 5. 显示更新后的电影列表（前10条）
SELECT 
    id,
    title,
    charge_type,
    price,
    is_vip,
    is_free
FROM movies 
ORDER BY id 
LIMIT 10; 