package com.myproject.orderbook.objects.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

public class ResponseDTO<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String successMessage;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T obj;

    public ResponseDTO(T type, String successMessage) {
        this.obj = type;
        this.successMessage = successMessage;
    }

    public ResponseDTO(String successMessage) {
        this.successMessage = successMessage;
    }

    public ResponseDTO() {
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
