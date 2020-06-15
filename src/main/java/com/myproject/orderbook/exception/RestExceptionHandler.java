package com.myproject.orderbook.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({OrderNotFoundException.class, OrderBookNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getLocalizedMessage(),
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllBadRequest(
            Exception ex, WebRequest request) {
        ex.printStackTrace();
        return handleExceptionInternal(ex, ex.getLocalizedMessage() != null && ex.getLocalizedMessage().length() > 0
                        ? ex.getLocalizedMessage() : "Something went wrong!!",
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
