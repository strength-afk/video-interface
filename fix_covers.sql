-- 修复404的图片链接，使用可用的高质量图片

UPDATE movies SET cover = 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=400&h=600&fit=crop&crop=center' WHERE title = '指环王：王者归来';
UPDATE movies SET cover = 'https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=400&h=600&fit=crop&crop=center' WHERE title = '黑客帝国';
UPDATE movies SET cover = 'https://images.unsplash.com/photo-1542206395-9feb3edaa68d?w=400&h=600&fit=crop&crop=center' WHERE title = '美丽人生'; 