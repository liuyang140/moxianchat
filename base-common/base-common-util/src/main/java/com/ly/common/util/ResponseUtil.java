package com.ly.common.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.common.result.Result;
import com.ly.common.result.ResultCodeEnum;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ResponseUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static void out(HttpServletResponse response, Result<?> result) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json; charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");

        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(result));
            writer.flush();
        } catch (IOException e) {
            // 建议使用日志框架，比如 log.error("响应输出异常", e);
            System.err.println("响应输出异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Mono<Void> writeJsonResponse(ServerWebExchange exchange, Result<?> result, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = JSON.toJSONString(result);
        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    public static Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        return writeJsonResponse(exchange, Result.fail(ResultCodeEnum.PERMISSION), HttpStatus.UNAUTHORIZED);
    }

    public static Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        return writeJsonResponse(exchange, Result.fail(ResultCodeEnum.LOGIN_AUTH), HttpStatus.FORBIDDEN);
    }

    public static Mono<Void> badRequest(ServerWebExchange exchange, String message) {
        return writeJsonResponse(exchange, Result.fail(ResultCodeEnum.ILLEGAL_REQUEST), HttpStatus.BAD_REQUEST);
    }


}
