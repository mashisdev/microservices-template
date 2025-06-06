package com.microservice.service3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Service3Application {

	public static void main(String[] args) { SpringApplication.run(Service3Application.class, args); }

}
