package com.myproject.orderbook.repository;

import com.myproject.orderbook.objects.domain.Instrument;
import com.myproject.orderbook.objects.domain.OrderBook;
import com.myproject.orderbook.objects.domain.OrderBookStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InstrumentRepository extends CrudRepository<Instrument, Long> {
}
