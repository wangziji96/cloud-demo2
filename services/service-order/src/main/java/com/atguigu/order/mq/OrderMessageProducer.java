package com.atguigu.order.mq;

import com.atguigu.order.bean.OrderBasicDTO;
import com.atguigu.order.config.OrderQueueConfig;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public OrderMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrderCreated(OrderBasicDTO order) {
        // 每条消息追加唯一ID，用于确认回调追踪
        CorrelationData correlationData = new CorrelationData(order.getOrderId().toString());
        // 可在此设置返回的 MessagePostProcessor，添加自定义 header
        rabbitTemplate.convertAndSend(
                OrderQueueConfig.ORDER_EXCHANGE,
                "order.pay",
                order,
                correlationData
        );
    }
}