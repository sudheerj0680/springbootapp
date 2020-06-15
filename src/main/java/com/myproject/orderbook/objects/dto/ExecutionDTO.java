package com.myproject.orderbook.objects.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class ExecutionDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long quantity;
    private BigDecimal executionPrice;

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
}
