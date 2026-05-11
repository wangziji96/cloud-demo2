package com.atguigu.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderQueueConfig {

    public static final String ORDER_EXCHANGE = "order.topic";
    public static final String ORDER_QUEUE = "learning.lesson.pay.queue";
    public static final String ORDER_DLX_EXCHANGE = "order.dlx.exchange";
    public static final String ORDER_DLX_QUEUE = "order.dlx.queue";

    // 正常业务交换机
    @Bean
    public DirectExchange orderExchange() {
        return ExchangeBuilder.directExchange(ORDER_EXCHANGE).durable(true).build();
    }

    // 死信交换机
    @Bean
    public DirectExchange orderDlxExchange() {
        return ExchangeBuilder.directExchange(ORDER_DLX_EXCHANGE).durable(true).build();
    }

    // 死信队列（真正消费死信）
    @Bean
    public Queue orderDlxQueue() {
        return QueueBuilder.durable(ORDER_DLX_QUEUE).build();
    }

    // 死信队列绑定
    @Bean
    public Binding orderDlxBinding() {
        return BindingBuilder.bind(orderDlxQueue())
                .to(orderDlxExchange())
                .with("order.dlx");
    }

    // 业务队列，绑定死信交换器
    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "order.dlx")
                .build();
    }

    // 业务队列绑定
    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue())
                .to(orderExchange())
                .with("order.pay");
    }
}