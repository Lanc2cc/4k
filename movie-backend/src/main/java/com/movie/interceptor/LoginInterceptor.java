package com.movie.interceptor;

import com.movie.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行预检请求，避免跨域场景下的无意义拦截
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头中读取令牌
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            // 未携带 token 时直接返回 401，提示未登录
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录\"}");
            return false;
        }
        try {
            // 兼容 Bearer 格式，提取真正的 token 内容
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 解析 token 中的用户身份信息，并写入请求上下文
            Long userId = JwtUtils.getUserId(token);
            request.setAttribute("userId", userId);
            request.setAttribute("token", token);
            return true;
        } catch (Exception e) {
            // token 非法或过期时返回 401，交由前端统一处理
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"登录已过期\"}");
            return false;
        }
    }
}
