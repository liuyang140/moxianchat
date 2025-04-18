CREATE TABLE `customer_info` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `wx_open_id` varchar(200) NOT NULL DEFAULT '' COMMENT '微信openId',
                                 `nickname` varchar(200) DEFAULT '' COMMENT '用户昵称',
                                 `gender` char(1) NOT NULL DEFAULT '1' COMMENT '性别',
                                 `avatar_url` varchar(200) DEFAULT NULL COMMENT '头像',
                                 `phone` char(11) DEFAULT NULL COMMENT '电话',
                                 `status` tinyint(3) DEFAULT '1' COMMENT '1有效，2禁用',
                                 `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 `is_deleted` tinyint(3) NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE KEY `uni_open_id` (`wx_open_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT= '用户表';

CREATE TABLE `customer_login_log` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
                                      `customer_id` varchar(50) DEFAULT '' COMMENT '用户id',
                                      `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
                                      `status` tinyint(1) DEFAULT '1' COMMENT '登录状态',
                                      `msg` varchar(255) DEFAULT '' COMMENT '提示信息',
                                      `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                      `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '删除标记（0:不可用 1:可用）',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户登录记录';