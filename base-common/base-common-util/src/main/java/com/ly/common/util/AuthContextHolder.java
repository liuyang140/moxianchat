package com.ly.common.util;

/**
 * 获取当前用户信息帮助类
 */
public class AuthContextHolder {

    private static final ThreadLocal<Long> USERID_HOLDER = new ThreadLocal<Long>();
    private static final ThreadLocal<String> TOKEN_HOLDER= new ThreadLocal<>();

    public static void setUserId(Long _userId) {
        USERID_HOLDER.set(_userId);
    }

    public static Long getUserId() {
        return USERID_HOLDER.get();
    }

    public static void clear() {
        USERID_HOLDER.remove();
    }


    public static void setToken(String token) {
        TOKEN_HOLDER.set(token);
    }

    public static String getToken() {
        return TOKEN_HOLDER.get();
    }

    public static void clearToken() {
        TOKEN_HOLDER.remove();
    }

}
