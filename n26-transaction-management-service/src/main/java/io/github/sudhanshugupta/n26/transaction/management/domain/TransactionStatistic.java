package io.github.sudhanshugupta.n26.transaction.management.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonAutoDetect
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionStatistic implements Serializable {

    private static final long serialVersionUID = 2033572114003172447L;
    private long count;
    private double sum;
    private double avg;
    private double min;
    private double max;

    public double getAvg() {
        return getCount() > 0 ? roundUp(getSum()/getCount()):Double.NaN;
    }

    public static final TransactionStatistic ZERO = TransactionStatistic.builder()
            .count(0)
            .sum(0.0)
            .min(Double.NaN)
            .max(Double.NaN)
            .build();

    public TransactionStatistic update(double amount) {
        return this.equals(ZERO)?
                TransactionStatistic.builder()
                        .count(1)
                        .sum(amount)
                        .min(amount)
                        .max(amount)
                        .build() :
                        TransactionStatistic.builder()
                        .count(getCount()+1)
                        .sum(getSum() + amount)
                        .max(Math.max(getMax(), amount))
                        .min(Math.min(getMin(), amount))
                        .build();
    }

    public TransactionStatistic merge(TransactionStatistic that) {
        if(this.equals(ZERO)) {
            return that;
        } else if(that.equals(ZERO)) {
            return this;
        } else {
            return TransactionStatistic.builder()
                    .count(getCount() + that.getCount())
                    .sum(getSum() + that.getSum())
                    .min(Math.min(getMin(), that.getMin()))
                    .max(Math.max(getMax(), that.getMax()))
                    .build();
        }
    }

    private static double roundUp(double value) {
        return new BigDecimal(value).setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
