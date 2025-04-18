package com.ly.gateway.filter;

import com.ly.common.constant.RedisConstant;
import com.ly.common.util.JwtUtils;
import com.ly.common.util.RedisUtils;
import com.ly.common.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 放行白名单接口（比如登录、注册、公开接口等）
        if (path.contains("/login") || path.contains("/register")) {
            return chain.filter(exchange);
        }

        // 获取Token（从请求头 Authorization 或自定义头中获取）
        String token = request.getHeaders().getFirst("token");
        if (StringUtils.isEmpty(token)) {
            return ResponseUtil.forbidden(exchange, "请先登录");
        }

        // 校验Token是否合法
        Long userId;
        try {
            userId = JwtUtils.getUserIdFromToken(token); // 假设你token里有userId
        } catch (Exception e) {
            return ResponseUtil.unauthorized(exchange, "Token非法或已过期");
        }

        // 校验Redis中是否有缓存（是否登录）
        String redisKey = RedisConstant.USER_LOGIN_KEY_PREFIX + userId;
        String json = redisUtils.get(redisKey);
        if (StringUtils.isEmpty(json)) {
            return ResponseUtil.unauthorized(exchange, "登录状态失效，请重新登录");
        }

        // 将用户信息传递到下游服务（可选）
        ServerHttpRequest newRequest = request.mutate()
                .header("userId", userId.toString())
                .header("token", token)
                .build();

        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        return chain.filter(newExchange);
    }
    @Override
    public int getOrder() {
        return 0;
    }
}