package com.atguigu.order.controller;


import com.atguigu.order.bean.Order;
import com.atguigu.order.service.OrderService;
import com.atguigu.order.properties.OrderProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@RefreshScope
@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    /*@Value("${order.timeout}")
    String orderTimeout;
    @Value("${order.auto-confirm}")
    String orderAutoConfirm;*/

    @Autowired
    OrderProperties orderProperties;

    @GetMapping("/config")
    public String config() {
        return "order.timeout:" + orderProperties.getTimeout() + "; order.auto-confirm:" + orderProperties.getAutoConfirm() + "order.db-url:" + orderProperties.getDbUrl();
    }

    //创建订单
    @GetMapping("/create")
    public Order createOrder(@RequestParam("userId") Long userId, @RequestParam("productId") Long productId) {
        return orderService.createOrder(productId, userId);
    }
}
