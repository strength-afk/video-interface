-- 创建电影表
CREATE TABLE movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '电影ID，主键，自增',
    title VARCHAR(200) NOT NULL COMMENT '电影标题，不可为空',
    description TEXT COMMENT '剧情简介',
    cover VARCHAR(500) COMMENT '封面图片URL',
    duration VARCHAR(20) COMMENT '电影时长，格式：HH:mm:ss',
    rating DECIMAL(3,1) DEFAULT 0.0 COMMENT '评分，0.0-10.0',
    views BIGINT NOT NULL DEFAULT 0 COMMENT '观看次数',
    likes BIGINT NOT NULL DEFAULT 0 COMMENT '点赞次数',
    favorites BIGINT NOT NULL DEFAULT 0 COMMENT '收藏次数',
    release_date TIMESTAMP COMMENT '发布日期',
    release_year INT COMMENT '发行年份',
    category_id BIGINT COMMENT '电影分类ID',
    region_id BIGINT COMMENT '电影地区ID',
    quality VARCHAR(50) COMMENT '画质：720P、1080P、4K等',
    tags JSON COMMENT '标签，JSON格式存储',
    is_vip BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否为VIP专享',
    is_free BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否为免费电影',
    price DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格，收费电影的价格',
    trial_duration INT NOT NULL DEFAULT 0 COMMENT '试看时长（秒），0表示不允许试看',
    file_path VARCHAR(500) COMMENT '视频文件路径',
    file_size BIGINT COMMENT '文件大小（字节）',
    file_format VARCHAR(20) COMMENT '文件格式：mp4、mkv、avi等',
    charge_type VARCHAR(20) NOT NULL DEFAULT 'FREE' COMMENT '收费类型：FREE-免费，VIP-VIP专享',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-上架，INACTIVE-下架，DELETED-已删除',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序权重，数字越大越靠前',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    INDEX idx_title (title),
    INDEX idx_status (status),
    INDEX idx_category_id (category_id),
    INDEX idx_region_id (region_id),
    INDEX idx_release_year (release_year),
    INDEX idx_charge_type (charge_type),
    INDEX idx_is_vip (is_vip),
    INDEX idx_is_free (is_free),
    INDEX idx_quality (quality),
    INDEX idx_views (views),
    INDEX idx_rating (rating),
    INDEX idx_release_date (release_date),
    INDEX idx_sort_order (sort_order),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (category_id) REFERENCES movie_categories(id) ON DELETE SET NULL,
    FOREIGN KEY (region_id) REFERENCES regions(id) ON DELETE SET NULL
) COMMENT '电影表';

-- 插入示例数据
INSERT INTO movies (title, description, cover, duration, rating, release_year, category_id, region_id, quality, tags, is_vip, is_free, price, trial_duration, charge_type, status, sort_order) VALUES
('肖申克的救赎', '希望让人自由。', 'https://example.com/shawshank.jpg', '02:22:00', 9.7, 1994, 1, 2, '1080P', '["剧情", "犯罪"]', false, true, 0.00, 0, 'FREE', 'ACTIVE', 100),
('教父', '千万不要恨你的敌人，这会影响你的判断力。', 'https://example.com/godfather.jpg', '02:55:00', 9.6, 1972, 1, 2, '4K', '["剧情", "犯罪"]', false, true, 0.00, 0, 'FREE', 'ACTIVE', 99),
('盗梦空间', '你是在等待一辆永远不会来的火车吗？', 'https://example.com/inception.jpg', '02:28:00', 9.3, 2010, 2, 2, '1080P', '["科幻", "动作"]', true, false, 5.00, 300, 'PAY', 'ACTIVE', 98),
('泰坦尼克号', '你跳，我也跳。', 'https://example.com/titanic.jpg', '03:14:00', 9.4, 1997, 3, 2, '1080P', '["爱情", "灾难"]', false, true, 0.00, 0, 'FREE', 'ACTIVE', 97),
('阿甘正传', '生活就像一盒巧克力，你永远不知道下一颗是什么味道。', 'https://example.com/forrest.jpg', '02:22:00', 9.5, 1994, 3, 2, '4K', '["剧情", "爱情"]', true, false, 0.00, 600, 'VIP', 'ACTIVE', 96); 