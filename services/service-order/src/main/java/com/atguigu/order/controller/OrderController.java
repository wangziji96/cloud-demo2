package com.atguigu.order.controller;


import com.atguigu.order.bean.Order;
import com.atguigu.order.bean.OrderBasicDTO;
import com.atguigu.order.mq.OrderMessageProducer;
import com.atguigu.order.service.OrderService;
import com.atguigu.order.properties.OrderProperties;
import com.atguigu.result.Result;
import com.atguigu.user.LoginUser;
import com.atguigu.utils.UserContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//@RefreshScope
//@RequestMapping("/api/order")
@Slf4j
@RestController
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMessageProducer orderMessageProducer;

    /*@Value("${order.timeout}")
    String orderTimeout;
    @Value("${order.auto-confirm}")
    String orderAutoConfirm;*/

    @Autowired
    OrderProperties orderProperties;

    @PostMapping("/orderMq")
    public Result orderMq(@RequestBody OrderBasicDTO  order) {
        orderMessageProducer.sendOrderCreated(order);
        return Result.success();
    }

    @GetMapping("/config")
    public String config() {
        return "order.timeout:" + orderProperties.getTimeout() + "; order.auto-confirm:" + orderProperties.getAutoConfirm() + "order.db-url:" + orderProperties.getDbUrl();
    }

    //创建订单
    @GetMapping("/create")
    public Order createOrder(@RequestParam("userId") Long userId, @RequestParam("productId") Long productId) {
        return orderService.createOrder(productId, userId);
    }

    @GetMapping("/seckill")
    public Order seckill(@RequestParam("userId") Long userId, @RequestParam("productId") Long productId) {
        Order order = orderService.createOrder(productId, userId);
        order.setId(Long.MAX_VALUE);
        return order;
    }

    @GetMapping("/writeDb")
    public String writeDb() {
        LoginUser user = UserContext.getUser();

        return "writeDb success..." + user.getUserId() + ":" + user.getRoles();
    }

    @GetMapping("/readDb")
    public String readDb() {
        log.info("readDb....");
        return "readDb success...";
    }
}
