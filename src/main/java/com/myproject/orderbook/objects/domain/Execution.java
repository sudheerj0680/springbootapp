package com.myproject.orderbook.objects.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.myproject.orderbook.objects.dto.ExecutionDTO;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Execution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quantity;
    private BigDecimal executionPrice;
    @ManyToOne
    @JoinColumn(name = "order_book_id")
    @JsonBackReference
    private OrderBook orderBook;

    public Execution(ExecutionDTO executionDTO) {
        this.quantity = executionDTO.getQuantity();
        this.executionPrice = executionDTO.getExecutionPrice();
    }

    public Execution() {
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getExecutionPrice() {
        return executionPrice;
    }

    public void setExecutionPrice(BigDecimal executionPrice) {
        this.executionPrice = executionPrice;
    }

    public void setOrderBook(OrderBook orderBook) {
        this.orderBook = orderBook;
    }
}
