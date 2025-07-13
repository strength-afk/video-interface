-- 插入电影分类数据
INSERT INTO movie_categories (name, description, weight, enabled, created_at, updated_at) VALUES
('剧情', '剧情类电影', 100, true, NOW(), NOW()),
('动作', '动作类电影', 90, true, NOW(), NOW()),
('爱情', '爱情类电影', 80, true, NOW(), NOW()),
('科幻', '科幻类电影', 70, true, NOW(), NOW()),
('恐怖', '恐怖类电影', 60, true, NOW(), NOW()),
('喜剧', '喜剧类电影', 50, true, NOW(), NOW()),
('动画', '动画类电影', 40, true, NOW(), NOW()),
('纪录片', '纪录片类电影', 30, true, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- 插入地区数据
INSERT INTO regions (name, description, weight, enabled, created_at, updated_at) VALUES
('华语', '华语电影', 100, true, NOW(), NOW()),
('欧美', '欧美电影', 90, true, NOW(), NOW()),
('日韩', '日韩电影', 80, true, NOW(), NOW()),
('印度', '印度电影', 70, true, NOW(), NOW()),
('其他', '其他地区电影', 60, true, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW(); 