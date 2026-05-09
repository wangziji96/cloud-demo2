package com.atguigu.order.config;


import com.atguigu.user.LoginUser;
import com.atguigu.utils.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class FeignConfig {
    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return (RequestTemplate template) -> {
            LoginUser user = UserContext.getUser();
            if (user != null) {
                template.header("X-User-Id", user.getUserId());
                template.header("X-User-Roles", String.join(",", user.getRoles()));
            }
        };
    }
}