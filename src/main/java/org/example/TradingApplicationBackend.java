package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class TradingApplicationBackend {
    public static void main(String[] args) {
        SpringApplication.run(TradingApplicationBackend.class, args);
    }
}