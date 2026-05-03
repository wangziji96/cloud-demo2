package com.atguigu.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

import java.net.URI;


@SpringBootTest
public class LoadBalancerTest {

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Test
    void test() {
        URI uri = loadBalancerClient.choose("service-product").getUri();
        System.out.println("choose = " + uri.getHost() + ": " + uri.getPort());
        uri = loadBalancerClient.choose("service-product").getUri();
        System.out.println("choose = " + uri.getHost() + ": " + uri.getPort());
        uri = loadBalancerClient.choose("service-product").getUri();
        System.out.println("choose = " + uri.getHost() + ": " + uri.getPort());
        uri = loadBalancerClient.choose("service-product").getUri();
        System.out.println("choose = " + uri.getHost() + ": " + uri.getPort());
        uri = loadBalancerClient.choose("service-product").getUri();
        System.out.println("choose = " + uri.getHost() + ": " + uri.getPort());

    }
}
