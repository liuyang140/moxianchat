package com.ly.common.constant;

public class RedisConstant {

    //用户登录
    public static final String USER_LOGIN_KEY_PREFIX = "user:login:wxlogin_";
    public static final String USER_LOGIN_REFRESH_KEY_PREFIX = "user:login:refresh:";
    public static final int USER_LOGIN_KEY_TIMEOUT = 60 * 60 * 24 * 100;
    public static final int USER_LOGIN_REFRESH_KEY_TIMEOUT = 60 * 60 * 24 * 365;

    //经纬度
    public static final String GEO_KEY = "user:geo:location";

    //聊天
    public static final String ONLINE_KEY_PREFIX = "chat:online:"; // 用户在线状态
    public static final String CHANNEL_KEY_PREFIX = "chat:channel:"; // 用户ChannelId
    public static final String LAST_ACTIVE_PREFIX = "chat:lastActive:"; // 活跃时间
    public static final String USER_ROOM_PREFIX = "chat:user:rooms:"; //用户加入的房间集合
    public static final String ROOM_KEY_PREFIX = "chat:room:"; // 房间成员集合

    //用户缓存
    public static final String USER_CACHE_KEY_PREFIX = "user:cache:";



//    //订单与任务关联
//    public static final String ORDER_JOB = "order:job:";
//    public static final long ORDER_JOB_EXPIRES_TIME = 15;

    //更新订单位置
    public static final String UPDATE_ORDER_LOCATION = "update:order:location:";
    public static final long UPDATE_ORDER_LOCATION_EXPIRES_TIME = 15;

    //订单接单标识
    public static final String ORDER_ACCEPT_MARK = "order:accept:mark:";
    public static final long ORDER_ACCEPT_MARK_EXPIRES_TIME = 15;

    //抢新订单锁
    public static final String ROB_NEW_ORDER_LOCK = "rob:new:order:lock";
    //等待获取锁的时间
    public static final long ROB_NEW_ORDER_LOCK_WAIT_TIME = 1;
    //加锁的时间
    public static final long ROB_NEW_ORDER_LOCK_LEASE_TIME = 1;

    //优惠券信息
    public static final String COUPON_INFO = "coupon:info:";

    //优惠券分布式锁
    public static final String COUPON_LOCK = "coupon:lock:";
    //等待获取锁的时间
    public static final long COUPON_LOCK_WAIT_TIME = 1;
    //加锁的时间
    public static final long COUPON_LOCK_LEASE_TIME = 1;
}
