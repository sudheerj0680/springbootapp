package com.myproject.orderbook.objects.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_container")
public class OrderContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL,
            mappedBy = "orderContainer")
    private Order order;
    private Long quantity;
    private OrderStatus orderStatus;
    private Long executedQuantity = new Long(0L);
    private BigDecimal executionPrice;

    @ManyToOne
    @JoinColumn(name = "order_book_id")
    @JsonBackReference
    private OrderBook orderBook;

    public OrderContainer(Order order) {
        this.order = order;
        this.quantity = order.getQuantity();
        this.orderStatus = OrderStatus.VALID;
        this.order.setOrderContainer(this);
    }

    public OrderContainer() {
    }

    public void setOrderBook(OrderBook orderBook) {
        this.orderBook = orderBook;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Order getOrder() {
        return order;
    }

    public void setExecutedQuantity(Long executedQuantity) {
        this.executedQuantity = executedQuantity;
    }

    public void reduceQuantity(Long executedQuantity) {
        this.quantity -= executedQuantity;
    }

    public void updateExecutionQuantity(Long executedQuantity) {
        this.executedQuantity += executedQuantity;
    }

    public BigDecimal getExecutionPrice() {
        return executionPrice;
    }

    public void setExecutionPrice(BigDecimal executionPrice) {
        this.executionPrice = executionPrice;
    }
}
