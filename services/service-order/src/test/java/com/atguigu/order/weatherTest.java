package com.atguigu.order;

import com.atguigu.order.frign.WeatherFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class weatherTest {

    @Autowired
    private WeatherFeignClient weatherFeignClient;

    @Test
    void getWeather() {
        String weather = weatherFeignClient.getWeather(23.12911, 113.26438, "temperature_2m_max,temperature_2m_min", "Asia/Shanghai", 2);
        System.out.println(weather);
    }
}
