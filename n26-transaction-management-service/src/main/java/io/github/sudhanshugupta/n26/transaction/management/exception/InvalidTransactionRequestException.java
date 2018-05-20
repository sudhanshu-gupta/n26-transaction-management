package io.github.sudhanshugupta.n26.transaction.management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidTransactionRequestException extends RuntimeException {

    private final HttpStatus httpStatus;

    public InvalidTransactionRequestException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public InvalidTransactionRequestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public InvalidTransactionRequestException(String message, Throwable cause,
            HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public InvalidTransactionRequestException(Throwable cause, HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }

    public InvalidTransactionRequestException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace, HttpStatus httpStatus) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.httpStatus = httpStatus;
    }
}
