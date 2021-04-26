package com.va.vendingmachine.coins.exception;

/**
 * Exception class to thrown when not enough coins in inventory
 */
public class NotEnoughChangeException extends RuntimeException {
    private String message;

    public NotEnoughChangeException(String string) {
        this.message = string;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

