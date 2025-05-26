package com.ly.gateway.filter;

import com.ly.common.constant.RedisConstant;
import com.ly.common.constant.SystemConstant;
import com.ly.common.util.JwtUtils;
import com.ly.common.util.RedisUtils;
import com.ly.common.util.ResponseUtils;
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

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private RedisUtils redisUtils;

    //白名单繁琐且变化时切换为配置
    public static final List<String> FILTER_LIST = Arrays.asList("/login", "/register","/wxLogin");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 放行白名单接口
        if (FILTER_LIST.stream().anyMatch(path::contains)) {
            return chain.filter(exchange);
        }

        // 获取Token
        String token = request.getHeaders().getFirst(SystemConstant.TOKEN);
        if (StringUtils.isEmpty(token)) {
            return ResponseUtils.forbidden(exchange);
        }

        // 校验Token是否合法
        Long userId;
        try {
            userId = JwtUtils.getUserIdFromToken(token);
        } catch (Exception e) {
            return ResponseUtils.unauthorized(exchange);
        }

        // 校验Redis中是否有缓存
        String redisKey = RedisConstant.USER_LOGIN_KEY_PREFIX + userId;
        String json = redisUtils.get(redisKey);
        if (StringUtils.isEmpty(json)) {
            return ResponseUtils.unauthorized(exchange);
        }

        // 将用户信息传递到下游服务
        ServerHttpRequest newRequest = request.mutate()
                .header(SystemConstant.USER_ID, userId.toString())
                .header(SystemConstant.TOKEN, token)
                .build();

        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        return chain.filter(newExchange);
    }
    @Override
    public int getOrder() {
        return 0;
    }
}