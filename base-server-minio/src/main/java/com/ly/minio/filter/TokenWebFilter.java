package com.ly.minio.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.common.result.Result;
import com.ly.common.result.ResultCodeEnum;
import com.ly.common.util.AuthContextHolder;
import com.ly.common.util.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class TokenWebFilter implements WebFilter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.contains("/login")) {
            return chain.filter(exchange); // 登录接口放行
        }

        String token = exchange.getRequest().getHeaders().getFirst("token");
        if (StringUtils.isBlank(token)) {
            return unauthorized(exchange, "请先登录");
        }

        try {
            Long userId = JwtUtils.getUserIdFromToken(token);
            AuthContextHolder.setUserId(userId);
        } catch (Exception e) {
            return unauthorized(exchange, "Token非法或已过期");
        }

        return chain.filter(exchange)
                .doFinally(signalType -> AuthContextHolder.clear()); // 响应完成后清理上下文
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Result<?> result = Result.build(null, ResultCodeEnum.PERMISSION);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsString(result).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            bytes = "{\"code\":401,\"message\":\"未授权\"}".getBytes(StandardCharsets.UTF_8);
        }

        return exchange.getResponse().writeWith(Mono.just(
                exchange.getResponse()
                        .bufferFactory()
                        .wrap(bytes)
        ));
    }
}