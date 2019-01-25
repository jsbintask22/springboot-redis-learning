package cn.jsbintask.springbootredislearning.redis;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @author jsbintask@foxmail.com
 * @date 2019/1/24 17:47
 */
@Log
@Component
public class RedisMessageReceiver {
    @Autowired
    private CountDownLatch countDownLatch;

    public void receivedMsg(String msg) {
        // 收到消息后打印
        log.info("received msg: " + msg);
        countDownLatch.countDown();
    }
}
