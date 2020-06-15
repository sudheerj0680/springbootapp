package com.myproject.orderbook.service;

import com.myproject.orderbook.objects.domain.Execution;
import com.myproject.orderbook.objects.domain.Instrument;
import com.myproject.orderbook.objects.domain.Order;
import com.myproject.orderbook.objects.domain.OrderBook;
import com.myproject.orderbook.objects.dto.ResponseDTO;
import com.myproject.orderbook.objects.stats.OrderBookDetailStats;
import com.myproject.orderbook.objects.stats.OrderBookHighLevelStats;
import com.myproject.orderbook.objects.stats.OrderStats;

import java.util.List;

public interface OrderBookService {

    ResponseDTO<OrderBook> open(final Long instrumentId);

    ResponseDTO<String> addOrder(final Long orderBookId, final Order order);

    ResponseDTO<String> close(final Long orderBookId);

    ResponseDTO<String> execute(final Long orderBookId, final Execution execution);

    OrderStats getOrder(final Long orderBookId, final Long orderId);

    OrderBookHighLevelStats getOrderBook(final Long orderBookId);

    OrderBookDetailStats getOrderBookWithDetails(final Long orderBookId);

    List<Instrument> getInstruments();

    List<OrderBook> fetchAllOrderBooks();
}
