package com.microservice.service2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service2")
public class Controller2 {

    @Autowired
    private Service2 service2;

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from service 2";
    }

    @GetMapping("/call-service1")
    public String callService1() {
        return service2.callService1();
    }
}

