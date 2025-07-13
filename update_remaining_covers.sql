-- 更新剩余电影的图片链接
-- 为黑客帝国、美丽人生、指环王等电影设置合适的海报图片

UPDATE movies SET cover = 'https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=400&h=600&fit=crop&crop=center' WHERE title = '指环王：王者归来';
UPDATE movies SET cover = 'https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=400&h=600&fit=crop&crop=center' WHERE title = '黑客帝国';
UPDATE movies SET cover = 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=400&h=600&fit=crop&crop=center' WHERE title = '美丽人生'; 