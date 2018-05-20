package io.github.sudhanshugupta.n26.transaction.management.config;

import io.github.sudhanshugupta.n26.transaction.management.service.TransactionService;
import io.github.sudhanshugupta.n26.transaction.management.service.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

@TestConfiguration
@TestPropertySource("classpath:test.properties")
public class TestBeanConfiguration {

    @Value("${transaction.metric.target-duration-in-min}")
    private int targetDurationInMin;
    @Value("${transaction.metric.size}")
    private int size;

    @Bean
    public TransactionProperties transactionProperties() {
        TransactionProperties transactionProperties = new TransactionProperties();
        transactionProperties.setSize(size);
        transactionProperties.setTargetDurationInMin(targetDurationInMin);
        return transactionProperties;
    }

    @Bean
    public TransactionService transactionService() {
        return new TransactionServiceImpl(transactionProperties());
    }
}
