package io.github.sudhanshugupta.n26.transaction.management.controller.validator;


import io.github.sudhanshugupta.n26.transaction.management.domain.Transaction;
import io.github.sudhanshugupta.n26.transaction.management.exception.InvalidTransactionRequestException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class TransactionRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return Transaction.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "amount", "field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "timestamp", "field.required");
        Transaction transaction = (Transaction) o;
        validateTransactionTime(transaction.getTimestamp(), 1);
    }

    private void validateTransactionTime(long timestamp, int lastMin) {
        long now = System.currentTimeMillis();
        long lastMinInMilli = Duration.of(now, ChronoUnit.MILLIS).minus(lastMin, ChronoUnit.MINUTES).toMillis();
        if(timestamp < lastMinInMilli) {
            throw new InvalidTransactionRequestException(HttpStatus.NO_CONTENT);
        }
        if(timestamp > now) {
            throw new InvalidTransactionRequestException("Invalid transaction time", HttpStatus.BAD_REQUEST);
        }
    }
}
