package com.ly.common.util;

import io.netty.channel.ChannelHandlerContext;

/**
 * 获取当前用户信息帮助类
 */
public class AuthContextHolder {

    private static final ThreadLocal<Long> USERID_HOLDER = new ThreadLocal<Long>();
    private static final ThreadLocal<ChannelHandlerContext> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void setUserId(Long _userId) {
        USERID_HOLDER.set(_userId);
    }

    public static Long getUserId() {
        return USERID_HOLDER.get();
    }

    public static void clear() {
        USERID_HOLDER.remove();
    }


    public static void setContext(ChannelHandlerContext ctx) {
        CONTEXT_HOLDER.set(ctx);
    }

    public static ChannelHandlerContext getContext() {
        return CONTEXT_HOLDER.get();
    }

    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }
}
