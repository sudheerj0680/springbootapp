package com.myproject.orderbook.exception;

public class ExecutionNotAddedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExecutionNotAddedException() {
    }

    public ExecutionNotAddedException(String message) {
        super(message);
    }
}
