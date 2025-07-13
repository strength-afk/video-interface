-- 检查地区表是否存在
SHOW TABLES LIKE 'regions';

-- 如果表不存在，创建地区表
CREATE TABLE IF NOT EXISTS regions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '地区ID，主键，自增',
    name VARCHAR(50) NOT NULL COMMENT '地区名称，不可为空',
    description VARCHAR(200) COMMENT '地区描述',
    icon VARCHAR(200) COMMENT '地区图标URL',
    weight INT DEFAULT 0 COMMENT '排序权重，数字越大越靠前',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) COMMENT '地区表';

-- 检查是否有地区数据
SELECT * FROM regions;

-- 如果没有数据，插入默认地区数据
INSERT INTO regions (name, description, weight, enabled, created_at, updated_at) 
SELECT * FROM (
    SELECT '日韩' as name, '日本、韩国地区' as description, 100 as weight, TRUE as enabled, NOW() as created_at, NOW() as updated_at
    UNION ALL
    SELECT '欧美', '欧洲、美国地区', 90, TRUE, NOW(), NOW()
    UNION ALL
    SELECT '华语', '中国大陆、香港、台湾地区', 80, TRUE, NOW(), NOW()
    UNION ALL
    SELECT '印度', '印度地区', 70, TRUE, NOW(), NOW()
    UNION ALL
    SELECT '泰国', '泰国地区', 60, TRUE, NOW(), NOW()
    UNION ALL
    SELECT '其他', '其他地区', 50, TRUE, NOW(), NOW()
) AS temp
WHERE NOT EXISTS (SELECT 1 FROM regions);

-- 查看最终的地区数据
SELECT * FROM regions ORDER BY weight DESC; 