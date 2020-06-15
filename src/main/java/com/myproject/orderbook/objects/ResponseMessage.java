package com.myproject.orderbook.objects;

import com.myproject.orderbook.objects.domain.OrderBookStatus;

public enum ResponseMessage {
    ORDER_BOOK_OPENED("Order book is opened for instrument Id - "),
    ORDER_BOOK_ALREADY_OPENED("Order book is already opened for given instrument - "),
    ORDER_BOOK_CLOSED("Order book has been marked as closed"),
    ORDER_BOOK_NOT_FOUND_FOR_CLOSED("No open order book found to perform the close operation"),
    ORDER_BOOK_NOT_FOUND_FOR_ADD_ORDER("No open order book found to add orders"),
    ORDER_BOOK_NOT_FOUND_FOR_EXECUTION("No order book found to add executions"),
    ORDER_BOOK_EXECUTED("Order book has been <Status>. Order book Id - "),
    ORDER_BOOK_NOT_FOUND("Order book not found!!"),

    EXECUTION_QUANTITY_NOT_VALID("Execution quantity can not be negative or zero."),
    EXECUTION_NOT_ADDED_AS_BOOK_NOT_CLOSED("Execution can not be added to order book as it is not closed"),
    EXECUTION_NOT_ADDED_AS_BOOK_ALREADY_EXECUTED("Execution can not be added to order book as it is already executed"),

    ORDER_ADDED("Order has been added to the order book"),
    ORDER_NOT_FOUND("Order not found!!"),
    LIMIT_ORDER_PRICE_VALIDATION_FAILED("Price can not be null/zero if order is Limit order"),
    INVALID_ORDER_TYPE("Invalid order type!!");
    String message;

    ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public String getMessage(Long id) {
        return this.message + id;
    }

    public String getMessage(OrderBookStatus status, Long id) {
        return this.message.replace("<Status>", status.name()) + id;
    }
}
