package com.myproject.orderbook.objects.stats;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.myproject.orderbook.objects.domain.OrderBookStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
public class OrderBookStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic(optional = false)
    private Long orderBookId;

    @JsonIgnore
    private Long validOrders;

    private Long invalidOrders;

    private Long totalOrders;

    private Long biggestOrder;

    private Long smallestOrder;

    private LocalDate firstOrderEntryDate;

    private LocalDate lastOrderEntryDate;

    private OrderBookStatus status;

    private Long bookDemand;

    private BigDecimal executionPrice;

    private Long accumulatedExecQuantity;

    @OneToMany(mappedBy = "orderBookStats", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<LimitBreakDownStats> limitBreakDownStatsList;

    public OrderBookStats() {
    }

    public OrderBookStats(Long orderBookId, Long bookDemand, Long accumulatedExecQuantity, OrderBookStatus status) {
        this.orderBookId = orderBookId;
        this.bookDemand = bookDemand;
        this.status = status;
        this.accumulatedExecQuantity = accumulatedExecQuantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderBookId() {
        return orderBookId;
    }

    public void setOrderBookId(Long orderBookId) {
        this.orderBookId = orderBookId;
    }

    public Long getValidOrders() {
        return validOrders;
    }

    public void setValidOrders(Long validOrders) {
        this.validOrders = validOrders;
    }

    public Long getInvalidOrders() {
        return invalidOrders;
    }

    public void setInvalidOrders(Long invalidOrders) {
        this.invalidOrders = invalidOrders;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
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

    public BigDecimal getExecutionPrice() {
        return executionPrice;
    }

    public void setExecutionPrice(BigDecimal executionPrice) {
        this.executionPrice = executionPrice;
    }

    public List<LimitBreakDownStats> getLimitBreakDownStatsList() {
        return limitBreakDownStatsList;
    }

    public void setLimitBreakDownStatsList(List<LimitBreakDownStats> limitBreakDownStatsList) {
        this.limitBreakDownStatsList = limitBreakDownStatsList;
    }

    public Long getAccumulatedExecQuantity() {
        return accumulatedExecQuantity;
    }

    public void setAccumulatedExecQuantity(Long accumulatedExecQuantity) {
        this.accumulatedExecQuantity = accumulatedExecQuantity;
    }
}
