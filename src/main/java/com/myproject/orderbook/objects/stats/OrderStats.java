package com.myproject.orderbook.objects.stats;

import com.myproject.orderbook.objects.domain.OrderStatus;

import java.math.BigDecimal;

public class OrderStats {
    private Long orderId;
    private OrderStatus orderStatus;
    private BigDecimal executionPrice;
    private BigDecimal OrderPrice;
    private Long executionQuantity;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getExecutionPrice() {
        return executionPrice;
    }

    public void setExecutionPrice(BigDecimal executionPrice) {
        this.executionPrice = executionPrice;
    }

    public BigDecimal getOrderPrice() {
        return OrderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        OrderPrice = orderPrice;
    }

    public Long getExecutionQuantity() {
        return executionQuantity;
    }

    public void setExecutionQuantity(Long executionQuantity) {
        this.executionQuantity = executionQuantity;
    }

    public OrderStats(Long orderId, OrderStatus orderStatus, BigDecimal executionPrice, BigDecimal orderPrice, Long executionQuantity) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.executionPrice = executionPrice;
        OrderPrice = orderPrice;
        this.executionQuantity = executionQuantity;
    }

    public OrderStats() {
    }
}
