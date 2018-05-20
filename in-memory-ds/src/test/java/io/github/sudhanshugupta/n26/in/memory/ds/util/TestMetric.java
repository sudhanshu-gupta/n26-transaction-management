package io.github.sudhanshugupta.n26.in.memory.ds.util;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestMetric implements Serializable {

    private final long sum;

    public static final TestMetric ZERO = new TestMetric(0);

    public TestMetric add(long amount) {
        return this.equals(ZERO) ?
                new TestMetric(amount) : new TestMetric(this.getSum() + amount);
    }

    public TestMetric merge(TestMetric that) {
        if(this.equals(ZERO)) {
            return that;
        } else if(that.equals(ZERO)) {
            return this;
        } else {
            return new TestMetric(this.getSum() + that.getSum());
        }
    }
}
