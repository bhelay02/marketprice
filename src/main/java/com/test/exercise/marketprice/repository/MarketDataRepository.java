package com.test.exercise.marketprice.repository;

import com.test.exercise.marketprice.model.Price;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MarketDataRepository extends ReactiveCrudRepository<Price, Long> {
    Flux<Price> findByInstrument(String instrument);

    Mono<Price> findById(long id);

    @Query("SELECT * FROM price p WHERE p.instrument = :instrument ORDER BY instrumenttimestamp DESC LIMIT 1")
    Mono<Price> findLatestInstrument(String instrument);
}
