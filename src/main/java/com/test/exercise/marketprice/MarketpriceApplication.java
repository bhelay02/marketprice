package com.test.exercise.marketprice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication
public class MarketpriceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketpriceApplication.class, args);
    }

}
