package com.myproject.orderbook.service;

import com.myproject.orderbook.exception.*;
import com.myproject.orderbook.objects.ResponseMessage;
import com.myproject.orderbook.objects.domain.*;
import com.myproject.orderbook.objects.dto.ResponseDTO;
import com.myproject.orderbook.objects.stats.*;
import com.myproject.orderbook.repository.InstrumentRepository;
import com.myproject.orderbook.repository.OrderBookRepository;
import com.myproject.orderbook.repository.OrderBookStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class OrderBookServiceImpl implements OrderBookService {
    @Autowired
    private OrderBookRepository orderBookRepository;

    @Autowired
    private OrderBookStatsRepository orderBookStatsRepository;

    @Autowired
    private InstrumentRepository instrumentRepository;

    private final static Long MIN_QUANTITY = 0L;

    @Override
    public ResponseDTO<OrderBook> open(final Long instrumentId) {
        Optional<OrderBook> optionalOrderBook = orderBookRepository.findByInstrumentIdAndStatus(instrumentId, OrderBookStatus.OPEN);
        if (optionalOrderBook.isPresent())
            return new ResponseDTO<>(optionalOrderBook.get(), ResponseMessage.ORDER_BOOK_ALREADY_OPENED.getMessage(instrumentId));
        Instrument instrument = instrumentRepository.findById(instrumentId).get();
        OrderBook orderBook = new OrderBook(instrumentId, instrument.getName());
        orderBook.setStatus(OrderBookStatus.OPEN);
        orderBookRepository.save(orderBook);
        return new ResponseDTO<>(orderBook, ResponseMessage.ORDER_BOOK_OPENED.getMessage(instrumentId));
    }

    @Override
    public List<OrderBook> fetchAllOrderBooks() {
        List<OrderBook> orderBooks = new ArrayList<>();
        orderBookRepository.findAll().forEach(orderBooks::add);
        return  orderBooks;
    }

    @Override
    public ResponseDTO<String> close(final Long orderBookId) {
        OrderBook orderBook = orderBookRepository.findByIdAndStatus(orderBookId, OrderBookStatus.OPEN)
                .orElseThrow(() -> new OrderBookNotFoundException(ResponseMessage.ORDER_BOOK_NOT_FOUND_FOR_CLOSED.getMessage()));
        orderBook.setStatus(OrderBookStatus.CLOSED);
        orderBookRepository.save(orderBook);
        return new ResponseDTO<>(ResponseMessage.ORDER_BOOK_CLOSED.getMessage());
    }

    @Override
    public ResponseDTO<String> addOrder(final Long orderBookId, final Order order) {
        OrderBook orderBook = orderBookRepository.findByIdAndStatus(orderBookId, OrderBookStatus.OPEN)
                .orElseThrow(() -> new OrderBookNotFoundException(ResponseMessage.ORDER_BOOK_NOT_FOUND_FOR_ADD_ORDER.getMessage()));
        if (order.getOrderType() == null)
            throw new InvalidOrderTypeException(ResponseMessage.INVALID_ORDER_TYPE.getMessage());
        if (order.getOrderType() == OrderType.LIMIT && (order.getPrice() == null || order.getPrice().compareTo(BigDecimal.ZERO) == 0))
            throw new OrderNotAddedException(ResponseMessage.LIMIT_ORDER_PRICE_VALIDATION_FAILED.getMessage());
        OrderContainer orderContainer = new OrderContainer(order);
        orderContainer.setOrderBook(orderBook);
        orderBook.getOrders().add(orderContainer);
        calculateBookDemand(orderBook);
        orderBookRepository.save(orderBook);
        return new ResponseDTO<>(ResponseMessage.ORDER_ADDED.getMessage());
    }

    @Override
    public ResponseDTO<String> execute(final Long orderBookId, final Execution execution) {
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new OrderBookNotFoundException(ResponseMessage.ORDER_BOOK_NOT_FOUND_FOR_EXECUTION.getMessage()));
        if (execution != null && (execution.getQuantity() == null || execution.getQuantity() <= 0))
            throw new ExecutionNotAddedException(ResponseMessage.EXECUTION_QUANTITY_NOT_VALID.getMessage());
        if (orderBook.getStatus() == OrderBookStatus.OPEN)
            throw new ExecutionNotAddedException(ResponseMessage.EXECUTION_NOT_ADDED_AS_BOOK_NOT_CLOSED.getMessage());
        if (orderBook.getStatus() == OrderBookStatus.EXECUTED)
            throw new ExecutionNotAddedException(ResponseMessage.EXECUTION_NOT_ADDED_AS_BOOK_ALREADY_EXECUTED.getMessage());
        markValidInvalidOrders(orderBook, execution);
        calculateBookDemand(orderBook);
        Double ratio = execution.getQuantity().doubleValue() / orderBook.getBookDemand().doubleValue();
        if (orderBook.getBookDemand() < execution.getQuantity())
            executeOrder(execution.getQuantity(), orderBook);
        else
            executeOrder(execution.getQuantity(), ratio, orderBook);
        updateOrderBookStatus(orderBook);
        execution.setOrderBook(orderBook);
        orderBook.getExecutions().add(execution);
        orderBookRepository.save(orderBook);
        return new ResponseDTO<>(ResponseMessage.ORDER_BOOK_EXECUTED.getMessage(orderBook.getStatus(), orderBookId));
    }

    @Override
    public OrderStats getOrder(final Long orderBookId, final Long orderId) {
        OrderBook orderBook = orderBookRepository.findById(orderBookId)
                .orElseThrow(() -> new OrderBookNotFoundException(ResponseMessage.ORDER_BOOK_NOT_FOUND.getMessage()));
        OrderContainer orderContainer = orderBook.getOrders().stream().filter(order -> Objects.equals(order.getOrder().getId(), orderId))
                .findAny().orElseThrow(() -> new OrderNotFoundException(ResponseMessage.ORDER_NOT_FOUND.getMessage()));
        return new OrderStats(orderContainer.getOrder().getId(), orderContainer.getOrderStatus(),
                orderContainer.getExecutionPrice(), orderContainer.getOrder().getPrice(), orderContainer.getQuantity());
    }

    @Override
    public OrderBookHighLevelStats getOrderBook(final Long orderBookId) {
        OrderBookStats orderBookStats = getStats(orderBookId);
        return new OrderBookHighLevelStats(orderBookStats);
    }

    @Override
    public OrderBookDetailStats getOrderBookWithDetails(final Long orderBookId) {
        OrderBookStats orderBookStats = getStats(orderBookId);
        return new OrderBookDetailStats(orderBookStats);
    }

    @Override
    public List<Instrument> getInstruments() {
        List<Instrument> newInstruments = new ArrayList<>();
        instrumentRepository.findAll().forEach(newInstruments::add);
        if (!newInstruments.isEmpty()) return newInstruments;
        List<Instrument> instruments = new ArrayList<Instrument>() {{
            add(new Instrument("HCL Tech"));
            add(new Instrument("Infosys Ltd."));
            add(new Instrument("Commodity - Gold"));
        }};
        instrumentRepository.saveAll(instruments).forEach(newInstruments::add);
        return newInstruments;
    }

    /**
     * Update order book status based on the book demand. if book demand is greater than zero then order book is not fully executed.
     *
     * @param orderBook Order Book
     */
    private void updateOrderBookStatus(OrderBook orderBook) {
        if (orderBook.getBookDemand() == 0L)
            orderBook.setStatus(OrderBookStatus.EXECUTED);
        else
            orderBook.setStatus(OrderBookStatus.PARTIAL_EXECUTED);
    }

    /**
     * Marks Valid and Invalid orders in order book based on the execution price.
     *
     * @param orderBook Order book
     * @param execution Execution
     */
    private void markValidInvalidOrders(OrderBook orderBook, Execution execution) {
        if (orderBook.getStatus() == OrderBookStatus.CLOSED) {
            orderBook.getOrders().forEach(orderContainer -> {
                if (orderContainer.getOrder().getOrderType() == OrderType.LIMIT &&
                        orderContainer.getOrder().getPrice().compareTo(execution.getExecutionPrice()) < 0) {
                    orderContainer.setOrderStatus(OrderStatus.INVALID);
                    orderContainer.setExecutedQuantity(0L);
                } else {
                    orderContainer.setOrderStatus(OrderStatus.VALID);
                    orderContainer.setExecutionPrice(execution.getExecutionPrice());
                }
            });
        }
    }

    /**
     * Calculates the book demand for the order book.
     *
     * @param orderBook OrderBook
     */
    private void calculateBookDemand(OrderBook orderBook) {
        orderBook.setBookDemand(orderBook.getOrders().stream().filter(orderContainer ->
                orderContainer.getOrderStatus() == OrderStatus.VALID).mapToLong(OrderContainer::getQuantity).sum());
    }

    /**
     * Execute order book
     *
     * @param originalQuantity Execution quantity.
     * @param orderBook        Order book
     */
    private void executeOrder(Long originalQuantity, OrderBook orderBook) {
        executeOrder(originalQuantity, null, orderBook);
    }

    /**
     * Execute order book. Execution can be based on two condition:-
     * <ui>
     * <li>When book demand is less than to the execution quantity.</li>
     * <p> Distribute execution quantity to each order in sequential manner (FCFS basis).
     * Dont know if it is not valid scenario and what do with remaining execution quantity here ??? </p>
     * <li>When book demand is equal to or grater than execution quantity.</li>
     * <p> Linear distribution will be applied. we need to take out the ration b/w book demand and execution quantity and multiply with order
     * quantity and distribute the outcome quantity to each order in the order book.</p>
     * </ui>
     *
     * @param originalQuantity Execution quantity
     * @param ratio            ration B/W book demand and execution quantity
     * @param orderBook        Order book
     */
    private void executeOrder(Long originalQuantity, Double ratio, OrderBook orderBook) {
        BigDecimal bigDecimal;
        Long quantityToBeAllocated;

        for (OrderContainer orderContainer : orderBook.getOrders()) {
            if (originalQuantity <= MIN_QUANTITY)
                break;
            Long remainingQuantity = orderContainer.getQuantity();
            if (orderContainer.getOrderStatus() == OrderStatus.VALID && orderContainer.getQuantity() > 0) {
                if (ratio == null) {
                    quantityToBeAllocated = Math.min(originalQuantity, remainingQuantity);
                } else {
                    bigDecimal = new BigDecimal(remainingQuantity);
                    quantityToBeAllocated = bigDecimal.multiply(BigDecimal.valueOf(ratio))
                            .setScale(0, RoundingMode.HALF_UP).longValue();
                    quantityToBeAllocated = Math.min(originalQuantity, quantityToBeAllocated);
                }
                orderContainer.reduceQuantity(quantityToBeAllocated);
                originalQuantity -= quantityToBeAllocated;
                orderContainer.updateExecutionQuantity(quantityToBeAllocated);
                orderBook.updateBookDemand(quantityToBeAllocated);
                orderBook.updateAccumulatedExecQuantity(quantityToBeAllocated);
            }
        }
        if (originalQuantity > MIN_QUANTITY && orderBook.getBookDemand() > MIN_QUANTITY) {
            sortReverseByOrderQuantity(orderBook.getOrders());
            executeOrder(originalQuantity, null, orderBook);
        }
    }

    private OrderBookStats getStats(Long orderBookId) {
        OrderBookStats orderBookStats;
        Optional<OrderBookStats> orderBookStatsById = orderBookStatsRepository.findByOrderBookId(orderBookId);
        if (orderBookStatsById.isPresent())
            orderBookStats = orderBookStatsById.get();
        else {
            OrderBook orderBook = orderBookRepository.findById(orderBookId).orElseThrow(() ->
                    new OrderBookNotFoundException(ResponseMessage.ORDER_BOOK_NOT_FOUND.getMessage()));
            orderBookStats = new OrderBookStats(orderBook.getId(), orderBook.getBookDemand(),
                    orderBook.getAccumulatedExecQuantity(), orderBook.getStatus());
            List<OrderContainer> orders = orderBook.getOrders();
            if (orders != null && orders.size() > 0) {
                orderBookStats.setTotalOrders((long) orders.size());
                if (orderBook.getStatus() == OrderBookStatus.EXECUTED || orderBook.getStatus() == OrderBookStatus.PARTIAL_EXECUTED) {
                    orderBookStats.setExecutionPrice(orders.get(0).getExecutionPrice());

                    // fetching valid and invalid number of orders
                    Map<OrderStatus, Long> validInvalidMap = orders.stream().collect(Collectors.groupingBy(
                            OrderContainer::getOrderStatus, Collectors.counting()
                    ));
                    orderBookStats.setValidOrders(null != validInvalidMap && validInvalidMap.containsKey(OrderStatus.VALID)
                            ? validInvalidMap.get(OrderStatus.VALID) : 0L);
                    orderBookStats.setInvalidOrders(null != validInvalidMap && validInvalidMap.containsKey(OrderStatus.INVALID)
                            ? validInvalidMap.get(OrderStatus.INVALID) : 0L);
                }

                // fetching smallest and biggest order's quantity
                sortByOrderQuantity(orders);
                orderBookStats.setSmallestOrder(orders.get(0).getOrder().getQuantity());
                orderBookStats.setBiggestOrder(orders.get(orders.size() - 1).getOrder().getQuantity());

                // fetching first and last order's quantity
                sortByEntryDate(orders);
                orderBookStats.setFirstOrderEntryDate(orders.get(0).getOrder().getEntryDate());
                orderBookStats.setLastOrderEntryDate(orders.get(orders.size() - 1).getOrder().getEntryDate());

                // fetching limit break down
                orderBookStats.setLimitBreakDownStatsList(fetchLimitBreakDown(orders, orderBookStats));
            }
            if (orderBook.getStatus() == OrderBookStatus.EXECUTED)
                orderBookStatsRepository.save(orderBookStats);
        }
        return orderBookStats;
    }

    private List<LimitBreakDownStats> fetchLimitBreakDown(List<OrderContainer> orders,
                                                          final OrderBookStats orderBookStats) {
        return orders.stream().filter(orderContainer ->
                orderContainer.getOrder().getOrderType() == OrderType.LIMIT).map(orderContainer -> {
            LimitBreakDownStats limitBreakDownStats = new LimitBreakDownStats(orderBookStats);
            limitBreakDownStats.setLimitQuantity(orderContainer.getOrder().getQuantity());
            limitBreakDownStats.setLimitPrice(orderContainer.getOrder().getPrice());
            return limitBreakDownStats;
        }).collect(Collectors.toList());
    }

    private void sortByEntryDate(List<OrderContainer> orders) {
        Comparator<OrderContainer> entryDateComparator = Comparator.comparing(orderContainer ->
                orderContainer.getOrder().getEntryDate());
        orders.sort(entryDateComparator);
    }

    private void sortByOrderQuantity(List<OrderContainer> orders) {
        Comparator<OrderContainer> quantityComparator = Comparator.comparingLong(orderContainer ->
                orderContainer.getOrder().getQuantity());
        orders.sort(quantityComparator);
    }

    private void sortReverseByOrderQuantity(List<OrderContainer> orders) {
        Comparator<OrderContainer> quantityComparator = Comparator.comparingLong((OrderContainer orderContainer) ->
                orderContainer.getOrder().getQuantity()).reversed();
        orders.sort(quantityComparator);
    }
}
