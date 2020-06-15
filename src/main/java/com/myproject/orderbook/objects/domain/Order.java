package com.myproject.orderbook.objects.domain;

import com.myproject.orderbook.exception.InvalidOrderTypeException;
import com.myproject.orderbook.objects.ResponseMessage;
import com.myproject.orderbook.objects.dto.OrderDTO;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public final class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    final private Long instrumentId;
    final private Long quantity;
    final private BigDecimal price;
    final private LocalDate entryDate;
    final private OrderType orderType;

    @OneToOne(optional = false)
    @JoinColumn(unique = true)
    private OrderContainer orderContainer;

    public Order(OrderDTO orderDTO) {
        this.quantity = orderDTO.getQuantity();
        this.instrumentId = orderDTO.getInstrumentId();
        this.price = orderDTO.getPrice();
        this.entryDate = orderDTO.getEntryDate();
        try {
            if (orderDTO.getOrderType() != null)
                this.orderType = OrderType.valueOf(orderDTO.getOrderType().toUpperCase());
            else
                this.orderType = null;
        } catch (Exception ex) {
            throw new InvalidOrderTypeException(ResponseMessage.INVALID_ORDER_TYPE.getMessage());
        }
    }

    public Order() {
        this.quantity = null;
        this.instrumentId = null;
        this.price = null;
        this.entryDate = null;
        this.orderType = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", instrumentId=" + instrumentId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", entryDate=" + entryDate +
                ", orderType=" + orderType +
                '}';
    }

    public void setOrderContainer(OrderContainer orderContainer) {
        this.orderContainer = orderContainer;
    }
}
