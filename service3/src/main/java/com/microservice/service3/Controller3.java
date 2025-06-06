package com.microservice.service3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service3")
public class Controller3 {

    @Autowired
    private Service3 service3;

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from service 3";
    }

    @GetMapping("/call-service1")
    public String callService1() {
        return service3.callService1();
    }
}
