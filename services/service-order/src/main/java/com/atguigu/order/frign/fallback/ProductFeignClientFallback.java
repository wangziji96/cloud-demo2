package com.atguigu.order.frign.fallback;

import com.atguigu.order.frign.ProductFeignClient;
import com.atguigu.product.bean.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductFeignClientFallback implements ProductFeignClient {
    @Override
    public Product getProductById(Long id) {
        System.out.println("兜底回调商品服务。。。");
        Product product = new Product();
        product.setId(id);
        product.setProductName("未知商品");
        product.setNum(0);
        product.setPrice(new BigDecimal("0"));
        return product;
    }
}
