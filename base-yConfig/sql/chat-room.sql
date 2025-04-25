CREATE TABLE chat_room (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0正常，1删除',

                           customer1_id BIGINT NOT NULL COMMENT '用户1 ID',
                           customer2_id BIGINT NOT NULL COMMENT '用户2 ID'
);