本文将介绍使用redis作为消息中间件和springboot的整合使用;
## 安装配置redis
请参考笔者另一篇博客，有详细介绍：[https://jsbintask.cn/2019/01/24/middleware/redis-install/#more](https://jsbintask.cn/2019/01/24/middleware/redis-install/#more)

## 整合springboot
### 新建项目
新建一个springboot项目，并且修改application.yml文件，pom如下：
```yaml
spring:
  redis:
    host: youripaddress
    password: jsbintask
```
host和password修改成自己的服务器用户名密码。pom：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.2.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>cn.jsbintask</groupId>
    <artifactId>springboot-redis-learning</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>springboot-redis-learning</name>
    <description>Demo project for Spring Boot redis</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```
### 消息消费者（接收者）
编写一个消息消费者类：
```java
@Log
@Component
public class RedisMessageReceiver {
    @Autowired
    private CountDownLatch countDownLatch;

    public void receivedMsg(String msg) {
        log.info("received msg: " + msg);
        // 计数，减一
        countDownLatch.countDown();
    }
}
```
并且加入@Component注解，将其作为bean归spring管理，并且通过@Autowried注入了一个CountdownLatch类。
### 将消息消费者作为监听器监听 redis的消息：
```java
@Configuration
public class RedisConfig {
    public static final String MSG_TOPIC = "chat";

    @Bean
    public CountDownLatch countDownLatch() {
        return new CountDownLatch(1);
    }

    /**
     * 消息消费者 适配器，其中 receivedMsg为定义的消费者的消费方法，必须保持一致
     */
    @Bean
    MessageListenerAdapter listenerAdapter(RedisMessageReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receivedMsg");
    }

    @Bean
    StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    /**
     * 消息监听容器，将适配器加入， 注意此处的 topic
     */
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(MSG_TOPIC));

        return container;
    }
}
```
### 启动测试类，发送消息
```java
@SpringBootApplication
public class SpringbootRedisLearningApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringbootRedisLearningApplication.class, args);

        //从 spring中取出已经有的bean
        CountDownLatch countDownLatch = applicationContext.getBean(CountDownLatch.class);
        StringRedisTemplate stringRedisTemplate = applicationContext.getBean(StringRedisTemplate.class);
        
        stringRedisTemplate.convertAndSend(RedisConfig.MSG_TOPIC, "hello from jsbintask.");

        // 一直等待消息被接收，没接收不退出
        countDownLatch.await();
    }
}
```
启动，查看控制台：收到消息并且打印：
![/pass](https://raw.githubusercontent.com/jsbintask22/static/master/middleware/redis-demo5.png)
这样redis作为消息队列就成功了。
本文原创地址：[https://jsbintask.cn/2019/01/25/springboot/springboot-redis-jms/](https://jsbintask.cn/2019/01/25/springboot/springboot-redis-jms/)，未经允许，禁止转载。