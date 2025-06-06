package com.microservice.service3;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "service-1", url = "${service1.url}")
public interface Service3 {

    @GetMapping("/api/service1/hello")
    String callService1();
}
