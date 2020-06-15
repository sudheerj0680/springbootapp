package com.myproject.orderbook.objects.stats;

import java.math.BigDecimal;

public class OrderBookDetailStats extends AbstractOrderBookStats {
    private Long invalidOrders;

    private Long validOrders;

    private BigDecimal executionPrice;

    private Long accumulatedExecQuantity;

    public OrderBookDetailStats() {
        super();
    }

    public OrderBookDetailStats(OrderBookStats orderBookStats) {
        super(orderBookStats);
        this.invalidOrders = orderBookStats.getInvalidOrders();
        this.validOrders = orderBookStats.getValidOrders();
        this.executionPrice = orderBookStats.getExecutionPrice();
        this.accumulatedExecQuantity = orderBookStats.getAccumulatedExecQuantity();
    }

    public Long getInvalidOrders() {
        return invalidOrders;
    }

    public void setInvalidOrders(Long invalidOrders) {
        this.invalidOrders = invalidOrders;
    }

    public Long getValidOrders() {
        return validOrders;
    }

    public void setValidOrders(Long validOrders) {
        this.validOrders = validOrders;
    }

    public BigDecimal getExecutionPrice() {
        return executionPrice;
    }

    public void setExecutionPrice(BigDecimal executionPrice) {
        this.executionPrice = executionPrice;
    }

    public Long getAccumulatedExecQuantity() {
        return accumulatedExecQuantity;
    }

    public void setAccumulatedExecQuantity(Long accumulatedExecQuantity) {
        this.accumulatedExecQuantity = accumulatedExecQuantity;
    }
}
