# <img src="https://github.com/user-attachments/assets/5852398b-203f-49c8-ac2f-05434ded8015" alt="package gif" width="33" /> Microservices template

A ready-to-use template for building microservices architecture using **Spring Boot** and **Spring Cloud**.

- ☕ **Java 21**
- 🔀 **API Gateway** (routing and filters)
- ⚙️ **Config Server** (centralized configuration)
- 🧭 **Eureka Server** (service discovery and registration)
- 🧩 **Example Services** (to demonstrate communication between services)
  
  - service1 (that will communicate with services 2 and 3)
  - service2 ➡️ RestTemplate
  - service3 ➡️ OpenFeign
  - service4 ➡️ RabbitMQ publisher
  - service5 ➡️ RabbitMQ consumer
  - service6 ➡️ Apache Kafka

<h1>
  <img src="https://github.com/user-attachments/assets/c70a8e01-430c-41a7-817b-570ea0e12c0e" alt="plus gif" width="28" /> How to add services:
</h1>

### 🔧 Common dependencies and module set up

<details>
  <summary> Services common dependencies </summary>
  <br>

  1. Add *Eureka Discovery Client* and *Config Client* to new service dependencies on [Spring Initializr](https://start.spring.io/)

```xml
<!-- Eureka Discovery Client -->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<!-- Config Client -->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```
</details>

<details>
  <summary> <strong>pom.xml</strong> configuration (for parent & service) </summary>
  <br>
  
  2. Set the `<parent>` in your new service's `pom.xml`

```xml
<parent>
  <groupId>com.microservice</groupId>
  <artifactId>parent</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</parent>
```

  3. Add the new service as a `<module>` in the root `pom.xml`
	
```xml
<modules>
  <module>eureka</module>
  <module>config-server</module>
  <module>gateway</module>
  <module>service1</module>
		
  <module>serviceN</module> <!-- 👈 Nº microservice -->
</modules>
```

</details>

### 🔗 [RestTemplate](https://www.geeksforgeeks.org/spring-boot-rest-template/) (Synchronous HTTP Client)

<details>
  <summary> <b>Spring Web</b> dependency</summary>
  <br>

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
  
</details>

<details>
  <summary>Config Server & Client + API Gateway</summary>
  <br>
  
  1. Convert `application.properties` to `application.yml` and import the _Config Server_ (spring.application.name must match the config file name you'll create in the next step)
  
```yaml
spring:
  application:
    name: service2

  config:
    import: "optional:configserver:http://localhost:8888"
```

  2. Create a config file `service2.yml` for the service in the Config Server (`config-server/src/main/resources/config/`)
    
```yaml
server:
  port: 8082

spring:
  application:
    name: service2
```

  3. Add the service2 routes in `gateway.yml`

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

  4. Add a `@Bean` for *RestTemplate*:

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
  
  5. Service2

```java
@Service
public class Service2 {

    @Autowired
    private RestTemplate restTemplate;

    public String callService1() {
        return restTemplate.getForObject("http://localhost:8081/api/service1/hello", String.class);
    }
}
```
  
  6. Controller2

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

### ⛓️ [OpenFeign](https://medium.com/javarevisited/spring-boot-microservices-openfeign-example-with-e-commerce-574d1ef54443) (Declarative HTTP Client)

<details>
  <summary> <b>Spring Web</b> + <b>OpenFeign</b> dependencies</summary>
  <br>

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```
  
</details>

<details>
  <summary>Config Server & Client + API Gateway</summary>
  <br>
  
  1. Convert `application.properties` to `application.yml` and import the _Config Server_ (spring.application.name must match the config file name you'll create in the next step)
  
  ```yaml
  spring:
    application:
      name: service3
    
    config:
      import: "optional:configserver:http://localhost:8888"
  ```

  2. Create a config file `service3.yml` for the service in the Config Server (`config-server/src/main/resources/config/`)
    
  ```yaml
  server:
    port: 8083
    
  spring:
    application:
      name: service3

  service1:
    url: http://localhost:8081
  ```

  3. Add the service3 routes in `gateway.yml`

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
          # 👇 3º microservice
          - id: service3
            uri: http://localhost:8083
            predicates:
              - Path=/api/service3/**
  ```

</details>

<details>
  <summary> Create a <em>Service</em> interface with <em>@FeignClient</em> and its <em>Controller</em></summary>
  <br>

  4. Add `@EnableFeignClients` in the application:

```java
@SpringBootApplication
@EnableFeignClients
public class Service3Application {

	public static void main(String[] args) { SpringApplication.run(Service3Application.class, args); }

}
```

  5. Service3

```java
@FeignClient(name = "service-1", url = "${service1.url}")
public interface Service3 {

    @GetMapping("/api/service1/hello")
    String callService1();
}
```
     
  6. Controller3

```java
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
```

</details>

### <img src="https://github.com/user-attachments/assets/cfb7d2a9-88a4-4e84-9fb8-ee27a0ce1861" alt="rabbitmq gif" width="23" /> [RabbitMQ](https://rameshfadatare.medium.com/spring-boot-microservices-with-rabbitmq-example-92a38cbe08fc) (Async Message Broker)

<details>
  <summary> RabbitMQ for publisher & consumer </summary>
  <br>

  1. Add *RabbitMQ* to new services dependencies

```xml
<!-- Spring for RabbitMQ -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

  2. Create `service4.yml` and `service5.yml` files in Config Server (`config-server/src/main/resources/config/`)

```yaml
server:
  port: 8084 # or 8085

spring:
  application:
    name: service4 # or service5
  rabbitmq:
    port: '5672'
    host: localhost
    username: admin
    password: password
```

</details>

<details>
  <summary> RabbitMQ consumer </summary>
  <br>

  1. Create a *MessageProducer* class that uses RabbitTemplate to send messages to a RabbitMQ queue

```java
@Service
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);
    }
}
```

  2.  Allow clients to send a message to a RabbitMQ queue via an HTTP GET request to `/api/service4/send?message=...`

```java
@RestController
@RequestMapping("/api/service4")
public class MessageController {

    private final MessageProducer messageProducer;

    public MessageController(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @GetMapping("/send")
    public String sendMessage(@RequestParam String message) {
        messageProducer.sendMessage(message);
        return "Message sent: " + message;
    }
}
```

</details>

<details>
  <summary> RabbitMQ consumer </summary>
  <br>

  1. Create a *RabbitConfig* class to set the `@RabbitListener`

```java
@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "myQueue";

    @Bean
    public Queue exampleQueue() {
        return new Queue(QUEUE_NAME, false);
    }

    @RabbitListener(queues = QUEUE_NAME)
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}
```

</details>
