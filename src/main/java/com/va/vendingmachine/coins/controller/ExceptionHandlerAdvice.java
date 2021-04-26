package com.va.vendingmachine.coins.controller;

import com.va.vendingmachine.coins.exception.InvalidInputException;
import com.va.vendingmachine.coins.exception.NotEnoughChangeException;
import com.va.vendingmachine.coins.pojo.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse("Server Error", ex.getLocalizedMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidInputException.class)
    public final ResponseEntity<Object> handleInvalidInputException(InvalidInputException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse("InvalidInputException", ex.getLocalizedMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotEnoughChangeException.class)
    public final ResponseEntity<Object> handleINotEnoughException(NotEnoughChangeException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse("NotEnoughChangeException", ex.getLocalizedMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
