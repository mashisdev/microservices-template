# 📦Microservices template

A ready-to-use template for building microservices architecture using **Spring Boot** and **Spring Cloud**.

- ☕ **Java 21**
- 🔀 **API Gateway** (Routing and filters)
- ⚙️ **Config Server** (Centralized configuration)
- 🧭 **Eureka Server** (Service discovery and registration)
- 🧩 Example Services (service1, service2, ...)

# 🏗️ How to add services:

<details>
  <summary>🔗 RestTemplate (Synchronous HTTP Client)</summary>
  
  ```java
    @Configuration
    public class RestTemplateConfig {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
  ```

  ```java
    @Service
    public class Service2 {

        @Autowired
        private RestTemplate restTemplate;
    
        public String getHelloFromService1() {
            return restTemplate.getForObject("http://localhost:8081/api/hello", String.class);
        }
    }
  ```
  
</details>
