package com.hand.hdsp.quality;

import com.hand.hdsp.quality.infra.annotation.EnableQuality;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author aaron.yi
 */
@SpringBootApplication
@EnableQuality
public class QualityApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(QualityApplication.class, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
