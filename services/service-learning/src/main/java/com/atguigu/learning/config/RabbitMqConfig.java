package com.atguigu.learning.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class RabbitMqConfig {

    // 注入连接工厂，微调缓存
    @Bean
    public CachingConnectionFactory rabbitConnectionFactory(
            @Value("${spring.rabbitmq.addresses}") String addresses,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password,
            @Value("${spring.rabbitmq.virtual-host}") String vhost) {

        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setAddresses(addresses);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(vhost);

        // 连接缓存模式：默认 CHANNEL，每个连接可缓存多个 Channel
        // 设置每个连接最大信道数，避免过度创建
        factory.setChannelCacheSize(50);
        // 最大连接数（连接池大小，防止把 Broker 连接占满）
        factory.setConnectionLimit(20);
        return factory;
    }

    // 使用 Jackson2JsonMessageConverter 替代默认的 SimpleMessageConverter（JDK 序列化）
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 为 RabbitTemplate 配置消息转换器、确认回调、退回回调
    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);

        // 发布确认回调：记录成功或失败日志，失败可做报警/落库
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                // 重要：将失败消息进行持久化或告警
                log.error("消息发送到交换机失败: id={}, cause={}",
                        correlationData != null ? correlationData.getId() : "null", cause);
            }
        });

        // 退回回调：消息未路由到任何队列
        template.setReturnsCallback(returned -> {
            log.error("消息被退回: exchange={}, routingKey={}, replyCode={}, replyText={}, body={}",
                    returned.getExchange(), returned.getRoutingKey(),
                    returned.getReplyCode(), returned.getReplyText(),
                    new String(returned.getMessage().getBody(), StandardCharsets.UTF_8));
        });

        return template;
    }
}