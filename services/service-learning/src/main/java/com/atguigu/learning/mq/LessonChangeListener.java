package com.atguigu.learning.mq;

import com.atguigu.learning.config.OrderQueueConfig;
import com.atguigu.learning.service.ILearningLessonService;
import com.atguigu.order.bean.OrderBasicDTO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
@Slf4j
@Component
@RequiredArgsConstructor
public class LessonChangeListener {

    private final ILearningLessonService learningLessonService;
    @RabbitListener(queues = OrderQueueConfig.ORDER_QUEUE)
    public void handleOrderCreated(@Payload OrderBasicDTO order,
                                   Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            //1.健壮性判断
            if (order == null || order.getUserId()== null || CollectionUtils.isEmpty(order.getCourseIds())) {
                log.error("接收到的Mq消息有误，订单数据为空");
                return;
            }
            //2.添加课程
            log.info("监听到用户{}的订单{},需要添加课程{}到课表中", order.getUserId(), order.getOrderId(), order.getCourseIds());
            learningLessonService.addUserLessons(order.getUserId(), order.getCourseIds());
            // 确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 根据异常类型决定是否 requeue 或投递到死信
            try {
                // 这里采用拒绝并重新入队为 false，让消息进入死信队列
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                // 记录日志，并触发监测告警
            }
        }
    }
}
