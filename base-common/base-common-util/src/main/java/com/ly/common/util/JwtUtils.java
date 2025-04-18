package com.ly.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
public class JwtUtils {

    // 默认密钥 - 实际项目中应从配置文件中读取
    private static final String SECRET_KEY = "aY2pG7nRsQwPzJt5Lk9HdVmWbXc8EuFqZo1KrN6UbGyTcD4MhAvXn2LqOjSeRtYf";

    // 默认过期时间(毫秒) - 24小时
    private static final long DEFAULT_EXPIRATION_MS = 60 * 60 * 24 * 1000L;

    // JWT签发者
    private static final String ISSUER = "service-customer";

    private static final String USER_ID_CLAIM = "userId";

    /**
     * 生成JWT Token
     * @param userId 用户ID(Long类型)
     * @return JWT token字符串
     */
    public static String generateToken(Long userId) {
        return generateToken(userId, DEFAULT_EXPIRATION_MS);
    }

    /**
     * 生成JWT Token
     * @param userId 用户ID(Long类型)
     * @param expirationMs 过期时间(毫秒)
     * @return JWT token字符串
     */
    public static String generateToken(Long userId, long expirationMs) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID_CLAIM, userId); // 将Long类型用户ID放入claims

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId)) // 主题设置为用户ID的字符串形式
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从token中解析用户ID
     * @param token JWT token
     * @return 用户ID(Long类型)
     * @throws JwtException 如果token无效或过期
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        // 从claims中获取userId并转为Long
        return claims.get(USER_ID_CLAIM, Long.class);
    }

    /**
     * 解析token
     * @param token JWT token
     * @return Claims对象
     * @throws JwtException 如果token无效或过期
     */
    public static Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证token是否有效
     * @param token JWT token
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 检查token是否过期
     * @param token JWT token
     * @return 是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    /**
     * 刷新token(延长过期时间)
     * @param token 原token
     * @param expirationMs 新的过期时间(毫秒)
     * @return 新的token
     * @throws JwtException 如果原token无效
     */
    public static String refreshToken(String token, long expirationMs) {
        Long userId = getUserIdFromToken(token);
        return generateToken(userId, expirationMs);
    }

}