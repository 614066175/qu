package com.hand.hdsp.quality.infra.consumer;

import com.hand.hdsp.quality.infra.util.CustomThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Title: AnsjCommand
 * @Description:
 * @author: lgl
 * @date: 2022/12/5 20:33
 */
@Slf4j
@Component
public class AnsjCommand implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        log.info("Ansj加载核心词库");
        ThreadPoolExecutor executor = CustomThreadPool.getExecutor();
        executor.execute(() -> DicAnalysis.parse("加载核心词库"));
    }
}
