package com.myproject.orderbook.helper;

import com.myproject.orderbook.objects.domain.*;
import com.myproject.orderbook.objects.dto.ExecutionDTO;
import com.myproject.orderbook.objects.dto.OrderDTO;
import com.myproject.orderbook.objects.stats.OrderBookHighLevelStats;
import com.myproject.orderbook.objects.stats.OrderBookStats;
import com.myproject.orderbook.objects.stats.OrderStats;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TestHelper {

    public static Execution getExecution(Long quantity, BigDecimal price) {
        Execution execution = new Execution();
        execution.setExecutionPrice(price);
        execution.setQuantity(quantity);
        return execution;
    }

    public static ExecutionDTO getExecutionDTO(Long quantity, BigDecimal price) {
        ExecutionDTO executionDTO = new ExecutionDTO();
        executionDTO.setExecutionPrice(price);
        executionDTO.setQuantity(quantity);
        return executionDTO;
    }

    public static OrderBook getOrderBook(Long instrumentId) {
        OrderBook orderBook = new OrderBook(instrumentId);
        orderBook.setStatus(OrderBookStatus.OPEN);
        return orderBook;
    }

    public static Order getOrder(Long instrumentId, Long quantity, LocalDate entryDate, String type) {
        return getOrder(instrumentId, quantity, entryDate, type, null);
    }

    public static OrderStats getOrderStats(Long orderId, OrderStatus status, Long executionQuantity, BigDecimal executionPrice, BigDecimal orderPrice) {
        return new OrderStats(orderId, status, executionPrice, orderPrice, executionQuantity);
    }

    private static Order getOrder(Long instrumentId, Long quantity, LocalDate entryDate, String type, BigDecimal price) {
        return new Order(getOrderDTO(instrumentId, quantity, entryDate, type, price));
    }

    public static OrderDTO getOrderDTO(Long instrumentId, Long quantity, LocalDate entryDate, String type, BigDecimal price) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setInstrumentId(instrumentId);
        orderDTO.setQuantity(quantity);
        orderDTO.setEntryDate(entryDate);
        orderDTO.setOrderType(type);
        orderDTO.setPrice(price);
        return orderDTO;
    }

    public static OrderContainer getOrderContainer(Order order) {
        return new OrderContainer(order);
    }

    public static OrderBookStats getOrderBookStats(Long orderBookId, Long bookDemand, Long accuExecQuantity, OrderBookStatus status) {
        return new OrderBookStats(orderBookId, bookDemand, accuExecQuantity, status);
    }

    public static OrderBookHighLevelStats getOrderBookHighLevelStats(OrderBookStats orderBookStats) {
        return new OrderBookHighLevelStats(orderBookStats);
    }
}
