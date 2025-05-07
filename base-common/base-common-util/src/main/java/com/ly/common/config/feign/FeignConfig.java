package com.ly.common.config.feign;

import com.ly.common.util.AuthContextHolder;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            // 优先从 ServletRequestAttributes 获取（说明是在 HTTP 请求线程中）
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            String token = null;

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                token = request.getHeader("token");
            }

            // 如果不是 HTTP 请求线程（比如 Netty 调用 Feign），从 AuthContextHolder 拿
            if (token == null || token.isEmpty()) {
                token = AuthContextHolder.getToken();
            }

            if (token != null && !token.isEmpty()) {
                template.header("token", token);
            }
        };
    }

}