package com.atguigu.order.controller;


import com.atguigu.order.bean.Order;
import com.atguigu.order.service.OrderService;
import com.atguigu.order.properties.OrderProperties;
import com.atguigu.user.LoginUser;
import com.atguigu.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@RefreshScope
//@RequestMapping("/api/order")
@Slf4j
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
