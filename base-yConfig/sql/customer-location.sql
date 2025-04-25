CREATE TABLE user_location (
                               id BIGINT PRIMARY KEY COMMENT '主键',
                               create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0正常 1删除',
                               customer_id BIGINT NOT NULL COMMENT '用户ID',
                               latitude DOUBLE NOT NULL COMMENT '纬度',
                               longitude DOUBLE NOT NULL COMMENT '经度'
);