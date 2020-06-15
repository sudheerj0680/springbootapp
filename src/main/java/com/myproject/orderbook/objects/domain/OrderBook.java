package com.myproject.orderbook.objects.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

@Entity
public class OrderBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic(optional = false)
    private OrderBookStatus status;

    @Basic(optional = false)
    private Long instrumentId;

    @Basic(optional = false)
    private Long bookDemand = new Long(0);

    @Basic(optional = false)
    private Long accumulatedExecQuantity = new Long(0);

    @OneToMany(mappedBy = "orderBook", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderContainer> orders;

    @OneToMany(mappedBy = "orderBook", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Execution> executions;

    @Basic(optional = false)
    private String instrument;

    public OrderBook(final Long instrumentId) {
        this.instrumentId = instrumentId;
    }

    public OrderBook(final Long instrumentId, final String instrument) {
        this.instrumentId = instrumentId;
        this.instrument = instrument;
    }

    public OrderBook() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderBookStatus getStatus() {
        return status;
    }

    public void setStatus(OrderBookStatus status) {
        this.status = status;
    }

    public List<OrderContainer> getOrders() {
        return orders;
    }

    @Override
    public String toString() {
        return "OrderBook{" +
                "id=" + id +
                ", status=" + status +
                ", instrumentId=" + instrumentId +
                ", bookDemand=" + bookDemand +
                ", orders=" + orders +
                '}';
    }

    public Long getBookDemand() {
        return bookDemand;
    }

    public void setBookDemand(Long bookDemand) {
        this.bookDemand = bookDemand;
    }

    public void updateBookDemand(Long executedQuantity) {
        bookDemand -= executedQuantity;
    }

    public void updateAccumulatedExecQuantity(Long executedQuantity) {
        accumulatedExecQuantity += executedQuantity;
    }

    public void setOrders(List<OrderContainer> orders) {
        this.orders = orders;
    }

    public List<Execution> getExecutions() {
        return executions;
    }

    public void setExecutions(List<Execution> executions) {
        this.executions = executions;
    }

    public Long getAccumulatedExecQuantity() {
        return accumulatedExecQuantity;
    }

    public void setAccumulatedExecQuantity(Long accumulatedExecQuantity) {
        this.accumulatedExecQuantity = accumulatedExecQuantity;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Long instrumentId) {
        this.instrumentId = instrumentId;
    }
}
