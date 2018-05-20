package io.github.sudhanshugupta.n26.transaction.management.service.impl;

import com.google.common.annotations.VisibleForTesting;
import io.github.sudhanshugupta.n26.in.memory.ds.metric.AtomicMetricArray;
import io.github.sudhanshugupta.n26.transaction.management.config.TransactionProperties;
import io.github.sudhanshugupta.n26.transaction.management.domain.Transaction;
import io.github.sudhanshugupta.n26.transaction.management.domain.TransactionStatistic;
import io.github.sudhanshugupta.n26.transaction.management.service.TransactionService;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final AtomicMetricArray<TransactionStatistic> transactions;

    @Autowired
    public TransactionServiceImpl(TransactionProperties transactionProperties) {
        this(new AtomicMetricArray<>(transactionProperties.getTargetDurationInMin(), ChronoUnit.MINUTES,
                ChronoUnit.SECONDS, () -> TransactionStatistic.ZERO, transactionProperties.getSize()));
    }

    @VisibleForTesting
    protected TransactionServiceImpl(
            AtomicMetricArray<TransactionStatistic> transactions) {
        this.transactions = transactions;
    }

    @Override
    public void addTransaction(Transaction transaction) {
        transactions.addOrUpdate(transaction.getTimestamp(),
                transactionStatistic -> transactionStatistic.update(transaction.getAmount()));
    }

    @Override
    public TransactionStatistic getTransactionStatistics() {
        TransactionStatistic transactionStatistic = transactions.reduce(TransactionStatistic::merge);
        log.info("Transaction at timestamp={}", System.currentTimeMillis(), transactionStatistic);
        return transactionStatistic;
    }
}
