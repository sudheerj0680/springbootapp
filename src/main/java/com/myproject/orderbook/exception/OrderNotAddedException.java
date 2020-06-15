package com.myproject.orderbook.exception;

public class OrderNotAddedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OrderNotAddedException() {
    }

    public OrderNotAddedException(String message) {
        super(message);
    }
}
