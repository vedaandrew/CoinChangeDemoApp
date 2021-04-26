package com.va.vendingmachine.coins.pojo;

/**
 * Class to return Error Response in generic  format across api service
 */
public class ErrorResponse {

    public ErrorResponse(String message, String detail) {
        super();
        this.message = message;
        this.detail = detail;
    }

    private String message;

    private String detail;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
