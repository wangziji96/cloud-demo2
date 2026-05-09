package com.atguigu.gateway.filter;

import com.atguigu.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    // 白名单路径（不需要携带Token）
    private static final List<String> WHITE_LIST = Arrays.asList("/auth/login", "/auth/register");
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        //1.白名单直接放行
        if (WHITE_LIST.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }
        //2.获取 Token
        String authToken = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authToken == null || !authToken.startsWith("Bearer ")) {
            return unauthorized(exchange, "用户未登录");
        }
        String token = authToken.substring(7);
        try {
            //3.验证 Token
            Claims claims = JwtUtil.parseToken(token);
            String jti = claims.getId();
            String userId = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);
            //4.todo 利用Redis检查黑名单。等退出登录功能完成后，再做
            //5.移除客户端可能伪造的内部头，放入真实用户信息
            ServerWebExchange newExchange = exchange.mutate()
                    .request(builder -> builder
                            .headers(headers -> {
                                headers.remove("X-User-Id");
                                headers.remove("X-User-Roles");
                            })
                            .header("X-User-Id", userId)
                            .header("X-User-Roles", String.join(",", roles))
                    ).build();

            return chain.filter(newExchange);
        } catch (Exception e) {
            return unauthorized(exchange, "Token无效或已过期");
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = String.format("{\"code\":401,\"message\":\"%s\"}", message);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
    @Override
    public int getOrder() {
        return -100;
    }
}
