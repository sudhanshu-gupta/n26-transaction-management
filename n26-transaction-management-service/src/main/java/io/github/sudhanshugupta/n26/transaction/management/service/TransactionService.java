package io.github.sudhanshugupta.n26.transaction.management.service;

import io.github.sudhanshugupta.n26.transaction.management.domain.Transaction;
import io.github.sudhanshugupta.n26.transaction.management.domain.TransactionStatistic;

public interface TransactionService {

    void addTransaction(Transaction transaction);

    TransactionStatistic getTransactionStatistics();
}
