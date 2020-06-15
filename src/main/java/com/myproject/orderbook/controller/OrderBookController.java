package com.myproject.orderbook.controller;

import com.myproject.orderbook.objects.domain.Execution;
import com.myproject.orderbook.objects.domain.Instrument;
import com.myproject.orderbook.objects.domain.Order;
import com.myproject.orderbook.objects.domain.OrderBook;
import com.myproject.orderbook.objects.dto.ExecutionDTO;
import com.myproject.orderbook.objects.dto.OrderDTO;
import com.myproject.orderbook.objects.dto.ResponseDTO;
import com.myproject.orderbook.objects.stats.OrderBookDetailStats;
import com.myproject.orderbook.objects.stats.OrderBookHighLevelStats;
import com.myproject.orderbook.objects.stats.OrderStats;
import com.myproject.orderbook.service.OrderBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/orderbook")
public class OrderBookController {

    @Autowired
    private OrderBookService orderBookService;

    @PostMapping(value = "/{instrumentId}/open")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO<OrderBook> openOrderBook(@PathVariable final Long instrumentId) {
        return orderBookService.open(instrumentId);
    }

    @PutMapping(value = "/{orderBookId}/order")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<String> addOrderToOrderBook(@PathVariable final Long orderBookId,
                                                   @RequestBody OrderDTO orderDTO) {
        return orderBookService.addOrder(orderBookId, new Order(orderDTO));
    }

    @PutMapping(value = "/{orderBookId}/close")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<String> closeOrderBook(@PathVariable final Long orderBookId) {
        return orderBookService.close(orderBookId);
    }

    @PutMapping(value = "/{orderBookId}/execute")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<String> addExecutionToOrderBook(@PathVariable final Long orderBookId,
                                                       @RequestBody ExecutionDTO executionDTO) {
        return orderBookService.execute(orderBookId, new Execution(executionDTO));
    }

    @GetMapping(value = "/{orderBookId}/order/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderStats getOrder(@PathVariable Long orderBookId, @PathVariable Long orderId) {
        return orderBookService.getOrder(orderBookId, orderId);
    }

    @GetMapping(value = "/get-all")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderBook> getOrderBooks() {
        return orderBookService.fetchAllOrderBooks();
    }

    @GetMapping(value = "/{orderBookId}/stats")
    @ResponseStatus(HttpStatus.OK)
    public OrderBookHighLevelStats getOrderBook(@PathVariable Long orderBookId) {
        return orderBookService.getOrderBook(orderBookId);
    }

    @GetMapping(value = "/{orderBookId}/detail-stats")
    @ResponseStatus(HttpStatus.OK)
    public OrderBookDetailStats getOrderBookWithDetails(@PathVariable Long orderBookId) {
        return orderBookService.getOrderBookWithDetails(orderBookId);
    }

    @GetMapping(value = "/fetchInstruments")
    @ResponseStatus(HttpStatus.OK)
    public List<Instrument> fetchInstruments() {
        return orderBookService.getInstruments();
    }

    @GetMapping(value = "/status")
    @ResponseStatus(HttpStatus.OK)
    public String test() {
        return "Service is up and running. ";
    }
}
