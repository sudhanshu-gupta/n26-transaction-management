package io.github.sudhanshugupta.n26.transaction.management.controller.advice;

import io.github.sudhanshugupta.n26.transaction.management.exception.InvalidTransactionRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(InvalidTransactionRequestException.class)
    public ResponseEntity handleInvalidTransactionException(InvalidTransactionRequestException e) {
        HttpStatus httpStatus = e.getHttpStatus()!=null?e.getHttpStatus():HttpStatus.BAD_REQUEST;
        return new ResponseEntity(e.getMessage(), httpStatus);
    }
}
