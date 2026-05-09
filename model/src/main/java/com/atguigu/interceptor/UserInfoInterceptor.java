package com.atguigu.interceptor;

import com.atguigu.user.LoginUser;
import com.atguigu.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

public class UserInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader("X-User-Id");
        String rolesHeader = request.getHeader("X-User-Roles");
        if (userId != null) {
            LoginUser user = new LoginUser();
            user.setUserId(userId);
            if (rolesHeader != null) {
                user.setRoles(Arrays.asList(rolesHeader.split(",")));
            }
            UserContext.setUser(user);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }
}