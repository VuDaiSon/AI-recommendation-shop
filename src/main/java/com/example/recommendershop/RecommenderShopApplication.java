package com.example.recommendershop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RecommenderShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecommenderShopApplication.class, args);
    }

}
