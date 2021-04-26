package com.va.vendingmachine.coins.exception;

/**
 * Exception class to thrown when invalid format for input
 */
public class InvalidInputException extends RuntimeException {
    private String message;

    public InvalidInputException(String string) {
        this.message = string;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

