package io.github.sudhanshugupta.n26.transaction.management.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.sudhanshugupta.n26.in.memory.ds.metric.AtomicMetricArray;
import io.github.sudhanshugupta.n26.transaction.management.config.TransactionProperties;
import io.github.sudhanshugupta.n26.transaction.management.domain.Transaction;
import io.github.sudhanshugupta.n26.transaction.management.domain.TransactionStatistic;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TransactionServiceImplTest {

    private TransactionServiceImpl transactionService;
    @Mock
    private AtomicMetricArray<TransactionStatistic> transactions;

    private Transaction transaction;
    @Mock
    private TransactionStatistic transactionStatistic;
    @Mock
    private TransactionStatistic otherTransactionStatistic;
    @Mock
    private TransactionStatistic reducedTransactionStatistic;
    @Mock
    private TransactionProperties transactionProperties;

    @Before
    public void setUp() throws Exception {
        when(transactionProperties.getSize()).thenReturn(60);
        when(transactionProperties.getTargetDurationInMin()).thenReturn(1);
        transaction = Transaction.builder()
                .amount(10d)
                .timestamp(System.currentTimeMillis())
                .build();
        transactionService = new TransactionServiceImpl(transactions);
    }

    @Test
    public void shouldAddTransaction() {
        doAnswer(invocationOnMock -> invocationOnMock.getArgumentAt(1, UnaryOperator.class)
                .apply(transactionStatistic)).when(transactions).addOrUpdate(anyLong(), any(UnaryOperator.class));
        transactionService.addTransaction(transaction);
        verify(transactionStatistic).update(anyDouble());
    }

    @Test
    public void shouldGetTransactionStatistic() {
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(0, BinaryOperator.class)
                .apply(transactionStatistic, otherTransactionStatistic);
                return reducedTransactionStatistic;})
                .when(transactions).reduce(any(BinaryOperator.class));
        transactionService.getTransactionStatistics();
        verify(transactionStatistic).merge(any(TransactionStatistic.class));
    }
}