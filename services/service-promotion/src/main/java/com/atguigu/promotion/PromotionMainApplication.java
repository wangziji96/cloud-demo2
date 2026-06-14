package com.atguigu.promotion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableAsync
@SpringBootApplication
public class PromotionMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(PromotionMainApplication.class, args);
    }
}
