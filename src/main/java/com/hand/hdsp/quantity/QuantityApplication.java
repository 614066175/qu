package com.hand.hdsp.quantity;

import com.hand.hdsp.quantity.infra.annotation.EnableQuantity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author aaron.yi
 */
@SpringBootApplication
@EnableQuantity
public class QuantityApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuantityApplication.class, args);
    }
}