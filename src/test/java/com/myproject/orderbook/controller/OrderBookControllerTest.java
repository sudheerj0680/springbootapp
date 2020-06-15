package com.myproject.orderbook.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.orderbook.helper.TestHelper;
import com.myproject.orderbook.objects.ResponseMessage;
import com.myproject.orderbook.objects.domain.*;
import com.myproject.orderbook.objects.dto.ExecutionDTO;
import com.myproject.orderbook.objects.dto.OrderDTO;
import com.myproject.orderbook.objects.dto.ResponseDTO;
import com.myproject.orderbook.service.OrderBookService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = OrderBookController.class, secure = false)
public class OrderBookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private OrderBookService orderBookService;
    private JacksonTester<OrderDTO> jsonOrderDTOTester;
    private JacksonTester<ExecutionDTO> executionJacksonTester;

    @Before
    public void setup() {
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    public void open() throws Exception {
        OrderBook orderBook = TestHelper.getOrderBook(1L);
        given(orderBookService.open(1L)).willReturn(new ResponseDTO<>(orderBook, "Success"));
        mockMvc
                .perform(post("/orderbook/1/open").content("").contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.successMessage", is("Success")));
    }

    @Test
    public void addOrder() throws Exception {
        OrderDTO orderDTO = TestHelper.getOrderDTO(1L, 100L, LocalDate.now(), "MARKET", new BigDecimal(100));
        final String orderDTOJson = jsonOrderDTOTester.write(orderDTO).getJson();
        given(orderBookService.addOrder(any(Long.class), any(Order.class))).willReturn(new ResponseDTO<>(ResponseMessage.ORDER_ADDED.getMessage()));
        mockMvc
                .perform(put("/orderbook/1/order").content(orderDTOJson).contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void close() throws Exception {
        given(orderBookService.close(1L)).willReturn(new ResponseDTO<>("Success"));
        mockMvc
                .perform(put("/orderbook/1/close").contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void execute() throws Exception {
        ExecutionDTO executionDTO = TestHelper.getExecutionDTO(100L, new BigDecimal(100));
        final String executionDTOJson = executionJacksonTester.write(executionDTO).getJson();
        given(orderBookService.execute(1L, new Execution(executionDTO))).willReturn(
                new ResponseDTO<>(ResponseMessage.ORDER_BOOK_EXECUTED.getMessage(OrderBookStatus.EXECUTED, 1L)));
        mockMvc
                .perform(put("/orderbook/1/execute").content(executionDTOJson).contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void getOrder() throws Exception {
        given(orderBookService.getOrder(1L, 1L)).willReturn(TestHelper.getOrderStats(1L,
                OrderStatus.VALID, 12L, new BigDecimal(20), new BigDecimal(10)));
        mockMvc
                .perform(get("/orderbook/1/order/1").contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is(1)));
    }

    @Test
    public void getOrderBook() throws Exception {
        given(orderBookService.getOrderBook(1L))
                .willReturn(TestHelper.getOrderBookHighLevelStats(TestHelper.getOrderBookStats(1L,
                        0L, 0L, OrderBookStatus.EXECUTED)));
        mockMvc
                .perform(get("/orderbook/1/stats").contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderBookId", is(1)));
    }
}