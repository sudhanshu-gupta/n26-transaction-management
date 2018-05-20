package io.github.sudhanshugupta.n26.in.memory.ds.metric;

import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class AtomicMetricArray<V> implements Serializable {

    private static final long serialVersionUID = 3216383712468247730L;
    private final int targetDuration;
    private final ChronoUnit targetUnit;
    private final ChronoUnit groupingUnit;
    private final Supplier<V> factory;
    private final AtomicReferenceArray<AtomicMetric<V>> array;

    public AtomicMetricArray(int targetDuration, ChronoUnit targetUnit, ChronoUnit groupingUnit,
            Supplier<V> factory, int size) {
        this.targetDuration = targetDuration;
        this.targetUnit = targetUnit;
        this.groupingUnit = groupingUnit;
        this.factory = factory;
        array = new AtomicReferenceArray<>(size);
    }

    public void addOrUpdate(long timestamp, UnaryOperator<V> value) {
        getAtomicMetric(timestamp).updateAndGet(value);
    }

    public V reduce(BinaryOperator<V> operator) {
        return getStream().reduce(factory.get(), operator);
    }

    private int getOffset(long index) {
        return (int) index%array.length();
    }

    private long getIndex(long timestamp) {
        return Duration.of(timestamp, ChronoUnit.MILLIS).get(groupingUnit);
    }

    private long getTargetIndex(long timestamp) {
        return Duration.of(timestamp, ChronoUnit.MILLIS).minus(targetDuration, targetUnit).get(groupingUnit);
    }

    @VisibleForTesting
    protected AtomicMetric<V> getAtomicMetric(long timestamp) {
        long index = getIndex(timestamp);
        int offset = getOffset(index);
        return array.updateAndGet(offset, value -> initial(index, value));
    }

    private Stream<V> getStream() {
        long now = System.currentTimeMillis();
        long lastIndex = getIndex(now);
        long firstIndex = getTargetIndex(now);
        return LongStream.rangeClosed(firstIndex, lastIndex)
                .mapToObj(index -> filter(index, array.get(getOffset(index))))
                .filter(Objects::nonNull)
                .map(AtomicMetric::getValue);
    }

    private AtomicMetric<V> initial(long index, AtomicMetric<V> value) {
        return value == null || value.getIndex() < index? new AtomicMetric<>(factory.get(), index):value;
    }

    private AtomicMetric<V> filter(long index, AtomicMetric<V> value) {
        return value != null && value.getIndex() == index?value:null;
    }

    @VisibleForTesting
    protected static class AtomicMetric<V> implements Serializable {

        private static final long serialVersionUID = -1443325798845332397L;
        private final AtomicReference<V> value;
        private final long index;

        public AtomicMetric(V value, long index) {
            this.value = new AtomicReference<>(value);
            this.index = index;
        }

        public void updateAndGet(UnaryOperator<V> newValue) {
            value.updateAndGet(newValue);
        }

        public V getValue() {
            return value.get();
        }

        public long getIndex() {
            return index;
        }
    }
}
