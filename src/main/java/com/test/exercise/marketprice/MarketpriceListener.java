package com.test.exercise.marketprice;

import com.test.exercise.marketprice.model.Price;
import com.test.exercise.marketprice.service.MarketDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class MarketpriceListener {
    @Autowired
    MarketDataService marketDataService;

    @Value("${bidCommission}")
    double bidCommission;

    @Value("${askCommission}")
    double askCommission;


    public void onMessage(String message) {

        /**
         *   Assuming a single message might be made up of multiple lines
         *
         *   for e.g.
         *
         *   106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001
         *   107, EUR/JPY, 119.60,119.90,01-06-2020 12:01:02:002
         */

        Stream<String> lineMessages = new BufferedReader(new StringReader(message)).lines();

        lineMessages
                .parallel()
                .map(this::priceLineProcess)
                .filter(Optional::isPresent)
                .forEach(price -> marketDataService.save(price.get())
                        .onErrorContinue((error, response) -> log.warn(error.getMessage()))
                        .doOnSuccess(response -> log.info("successfully processed price {}", response))
                        .subscribe(System.out::println, Throwable::printStackTrace, () -> log.info("Subscriber onComplete called.")));

    }

    public Optional<Price> priceLineProcess(String priceLine) {
        try {
            String[] splittedMessage = priceLine.trim().split(",");

            if (splittedMessage.length < 4) {
                log.warn("invalid price message : " + priceLine);
            }

            return Optional.of(new Price(Integer.parseInt(splittedMessage[0].trim()), //id
                    splittedMessage[1].trim(), //instrument
                    addCommissionToBidPrice(Double.parseDouble(splittedMessage[2].trim())), //bid
                    addCommissionToAskPrice(Double.parseDouble(splittedMessage[3].trim())),  //ask
                    LocalDateTime.parse(splittedMessage[4].trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS")) //timestamp
            ));
        } catch (Exception ex) {
            log.warn("error occurred during processing price message : {} because of {}", priceLine, ex.getMessage());
        }
        return Optional.empty();
    }

    // Using double for calculations as over BigDecimal as it's about 100x slower,
    // by rounding up with double we can largely cover any errors unless it's more than 70 trillion
    // regardless it's upon project standard , if it's using BigDecimal or double that should be used
    // regardless it's upon project standard , if it's using BigDecimal or double that should be used
    public double addCommissionToAskPrice(double askPrice) {
        return roundToFourPlaces(askPrice + (askPrice * askCommission));
    }

    public double addCommissionToBidPrice(double bidPrice) {
        return roundToFourPlaces(bidPrice + (bidPrice * bidCommission));
    }

    public double roundToFourPlaces(double d) {
        //Following ( casting approach ) is on average about 3 times faster than doing it with
        // Math.round(d * 10000) / 10000.0;
        return ((long) (d < 0 ? d * 10000 - 0.5 : d * 10000 + 0.5)) / 10000.0;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void populateSampleData() {
        onMessage(
                """ 
                                106, EUR/USD, 1000,1000,01-06-2020 12:01:01:002
                                106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001
                                107, EUR/JPY, 119.60,119.90,01-06-2020 12:01:02:002
                                108, GBP/USD, 1.2500,1.2560,01-06-2020 12:01:02:002
                                109, GBP/USD, 1.2499,1.2561,01-06-2020 12:01:02:100
                                110, EUR/JPY, 119.61,119.91,01-06-2020 12:01:02:110
                        """
        );
    }
}
