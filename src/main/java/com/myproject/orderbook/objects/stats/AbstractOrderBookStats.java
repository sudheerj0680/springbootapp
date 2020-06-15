package com.myproject.orderbook.objects.stats;

import com.myproject.orderbook.objects.domain.OrderBookStatus;

import java.time.LocalDate;
import java.util.List;

public class AbstractOrderBookStats {

    private Long orderBookId;

    private Long biggestOrder;

    private Long smallestOrder;

    private LocalDate firstOrderEntryDate;

    private LocalDate lastOrderEntryDate;

    private OrderBookStatus status;

    private Long bookDemand;


    private List<LimitBreakDownStats> limitBreakDown;

    public AbstractOrderBookStats(OrderBookStats orderBookStats) {
        this.orderBookId = orderBookStats.getOrderBookId();
        this.biggestOrder = orderBookStats.getBiggestOrder();
        this.smallestOrder = orderBookStats.getSmallestOrder();
        this.firstOrderEntryDate = orderBookStats.getFirstOrderEntryDate();
        this.lastOrderEntryDate = orderBookStats.getLastOrderEntryDate();
        this.status = orderBookStats.getStatus();
        this.bookDemand = orderBookStats.getBookDemand();
        this.limitBreakDown = orderBookStats.getLimitBreakDownStatsList();
    }

    public AbstractOrderBookStats() {
    }

    public Long getOrderBookId() {
        return orderBookId;
    }

    public void setOrderBookId(Long orderBookId) {
        this.orderBookId = orderBookId;
    }

    public Long getBiggestOrder() {
        return biggestOrder;
    }

    public void setBiggestOrder(Long biggestOrder) {
        this.biggestOrder = biggestOrder;
    }

    public Long getSmallestOrder() {
        return smallestOrder;
    }

    public void setSmallestOrder(Long smallestOrder) {
        this.smallestOrder = smallestOrder;
    }

    public LocalDate getFirstOrderEntryDate() {
        return firstOrderEntryDate;
    }

    public void setFirstOrderEntryDate(LocalDate firstOrderEntryDate) {
        this.firstOrderEntryDate = firstOrderEntryDate;
    }

    public LocalDate getLastOrderEntryDate() {
        return lastOrderEntryDate;
    }

    public void setLastOrderEntryDate(LocalDate lastOrderEntryDate) {
        this.lastOrderEntryDate = lastOrderEntryDate;
    }

    public OrderBookStatus getStatus() {
        return status;
    }

    public void setStatus(OrderBookStatus status) {
        this.status = status;
    }

    public Long getBookDemand() {
        return bookDemand;
    }

    public void setBookDemand(Long bookDemand) {
        this.bookDemand = bookDemand;
    }

    public List<LimitBreakDownStats> getLimitBreakDown() {
        return limitBreakDown;
    }

    public void setLimitBreakDown(List<LimitBreakDownStats> limitBreakDown) {
        this.limitBreakDown = limitBreakDown;
    }
}
