package org.xdsp.quality;

import org.hzero.boot.admin.bus.publisher.annotation.EnableNoticePublisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.xdsp.quality.infra.annotation.EnableQuality;

/**
 * @author aaron.yi
 */
@SpringBootApplication
@EnableQuality
@ComponentScan("org.xdsp.*")
@EnableNoticePublisher(publisherPackages = "org.xdsp")
public class QualityApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(QualityApplication.class, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
