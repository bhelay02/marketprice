package com.test.exercise.marketprice.service;

import com.test.exercise.marketprice.model.Price;
import com.test.exercise.marketprice.repository.MarketDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class MarketDataService {

    @Autowired
    MarketDataRepository marketDataRepository;

    public Flux<Price> findAll() {
        return marketDataRepository.findAll();
    }

    public Mono<Price> findByInstrument(String instrument) {
        return marketDataRepository.findLatestInstrument(instrument);
    }

    public Mono<Price> save(Price price) {
        return marketDataRepository
                .save(price)
                .onErrorContinue(
                        error -> error instanceof io.r2dbc.spi.R2dbcDataIntegrityViolationException,
                        (error, obj) -> log.warn("error:[{}], obj:[{}]", error, obj)
                );
    }
}
