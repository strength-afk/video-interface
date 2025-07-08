-- 修改email字段为可选
ALTER TABLE users MODIFY COLUMN email VARCHAR(50) NULL COMMENT '邮箱地址，唯一，可为空，最大长度50';

-- 修改phone_number字段为可选
ALTER TABLE users MODIFY COLUMN phone_number VARCHAR(20) NULL;

-- 修改avatar字段为可选
ALTER TABLE users MODIFY COLUMN avatar VARCHAR(200) NULL; 