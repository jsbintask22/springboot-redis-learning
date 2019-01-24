package cn.jsbintask.springbootredislearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class SpringbootRedisLearningApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringbootRedisLearningApplication.class, args);
        CountDownLatch countDownLatch = applicationContext.getBean(CountDownLatch.class);

        StringRedisTemplate stringRedisTemplate = applicationContext.getBean(StringRedisTemplate.class);

        stringRedisTemplate.convertAndSend("chat", "hello from jsbintask.");

        countDownLatch.await();
    }

}

