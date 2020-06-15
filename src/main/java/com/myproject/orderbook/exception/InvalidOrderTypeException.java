package com.myproject.orderbook.exception;

public class InvalidOrderTypeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidOrderTypeException(String message) {
        super(message);
    }
}
