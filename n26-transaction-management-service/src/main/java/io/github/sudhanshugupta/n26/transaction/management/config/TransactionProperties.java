package io.github.sudhanshugupta.n26.transaction.management.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "transaction.metric")
public class TransactionProperties {

    private int targetDurationInMin;
    private int size;
}
