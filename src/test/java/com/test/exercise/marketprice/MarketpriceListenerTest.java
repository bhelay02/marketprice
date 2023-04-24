package com.test.exercise.marketprice;

import com.test.exercise.marketprice.model.Price;
import com.test.exercise.marketprice.service.MarketDataService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


@SpringBootTest
public class MarketpriceListenerTest {
    @Autowired
    MarketpriceListener marketpriceListener;

    @Autowired
    MarketDataService marketDataService;


    @Test
    public void testBidCommission() {
        double bidPrice = 1000;
        double bidPriceWithCommission = 999.0;
        double actualPrice = marketpriceListener.addCommissionToBidPrice(bidPrice);
        Assertions.assertEquals(bidPriceWithCommission, actualPrice);
    }

    @Test
    public void testAskCommission() {
        double askPrice = 1000;
        double askPriceWithCommission = 1001.0;
        double actualPrice = marketpriceListener.addCommissionToAskPrice(askPrice);
        Assertions.assertEquals(askPriceWithCommission, actualPrice);
    }

    @Test
    public void testPriceWithAddedCommissionFromPriceLine() {
        String priceLine = "1000, EUR/USD, 1000,1000,01-06-2020 12:01:01:002";

        Optional<Price> price = marketpriceListener.priceLineProcess(priceLine);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS");
        LocalDateTime expectedTimestamp = LocalDateTime.parse("01-06-2020 12:01:01:002", formatter);

        Assertions.assertTrue(price.isPresent());
        Assertions.assertEquals(1000, price.get().getId());
        Assertions.assertEquals("EUR/USD", price.get().getInstrument());
        Assertions.assertEquals(999.0, price.get().getBid()); // with bid commission
        Assertions.assertEquals(1001.0, price.get().getAsk());// with ask commission
        Assertions.assertEquals(expectedTimestamp, price.get().getInstrumenttimestamp());
    }

    @Test
    public void testPriceWithAddedCommissionFromPriceLine1() {
        String priceLine = "10001, EUR/GBP, 1000,1000,01-06-2020 12:01:01:002";
        marketpriceListener.onMessage(priceLine);

        StepVerifier
                .create(marketDataService.findByInstrument("EUR/GBP"))
                .assertNext(price -> {
                    Assertions.assertEquals(10001, price.getId());
                    Assertions.assertEquals(1001.0, price.getAsk());
                    Assertions.assertEquals(999.0, price.getBid());
                });
    }

}
