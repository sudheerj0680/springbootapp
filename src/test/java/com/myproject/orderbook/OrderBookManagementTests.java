package com.myproject.orderbook;

import com.myproject.orderbook.exception.ExecutionNotAddedException;
import com.myproject.orderbook.exception.InvalidOrderTypeException;
import com.myproject.orderbook.exception.OrderBookNotFoundException;
import com.myproject.orderbook.helper.TestHelper;
import com.myproject.orderbook.objects.ResponseMessage;
import com.myproject.orderbook.objects.domain.*;
import com.myproject.orderbook.objects.dto.ResponseDTO;
import com.myproject.orderbook.objects.stats.OrderBookHighLevelStats;
import com.myproject.orderbook.objects.stats.OrderStats;
import com.myproject.orderbook.repository.InstrumentRepository;
import com.myproject.orderbook.repository.OrderBookRepository;
import com.myproject.orderbook.repository.OrderBookStatsRepository;
import com.myproject.orderbook.service.OrderBookService;
import com.myproject.orderbook.service.OrderBookServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderBookManagementTests {

    @MockBean
    OrderBookRepository orderBookRepository;

    @MockBean
    InstrumentRepository instrumentRepository;

    @MockBean
    OrderBookStatsRepository orderBookStatsRepository;

    @InjectMocks
    OrderBookService orderBookService = new OrderBookServiceImpl();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void openOrderBook() {
        given(orderBookRepository.findByInstrumentIdAndStatus(1L, OrderBookStatus.OPEN))
                .willReturn(Optional.empty());
        Instrument instrument = new Instrument("Equity bonds");
        instrument.setId(1L);

        given(instrumentRepository.findById(1L)).willReturn(Optional.of(instrument));
        ResponseDTO<OrderBook> orderBook = orderBookService.open(1L);
        Assert.assertEquals(orderBook.getObj().getStatus(), OrderBookStatus.OPEN);
    }

    @Test
    public void testOpenOrderBookWhenBookIsAlreadyOpened() {
        given(orderBookRepository.findByInstrumentIdAndStatus(1L, OrderBookStatus.OPEN))
                .willReturn(Optional.of(TestHelper.getOrderBook(1L)));
        ResponseDTO<OrderBook> orderBook = orderBookService.open(1L);
        Assert.assertEquals(orderBook.getObj().getStatus(), OrderBookStatus.OPEN);
        Assert.assertEquals(orderBook.getSuccessMessage(), ResponseMessage.ORDER_BOOK_ALREADY_OPENED.getMessage(1L));
    }

    @Test(expected = OrderBookNotFoundException.class)
    public void testCloseOrderBookWhenBookIsAlreadyClosed() {
        given(orderBookRepository.findByIdAndStatus(1L, OrderBookStatus.OPEN)).willReturn(Optional.empty());
        orderBookService.close(1L);
    }

    @Test(expected = OrderBookNotFoundException.class)
    public void testAddOrderWhenOrderBookIsNotOpen() {
        Order order = TestHelper.getOrder(1L, 100L, LocalDate.now(), "MARKET");
        given(orderBookRepository.findByInstrumentIdAndStatus(order.getInstrumentId(), OrderBookStatus.OPEN))
                .willReturn(Optional.empty());
        orderBookService.addOrder(1L, order);
    }

    @Test(expected = InvalidOrderTypeException.class)
    public void testAddOrderWhenInvalidOrderTypeIsGiven() {
    	OrderBook orderBook = TestHelper.getOrderBook(1L);
        Order order = TestHelper.getOrder(1L, 100L, LocalDate.now(), null);
        given(orderBookRepository.findByIdAndStatus(order.getInstrumentId(), OrderBookStatus.OPEN))
                .willReturn(Optional.of(orderBook));
        orderBookService.addOrder(1L, order);
    }

    @Test(expected = OrderBookNotFoundException.class)
    public void testAddOrderWhenOrderTypeIsLimitWithNoPrice() {
        Order order = TestHelper.getOrder(1L, 100L, LocalDate.now(), "LIMIT");
        given(orderBookRepository.findByInstrumentIdAndStatus(order.getInstrumentId(), OrderBookStatus.OPEN))
                .willReturn(Optional.empty());
        orderBookService.addOrder(1L, order);
    }

    @Test
    public void closeOrderBook() {
        OrderBook orderBook = TestHelper.getOrderBook(1L);
        given(orderBookRepository.findByIdAndStatus(1L, OrderBookStatus.OPEN)).willReturn(Optional.of(orderBook));
        ResponseDTO<String> responseDTO = orderBookService.close(1L);
        Assert.assertEquals(responseDTO.getSuccessMessage(), ResponseMessage.ORDER_BOOK_CLOSED.getMessage());
    }
    
    @Test(expected = ExecutionNotAddedException.class)
    public void testExecuteOrderBookWhenExecutionQuantityIsZeroOrNegative() {
        OrderBook orderBook = TestHelper.getOrderBook(1L);
        orderBook.setStatus(OrderBookStatus.CLOSED);
        Execution execution = TestHelper.getExecution(0L, new BigDecimal(100));
        given(orderBookRepository.findById(1L)).willReturn(Optional.of(orderBook));
        orderBookService.execute(1L, execution);
    }

    @Test(expected = ExecutionNotAddedException.class)
    public void testExecuteOrderBookWhenOrderBookIsOpen() {
        OrderBook orderBook = TestHelper.getOrderBook(1L);
        Execution execution = TestHelper.getExecution(50L, new BigDecimal(100));
        given(orderBookRepository.findById(1L)).willReturn(Optional.of(orderBook));
        orderBookService.execute(1L, execution);
    }

    @Test(expected = ExecutionNotAddedException.class)
    public void testExecuteOrderBookWhenOrderBookIsExecuted() {
        OrderBook orderBook = TestHelper.getOrderBook(1L);
        orderBook.setStatus(OrderBookStatus.EXECUTED);
        Execution execution = TestHelper.getExecution(50L, new BigDecimal(100));
        given(orderBookRepository.findById(1L)).willReturn(Optional.of(orderBook));
        orderBookService.execute(1L, execution);
    }

    @Test
    public void testExecuteOrderBookWhenOrderBookIsClosed() {
        OrderBook orderBook = TestHelper.getOrderBook(1L);
        orderBook.setStatus(OrderBookStatus.CLOSED);
        orderBook.setExecutions(new ArrayList<>());
        orderBook.setOrders(new ArrayList<>());
        Execution execution = TestHelper.getExecution(50L, new BigDecimal(100));
        given(orderBookRepository.findById(1L)).willReturn(Optional.of(orderBook));
        ResponseDTO<String> responseDTO = orderBookService.execute(1L, execution);
        Assert.assertEquals(responseDTO.getSuccessMessage(), ResponseMessage.ORDER_BOOK_EXECUTED
                .getMessage(OrderBookStatus.EXECUTED, 1L));
    }

    @Test
    public void testExecuteOrderBookWhenMultipleOrders() {
        OrderBook orderBook = TestHelper.getOrderBook(1L);
        orderBook.setStatus(OrderBookStatus.CLOSED);
        orderBook.setExecutions(new ArrayList<>());
        final OrderContainer orderContainer = TestHelper.getOrderContainer(TestHelper.getOrder(1L,
                50L, LocalDate.now(), "MARKET"));
        List<OrderContainer> list = new ArrayList<>();
        list.add(orderContainer);
        orderBook.setOrders(list);
        OrderContainer orderContainer1 = TestHelper.getOrderContainer(TestHelper.getOrder(1L,
                50L, LocalDate.now(), "MARKET"));
        orderBook.getOrders().add(orderContainer1);
        Execution execution = TestHelper.getExecution(100L, new BigDecimal(100));
        given(orderBookRepository.findById(1L)).willReturn(Optional.of(orderBook));
        ResponseDTO<String> responseDTO = orderBookService.execute(1L, execution);
        Assert.assertEquals(responseDTO.getSuccessMessage(), ResponseMessage.ORDER_BOOK_EXECUTED
                .getMessage(OrderBookStatus.EXECUTED, 1L));
    }

    @Test
    public void testExecuteOrderBookWhenBookIsPartialExecuted() {
        OrderBook orderBook = TestHelper.getOrderBook(1L);
        orderBook.setStatus(OrderBookStatus.PARTIAL_EXECUTED);
        orderBook.setExecutions(new ArrayList<>());
        orderBook.setBookDemand(50L);
        final OrderContainer orderContainer = TestHelper.getOrderContainer(TestHelper.getOrder(1L,
                0L, LocalDate.now(), "MARKET"));
        orderContainer.setOrderStatus(OrderStatus.VALID);
        List<OrderContainer> list = new ArrayList<>();
        list.add(orderContainer);
        orderBook.setOrders(list);
        OrderContainer orderContainer1 = TestHelper.getOrderContainer(TestHelper.getOrder(1L, 50L,
                LocalDate.now(), "MARKET"));
        orderContainer1.setOrderStatus(OrderStatus.VALID);
        orderBook.getOrders().add(orderContainer1);
        Execution execution = TestHelper.getExecution(100L, new BigDecimal(100));
        given(orderBookRepository.findById(1L)).willReturn(Optional.of(orderBook));
        ResponseDTO<String> responseDTO = orderBookService.execute(1L, execution);
        Assert.assertEquals(responseDTO.getSuccessMessage(), ResponseMessage.ORDER_BOOK_EXECUTED
                .getMessage(OrderBookStatus.EXECUTED, 1L));
    }

    @Test
    public void testExecuteOrderBookWhenQuantityIsLessThanBookDemand() {
        OrderBook orderBook = TestHelper.getOrderBook(1L);
        orderBook.setStatus(OrderBookStatus.CLOSED);
        orderBook.setExecutions(new ArrayList<>());
        final OrderContainer orderContainer = TestHelper.getOrderContainer(TestHelper.getOrder(1L,
                50L, LocalDate.now(), "MARKET"));
        List<OrderContainer> list = new ArrayList<>();
        list.add(orderContainer);
        orderBook.setOrders(list);
        OrderContainer orderContainer1 = TestHelper.getOrderContainer(TestHelper.getOrder(1L,
                50L, LocalDate.now(), "MARKET"));
        orderBook.getOrders().add(orderContainer1);
        Execution execution = TestHelper.getExecution(75L, new BigDecimal(100));
        given(orderBookRepository.findById(1L)).willReturn(Optional.of(orderBook));
        ResponseDTO<String> responseDTO = orderBookService.execute(1L, execution);
        Assert.assertEquals(responseDTO.getSuccessMessage(), ResponseMessage.ORDER_BOOK_EXECUTED
                .getMessage(OrderBookStatus.PARTIAL_EXECUTED, 1L));
    }

    @Test
    public void testOrderBookStats() {
        OrderBook orderBook = TestHelper.getOrderBook(1L);
        orderBook.setExecutions(new ArrayList<>());
        List<OrderContainer> list = new ArrayList<>();
        orderBook.setOrders(list);
        given(orderBookRepository.findByIdAndStatus(1L, OrderBookStatus.OPEN)).willReturn(Optional.of(orderBook));
        orderBookService.addOrder(1L, TestHelper.getOrder(1L, 20L, LocalDate.now(),
                "MARKET"));
        orderBookService.addOrder(1L, TestHelper.getOrder(1L, 50L, LocalDate.now(),
                "MARKET"));
        given(orderBookStatsRepository.findByOrderBookId(1L)).willReturn(Optional.empty());
        given(orderBookRepository.findById(1L)).willReturn(Optional.of(orderBook));
        OrderBookHighLevelStats orderBookHighLevelStats = orderBookService.getOrderBook(1L);
        Assert.assertEquals(orderBookHighLevelStats.getTotalOrders(), new Long(2L));
        Assert.assertEquals(orderBookHighLevelStats.getBiggestOrder(), new Long(50L));
        Assert.assertEquals(orderBookHighLevelStats.getSmallestOrder(), new Long(20L));
        Assert.assertEquals(orderBookHighLevelStats.getBookDemand(), new Long(70L));
    }

    @Test
    public void testOrderStats() {
        OrderBook orderBook = TestHelper.getOrderBook(1L);
        orderBook.setExecutions(new ArrayList<>());
        List<OrderContainer> list = new ArrayList<>();
        OrderContainer orderContainer = TestHelper.getOrderContainer(TestHelper.getOrder(1L, 50L,
                LocalDate.now(), "MARKET"));
        orderContainer.getOrder().setId(1L);
        list.add(orderContainer);
        orderBook.setOrders(list);
        given(orderBookRepository.findById(1L)).willReturn(Optional.of(orderBook));
        OrderStats orderStats = orderBookService.getOrder(1L, 1L);
        Assert.assertEquals(orderStats.getOrderId(), new Long(1L));
    }
}
