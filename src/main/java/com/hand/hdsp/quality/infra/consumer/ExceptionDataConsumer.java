package com.hand.hdsp.quality.infra.consumer;

import com.hand.hdsp.quality.infra.util.CustomThreadPool;
import com.hand.hdsp.quality.infra.util.PlanExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.hzero.core.redis.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 异常数据消费着
 * </p>
 *
 * @author lgl 2022/8/31 16:32
 * @since 1.0
 */
@Component
@Slf4j
public class ExceptionDataConsumer implements CommandLineRunner {

    @Value("${hdsp.quality.message-key}")
    private String messageKey;

    @Autowired
    private RedisHelper redisHelper;

    @Value("${hdsp.quality.exception-thread-num:8}")
    //定义异常数据的固定线程数,做成服务参数，根据各个服务器性能进行配置，默认8个
    private int exceptionThreadNum;

    private AtomicInteger runningThread = new AtomicInteger(0);


    @Override
    public void run(String... args) throws Exception {
        log.info("异常数据消费线程开启");
        ThreadPoolExecutor executor = CustomThreadPool.getExecutor();
        while (true) {
            try{
                //判断有没有线程资源可以使用，有的话去阻塞获取消息
                if (runningThread.get() < exceptionThreadNum) {
                    String message = redisHelper.lstRightPop(messageKey, 0L, TimeUnit.DAYS);
                    //开启线程消费消息
                    executor.submit(() -> PlanExceptionUtil.getExceptionResult(message));
                    runningThread.incrementAndGet();
                }else {
                    //没有资源可用，休眠
                    Thread.sleep(1000);
                }
            }catch (Exception e){
                //即使此处是永久的阻塞获取，但是lettuce会导致命令超时io.lettuce.core.RedisCommandTimeoutException: Command timed out
                log.error("消费线程 error" + e.getMessage());
            }
        }

    }

    public void recycleThread(){
        //减1
        this.runningThread.decrementAndGet();
    }
}
