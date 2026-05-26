package com.atguigu.promotion.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        
        // 可选配置
        paginationInterceptor.setMaxLimit(500L);     // 单页最大限制 500 条
        paginationInterceptor.setOverflow(true);     // 溢出总页数后是否处理
        
        interceptor.addInnerInterceptor(paginationInterceptor);
        return interceptor;
    }
}