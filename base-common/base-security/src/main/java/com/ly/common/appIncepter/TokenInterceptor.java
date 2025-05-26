package com.ly.common.appIncepter;

import com.ly.common.util.AuthContextHolder;
import com.ly.common.util.JwtUtils;
import com.ly.common.result.Result;
import com.ly.common.result.ResultCodeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;


import java.io.PrintWriter;

public class TokenInterceptor implements HandlerInterceptor {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
         if(request.getRequestURI().contains("/login")){
            return true;
        }

        String token = request.getHeader("token");
        if (StringUtils.isBlank(token)) {
            return sendError(response, "请先登录");
        }

        try {
            Long userId = JwtUtils.getUserIdFromToken(token);
            AuthContextHolder.setUserId(userId);  // 存入当前线程
        } catch (Exception e) {
            return sendError(response, "Token非法或已过期");
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContextHolder.clear();  // 防止内存泄露
    }

    private boolean sendError(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Result<?> result = Result.build(null,ResultCodeEnum.PERMISSION);
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(result));
        writer.flush();
        writer.close();

        return false;
    }
}