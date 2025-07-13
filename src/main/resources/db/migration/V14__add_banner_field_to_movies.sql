-- 为movies表添加banner字段
ALTER TABLE movies ADD COLUMN banner VARCHAR(500) COMMENT 'Banner图片相对路径' AFTER cover;

-- 更新现有数据，将cover字段的值复制到banner字段（作为临时解决方案）
UPDATE movies SET banner = cover WHERE banner IS NULL; 