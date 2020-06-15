package com.myproject.orderbook.repository;

import com.myproject.orderbook.objects.domain.OrderBook;
import com.myproject.orderbook.objects.domain.OrderBookStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderBookRepository extends CrudRepository<OrderBook, Long> {
    Optional<OrderBook> findByInstrumentIdAndStatus(final Long instrumentId, OrderBookStatus status);

    Optional<OrderBook> findByIdAndStatus(final Long id, OrderBookStatus status);
}
