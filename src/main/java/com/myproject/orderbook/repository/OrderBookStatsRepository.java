package com.myproject.orderbook.repository;

import com.myproject.orderbook.objects.stats.OrderBookStats;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderBookStatsRepository extends CrudRepository<OrderBookStats, Long> {
    Optional<OrderBookStats> findByOrderBookId(final long orderBookId);
}
