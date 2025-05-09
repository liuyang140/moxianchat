CREATE TABLE chat_room (
                           id BIGINT PRIMARY KEY,
                           type TINYINT DEFAULT 0 COMMENT '0-private 或 1-group',
                           name VARCHAR(255) DEFAULT NULL COMMENT '群聊名称，私聊为空',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删 1-已删'
);


CREATE TABLE chat_room_user (
                                id BIGINT PRIMARY KEY COMMENT '主键ID',
                                room_id BIGINT NOT NULL COMMENT '聊天室ID',
                                user_id BIGINT NOT NULL COMMENT '用户ID',
                                join_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
                                is_owner BOOLEAN DEFAULT FALSE COMMENT '是否是群主',
                                create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除字段'
) COMMENT='聊天室成员表';

CREATE TABLE chat_message (
                              id BIGINT PRIMARY KEY COMMENT '消息ID',
                              room_id BIGINT NOT NULL COMMENT '聊天室ID',
                              sender_id BIGINT NOT NULL COMMENT '发送者用户ID',
                              receiver_id BIGINT DEFAULT NULL COMMENT '接收者用户ID（私聊时使用）',
                              content TEXT NOT NULL COMMENT '消息内容',
                              message_type TINYINT DEFAULT 0 COMMENT '消息类型：0-text / 1-image / 2-file 等',
                              recalled TINYINT DEFAULT 0 COMMENT '是否被撤回：0-否，1-是',
                              timestamp BIGINT DEFAULT NULL COMMENT '客户端发送时间戳',
                              chat_type TINYINT DEFAULT 0 COMMENT '聊天类型：0-私聊，1-群聊',
                              create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
                              update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除字段'
) COMMENT='聊天消息表';

CREATE TABLE chat_message_read (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',  -- 消息已读状态记录的唯一标识符
                                   room_id BIGINT NOT NULL COMMENT '聊天室ID',  -- 所属聊天室的ID
                                   user_id BIGINT NOT NULL COMMENT '用户ID',  -- 用户的唯一标识符
                                   last_read_time DATETIME NOT NULL COMMENT '上次读取时间', -- 用户在聊天室中最后读取的时间
                                   create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',  -- 记录创建时间
                                   update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',  -- 记录更新时间，自动更新
                                   is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除字段：0-未删除，1-已删除'  -- 标识该记录是否已被逻辑删除
) COMMENT='已读状态表';