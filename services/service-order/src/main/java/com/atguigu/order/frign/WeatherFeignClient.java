package com.atguigu.order.frign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weather-service", url = "https://api.open-meteo.com/")
public interface WeatherFeignClient {

    @GetMapping("v1/forecast")
    String getWeather(@RequestParam("latitude") Double latitude,
                      @RequestParam("longitude") Double longitude,
                      @RequestParam("daily") String daily,
                      @RequestParam("timezone") String timezone,
                      @RequestParam("forecast_days") Integer forecastDays);
}
