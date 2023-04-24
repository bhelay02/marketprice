package com.test.exercise.marketprice;

import com.test.exercise.marketprice.service.MarketDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class MarketpriceControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MarketDataService marketDataService;

    @Autowired
    MarketpriceListener marketpriceListener;

    @Test
    public void testGetLatestPrice() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/prices")
                        .queryParam("instrument", "EUR/USD")
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(106)
                .jsonPath("$.instrument").isEqualTo("EUR/USD")
                .jsonPath("$.bid").isEqualTo(999.0)
                .jsonPath("$.ask").isEqualTo(1001.0)
                .jsonPath("$.instrumenttimestamp").isEqualTo("01-06-2020 12:01:01.002")
        ;

    }
}
