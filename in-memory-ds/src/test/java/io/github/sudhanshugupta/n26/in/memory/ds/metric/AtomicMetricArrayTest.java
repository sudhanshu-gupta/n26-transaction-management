package io.github.sudhanshugupta.n26.in.memory.ds.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.github.sudhanshugupta.n26.in.memory.ds.util.TestMetric;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.Before;
import org.junit.Test;

public class AtomicMetricArrayTest {

    private AtomicMetricArray<TestMetric> atomicMetricArray;
    private long NOW;

    @Before
    public void setUp() throws Exception {
        atomicMetricArray = new AtomicMetricArray<>(1, ChronoUnit.MINUTES, ChronoUnit.SECONDS, ()->TestMetric.ZERO, 60);
        NOW = System.currentTimeMillis();
    }

    @Test
    public void shouldAddOrUpdateSingleValue() {
        atomicMetricArray.addOrUpdate(NOW, testMetric -> testMetric.add(10l));
        long value = atomicMetricArray.getAtomicMetric(NOW).getValue().getSum();
        assertEquals(10L, value);
    }

    @Test
    public void shouldAddOrUpdateMultipleValue() {
        atomicMetricArray.addOrUpdate(NOW, testMetric -> testMetric.add(10l));
        atomicMetricArray.addOrUpdate(NOW, testMetric -> testMetric.add(5l));
        atomicMetricArray.addOrUpdate(NOW, testMetric -> testMetric.add(15l));
        long value = atomicMetricArray.getAtomicMetric(NOW).getValue().getSum();
        assertEquals(30L, value);
    }

    @Test
    public void shouldAddOrUpdateMultipleValueMultiIndex() {
        atomicMetricArray.addOrUpdate(NOW, testMetric -> testMetric.add(10l));
        long onePlusNow = Duration.of(NOW, ChronoUnit.MILLIS).minus(1, ChronoUnit.SECONDS).toMillis();
        atomicMetricArray.addOrUpdate(onePlusNow, testMetric -> testMetric.add(5l));
        long twoPlusNow = Duration.of(NOW, ChronoUnit.MILLIS).minus(2, ChronoUnit.SECONDS).toMillis();
        atomicMetricArray.addOrUpdate(twoPlusNow, testMetric -> testMetric.add(15l));
        long value1 = atomicMetricArray.getAtomicMetric(NOW).getValue().getSum();
        assertEquals(10L, value1);
        long value2 = atomicMetricArray.getAtomicMetric(onePlusNow).getValue().getSum();
        assertEquals(5L, value2);
        long value3 = atomicMetricArray.getAtomicMetric(twoPlusNow).getValue().getSum();
        assertEquals(15L, value3);
    }

    @Test
    public void shouldReduceSingleValue() {
        atomicMetricArray.addOrUpdate(NOW, testMetric -> testMetric.add(10l));
        TestMetric testMetric = atomicMetricArray.reduce(TestMetric::merge);
        assertNotNull(testMetric);
        assertEquals(10L, testMetric.getSum());
    }

    @Test
    public void shouldReduceMultipleValue() {
        atomicMetricArray.addOrUpdate(NOW, testMetric -> testMetric.add(10l));
        atomicMetricArray.addOrUpdate(NOW, testMetric -> testMetric.add(5l));
        atomicMetricArray.addOrUpdate(NOW, testMetric -> testMetric.add(15l));
        TestMetric testMetric = atomicMetricArray.reduce(TestMetric::merge);
        assertNotNull(testMetric);
        assertEquals(30L, testMetric.getSum());
    }

    @Test
    public void shouldReduceMultipleValueMultiIndex() {
        atomicMetricArray.addOrUpdate(NOW, testMetric -> testMetric.add(10l));
        long onePlusNow = Duration.of(NOW, ChronoUnit.MILLIS).minus(1, ChronoUnit.SECONDS).toMillis();
        atomicMetricArray.addOrUpdate(onePlusNow, testMetric -> testMetric.add(5l));
        long twoPlusNow = Duration.of(NOW, ChronoUnit.MILLIS).minus(2, ChronoUnit.SECONDS).toMillis();
        atomicMetricArray.addOrUpdate(twoPlusNow, testMetric -> testMetric.add(15l));
        TestMetric testMetric = atomicMetricArray.reduce(TestMetric::merge);
        assertNotNull(testMetric);
        assertEquals(30L, testMetric.getSum());
    }
}