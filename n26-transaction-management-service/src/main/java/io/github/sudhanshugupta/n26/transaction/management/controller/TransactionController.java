package io.github.sudhanshugupta.n26.transaction.management.controller;

import io.github.sudhanshugupta.n26.transaction.management.controller.validator.TransactionRequestValidator;
import io.github.sudhanshugupta.n26.transaction.management.domain.Transaction;
import io.github.sudhanshugupta.n26.transaction.management.domain.TransactionStatistic;
import io.github.sudhanshugupta.n26.transaction.management.service.TransactionService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new TransactionRequestValidator());
    }

    public TransactionController(
            TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping(value = "/transactions", consumes = "application/json")
    public ResponseEntity addTransaction(@Valid @RequestBody Transaction transaction) {
        transactionService.addTransaction(transaction);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/statistics")
    public ResponseEntity<TransactionStatistic> getTransactionStatistic() {
        return new ResponseEntity<TransactionStatistic>(transactionService.getTransactionStatistics(), HttpStatus.OK);
    }
}
