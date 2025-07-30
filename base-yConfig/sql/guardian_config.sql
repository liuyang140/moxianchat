CREATE TABLE guardian_config (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                 user_id BIGINT NOT NULL COMMENT '用户ID（唯一）',
                                 enabled BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否开启守护功能',
                                 address VARCHAR(255) COMMENT '住址',
                                 last_active_time DATETIME COMMENT '上次活跃时间（登录或点击“我已安全”）',
                                 notified BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已触发告警（避免重复短信）',

                                 create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除字段',

                                 UNIQUE KEY uk_user_id (user_id)
) COMMENT = '独居青年守护功能配置表';

CREATE TABLE guardian_emergency_contact (
                                            id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                            user_id BIGINT NOT NULL COMMENT '用户ID',
                                            contact_name VARCHAR(50) NOT NULL COMMENT '联系人姓名',
                                            contact_phone VARCHAR(20) NOT NULL COMMENT '联系人手机号',

                                            create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                            is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除字段',

                                            INDEX idx_user_id (user_id)
) COMMENT = '独居青年守护紧急联系人表';

CREATE TABLE guardian_notify_log (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                     user_id BIGINT NOT NULL COMMENT '用户ID',
                                     notify_type INT NOT NULL COMMENT '通知类型 WECHAT-0 SMS-1',
                                     contact_phone VARCHAR(20) COMMENT '通知目标（仅短信）',
                                     status INT NOT NULL COMMENT '发送状态 0 失败，1成功',
                                     reason VARCHAR(255) COMMENT '失败原因',
                                     notify_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '通知时间',

                                     create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除字段',

                                     INDEX idx_user_id_time (user_id, notify_time)
) COMMENT = '独居青年守护通知日志表';

CREATE TABLE guardian_schedule_rule (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                        config_id BIGINT NOT NULL COMMENT '关联 guardian_config.id',
                                        weekday TINYINT COMMENT '星期几，1=周一，...，7=周日，NULL或0表示每日生效',
                                        start_time VARCHAR(15) NOT NULL COMMENT '守护开始时间',
                                        end_time VARCHAR(15) NOT NULL COMMENT '守护结束时间',

                                        create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除字段',

                                        INDEX idx_config_weekday (config_id, weekday),
                                        CONSTRAINT fk_schedule_config FOREIGN KEY (config_id) REFERENCES guardian_config(id)
) COMMENT = '守护时间配置明细表（支持每日及每周按天配置）';