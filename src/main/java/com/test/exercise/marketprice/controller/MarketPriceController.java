package com.test.exercise.marketprice.controller;

import com.test.exercise.marketprice.error.PriceNotFoundException;
import com.test.exercise.marketprice.model.Price;
import com.test.exercise.marketprice.service.MarketDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class MarketPriceController {
    @Autowired
    MarketDataService marketDataService;

    @GetMapping("/prices")
    public Mono<Price> getPriceByInstrument(@RequestParam String instrument) {

        log.info("processing request for instrument : {}", instrument);
        return marketDataService.findByInstrument(instrument)
                .switchIfEmpty(Mono.error(new PriceNotFoundException("Price not found")))
                .doOnSuccess(price -> log.info("processed request for instrument : {} with latest price : {}", instrument, price.toString()))
                ;
    }

    @PostMapping("/prices")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Price> createPrice(@RequestBody Price request) {

        log.info("processing request for adding a new price : {}", request);
        Price price = new Price(request.getId(), request.getInstrument(), request.getBid(), request.getAsk(), request.getInstrumenttimestamp());
        return marketDataService.save(price);
    }
}

