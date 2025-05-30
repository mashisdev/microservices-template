package com.microservice.service1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service1")
public class Service1Controller {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from service 1";
    }
}
