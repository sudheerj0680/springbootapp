package com.myproject.orderbook.exception;

public class OrderBookNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OrderBookNotFoundException() {
    }

    public OrderBookNotFoundException(String message) {
        super(message);
    }
}
