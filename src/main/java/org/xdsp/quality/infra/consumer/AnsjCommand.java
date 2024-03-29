package org.xdsp.quality.infra.consumer;

import lombok.extern.slf4j.Slf4j;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.xdsp.quality.infra.util.AnsjUtil;
import org.xdsp.quality.infra.util.CustomThreadPool;

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

    @Autowired
    private AnsjUtil ansjUtil;

    @Override
    public void run(String... args) throws Exception {
        log.info("Ansj加载核心词库");
        ThreadPoolExecutor executor = CustomThreadPool.getExecutor();
        executor.execute(() -> {
            DicAnalysis.parse("加载核心词库");
            //服务启动，先删除本地旧的词库文件
            ansjUtil.deleteDicFiles();
        });
    }
}
