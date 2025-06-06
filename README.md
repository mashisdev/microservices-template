# 📦Microservices template

A ready-to-use template for building microservices architecture using **Spring Boot** and **Spring Cloud**.

- ☕ **Java 21**
- 🔀 **API Gateway** (Routing and filters)
- ⚙️ **Config Server** (Centralized configuration)
- 🧭 **Eureka Server** (Service discovery and registration)
- 🧩 Example Services (service1, service2, ...)

<h1>
  <img src="https://github.com/user-attachments/assets/c70a8e01-430c-41a7-817b-570ea0e12c0e" alt="plus gif" width="28" /> How to add services:
</h1>

### 🔗 [RestTemplate](https://www.geeksforgeeks.org/spring-boot-rest-template/) (Synchronous HTTP Client)

<details>
  <summary>Dependencies & pom.xml module</summary>
  <br>
  
  1. Set the `<parent>` in your new service's `pom.xml`
  
  ```xml
    <parent>
      <groupId>com.microservice</groupId>
      <artifactId>parent</artifactId>
      <version>0.0.1-SNAPSHOT</version>
    </parent>
  ```

  2. Add the new service as a `<module>` in the root `pom.xml`
    
  ```xml
    <modules>
        <module>eureka</module>
        <module>config-server</module>
        <module>gateway</module>
        <module>service1</module>
        
        <module>service2</module> <!-- 👈 2º microservice -->
    </modules>
  ```

</details>

<details>
  <summary>Config server & client + API Gateway</summary>
  <br>
  
  3. Convert application.properties to `application.yml` and import the _Config Server_ (spring.application.name must match the config file name you'll create in the next step)
  
  ```yaml
    spring:
      application:
        name: service2
    
      config:
        import: "optional:configserver:http://localhost:8888"
  ```

  4. Create a config file for the service in the Config Server (`config-server/src/main/resources/config/`)
    
  ```yaml
    server:
      port: 8082
    
    spring:
      application:
        name: service2
  ```

  5. Add the service2 routes in `gateway.yml`

  ```yaml
server:
  port: 8080

spring:
  application:
    name: gateway
  cloud:
    gateway:
      routes:
        - id: service1
          uri: http://localhost:8081
          predicates:
            - Path=/api/service1/**

        # 👇 2º microservice
        - id: service2
          uri: http://localhost:8082
          predicates:
            - Path=/api/service2/**
  ```

</details>


<details>
  <summary> <em>RestTemplateConfig</em> </summary>
  <br>

  6. Add a `@Bean` for *RestTemplate*:

  ```java
    @Configuration
    public class RestTemplateConfig {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
  ```
</details>
  
<details>
  <summary><em>Service</em> & <em>Controller</em></summary>
  <br>
  
  7. Service2

  ```java
    @Service
    public class Service2 {

        @Autowired
        private RestTemplate restTemplate;
    
        public String getHelloFromService1() {
            return restTemplate.getForObject("http://localhost:8081/api/service1/hello", String.class);
        }
    }
  ```
  
  8. Controller2

  ```java
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
  ```

</details>
