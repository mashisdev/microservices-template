package com.microservice.service2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class Service2 {

    @Autowired
    private RestTemplate restTemplate;

    public String callService1() {
        return restTemplate.getForObject("http://localhost:8081/api/service1/hello", String.class);
    }
}
