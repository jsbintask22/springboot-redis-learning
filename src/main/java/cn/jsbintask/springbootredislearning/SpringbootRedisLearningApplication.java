package cn.jsbintask.springbootredislearning;

import cn.jsbintask.springbootredislearning.config.RedisConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CountDownLatch;

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

