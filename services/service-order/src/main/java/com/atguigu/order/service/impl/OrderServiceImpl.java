package com.atguigu.order.service.impl;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.atguigu.order.bean.Order;
import com.atguigu.order.frign.ProductFeignClient;
import com.atguigu.order.service.OrderService;
import com.atguigu.product.bean.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    RestTemplate restTemplate;

    /*@Autowired
    LoadBalancerClient loadBalancerClient;*/

    @Autowired
    ProductFeignClient productFeignClient;

    @SentinelResource(value = "createOrder",blockHandler = "createOrderFallback")
    @Override
    public Order createOrder(Long productId, Long userId) {
//        Product product = getProductFromRemote(productId);
        //Product product = getProductFromRemoteWithLoadBalancer(productId);
        Product product = productFeignClient.getProductById(productId);
        Order order = new Order();
        order.setId(1L);
        order.setUserId(userId);
        order.setNickName("张三");
        order.setAddress("北京");
        //总金额
        order.setTotalAmount(product.getPrice().multiply(new BigDecimal(product.getNum())));
        //todo 远程商品列表
        order.setProductList(Arrays.asList(product));
        return order;
    }

    public Order createOrderFallback(Long productId, Long userId, BlockException e) {
        Order order = new Order();
        order.setId(0L);
        order.setUserId(userId);
        order.setNickName("未知用户");
        order.setAddress("异常信息：" + e.getClass());
        order.setTotalAmount(new BigDecimal(0));
        //order.setProductList(null);
        return order;
    }

    private Product getProductFromRemoteWithLoadBalancer(Long productId) {
        //1.获取商品服务所在的所有机器IP+port
        String url = "http://service-product/product/" + productId;
        log.info("远程请求:{}", url);

        //2.给远程发送请求
        Product product = restTemplate.getForObject(url, Product.class);
        return product;
    }
    private Product getProductFromRemote(Long productId) {
        //1.获取商品服务所在的所有机器IP+port
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
        ServiceInstance instance = instances.get(0);
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/product/" + productId;
        log.info("远程请求:{}", url);

        //2.给远程发送请求
        Product product = restTemplate.getForObject(url, Product.class);
        return product;
    }
}
