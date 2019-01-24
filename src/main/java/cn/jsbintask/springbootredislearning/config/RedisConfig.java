package cn.jsbintask.springbootredislearning.config;

import cn.jsbintask.springbootredislearning.redis.RedisMessageReceiver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.concurrent.CountDownLatch;

/**
 * @author jsbintask@foxmail.com
 * @date 2019/1/24 17:52
 */
@Configuration
public class RedisConfig {
    @Bean
    public CountDownLatch countDownLatch() {
        return new CountDownLatch(1);
    }

    @Bean
    MessageListenerAdapter listenerAdapter(RedisMessageReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receivedMsg");
    }

    @Bean
    StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic("chat"));

        return container;
    }
}
