package com.pocketmath.stasov.util.optimizedrangemap;

import com.pocketmath.stasov.util.IndexAlgorithm;
import com.pocketmath.stasov.util.multimaps.ILong2ObjectMultiValueMap;
import com.pocketmath.stasov.util.multimaps.ILong2ObjectMultiValueSortedMap;
import com.pocketmath.stasov.util.multimaps.Long2ObjectMultiValueSortedMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * Optimized to maintain fast range matching with adaptive block (interval) size.
 * Maintenance operations can iterate over as little as one entry at a time thereby
 * preventing longer "stop the world" events.
 *
 * Created by etucker on 2/2/16.
 */
public class OptimizedRangeMap<T extends Comparable<T>> {

    public static final int DEFAULT_MAINTENANCE_ITEMS_PER_CALL = 50;

    private ILong2ObjectMultiValueSortedMap<OptRMEntry<T>> intervalToEntries =
            new Long2ObjectMultiValueSortedMap<OptRMEntry<T>>();

    // TODO: Use hashmap for better performance?
    private ILong2ObjectMultiValueMap<OptRMEntry<T>> map =
            new Long2ObjectMultiValueSortedMap<OptRMEntry<T>>(Comparator.naturalOrder(), IndexAlgorithm.AVL);

    private final double tuneFactor;

    private final double scaleFactor;
    private final double scaleConst;

    private final int newIntervalCalculationAfterNPuts; // TODO: count will be imperfect as this is not synchronized

    private final long initialInterval;

    private long interval;

    private long putsSinceLastIntervalCalculation = 0;

    public OptimizedRangeMap(
            final double scaleFactor,
            final double tuneFactor,
            final long initialInterval,
            final int newIntervalCalculationAfterNPuts) {
        if (scaleFactor <= 1000D ) throw new IllegalArgumentException();
        if (tuneFactor <= 0D ) throw new IllegalArgumentException();
        if (tuneFactor > 10D ) throw new IllegalArgumentException();
        if (initialInterval < 100D) throw new IllegalArgumentException();
        if (newIntervalCalculationAfterNPuts < 1) throw new IllegalArgumentException();
        this.scaleFactor = scaleFactor;
        this.tuneFactor = tuneFactor;

        final double scaleConst = Long.MAX_VALUE / 2 - scaleFactor / 2;
        if (scaleConst <= 0) throw new IllegalStateException("error calculating scaling");
        this.scaleConst = scaleConst;

        this.initialInterval = initialInterval;
        this.interval = initialInterval;

        this.newIntervalCalculationAfterNPuts = newIntervalCalculationAfterNPuts;
    }

    private long calcScaled(final float x) {
        if (x >= scaleConst) throw new IllegalArgumentException(); // TODO: doesn't cover all edge cases
        final double xscaledd = (x * scaleFactor + scaleConst);
        if (xscaledd > Long.MAX_VALUE) throw new ArithmeticException("too big");
        return (long) xscaledd;
    }

    public OptimizedRangeMap() {
        this(10*1000*1000d, 1.5d, 1*1000*1000, 100);
    }

    private void put(final long x0, final long x1, T t) {
        try {
            if (x0 > x1) throw new IllegalArgumentException();
            OptRMEntry<T> entry = new OptRMEntry(x0, x1, t);
            intervalToEntries.put(interval, entry);
            for (long i = x0; i < x1; i = Math.addExact(i, interval)) {
                map.put(i, entry);
            }
        } finally {
            putsSinceLastIntervalCalculation++;
        }
    }

    private ObjectSet<OptRMEntry<T>> getPossibilities(final long x, final long interval) {
        return map.get( x - x % interval );
    }

    public synchronized void put(final float x0, final float x1, T t) {
        final long x0scaled = calcScaled(x0);
        final long x1scaled = calcScaled(x1);
        put(x0scaled, x1scaled, t);
    }

    public LongSet getIntervals() {
        return intervalToEntries.getKeys();
    }

    /**
     * To get all matches call this call over all intervals.
     *
     * @param x
     * @param interval
     * @return
     */
    protected ObjectSet<OptRMEntry<T>> getPossibilities(final float x, final long interval) {
        final long xscaled = calcScaled(x);
        return getPossibilities(xscaled, interval);
    }

    public void getForEach(final float x, final Consumer<T> consumer) {
        final long xscaled = calcScaled(x);
        for (final long interval : getIntervals())
            for (final OptRMEntry<T> entry : getPossibilities(xscaled, interval))
                if (x >= entry.getX0() && x < entry.getX1())
                    consumer.accept(entry.getT());
    }

    private long calculateNewInterval() {
        final int maxOccurrences = 10;

        final LongSet keys = map.getKeys();

        // determine new interval
        long newInterval = this.interval;
        for (final long key : keys) {
            final int occurrences = map.occurrences(key);
            if (occurrences > maxOccurrences) {
                newInterval *= tuneFactor;
                break;
            }
        }
        // Change is important.  Ensure it.
        if (newInterval == interval) newInterval *= 1D / tuneFactor;
        if (newInterval == interval) newInterval = (long)(newInterval * 0.75d) + ThreadLocalRandom.current().nextLong(newInterval) / 2;
        if (newInterval == interval) newInterval = (long)(initialInterval * 0.75d) + ThreadLocalRandom.current().nextLong(initialInterval) / 2;
        if (newInterval == interval) newInterval = initialInterval;
        if (newInterval == interval) throw new IllegalStateException();

        newInterval = Math.max(1L, newInterval);
        newInterval = Math.min(Long.MAX_VALUE / 1024, newInterval);

        return newInterval;
    }

    public synchronized void maintain(final int n) {
        if (n <= 0) throw new IllegalArgumentException();

        if (putsSinceLastIntervalCalculation >= newIntervalCalculationAfterNPuts) {
            interval = calculateNewInterval();
            putsSinceLastIntervalCalculation = 0;
        }

        int i = 0;
        for (final long interval : intervalToEntries.getKeys()) {
            if (interval == this.interval) continue;
            for (final OptRMEntry<T> entry : intervalToEntries.get(interval)) {
                if (i >= n) return;
                try {
                    final int startCount = intervalToEntries.get(interval).size() + intervalToEntries.get(this.interval).size();

                    put(entry.getX0(), entry.getX1(), entry.getT());
                    intervalToEntries.remove(interval, entry);

                    final int endCount = intervalToEntries.get(interval).size() + intervalToEntries.get(this.interval).size();
                    if (startCount != endCount) throw new IllegalStateException();
                } finally {
                    i++;
                }
            }
        }
    }

    public void maintain() {
        maintain(DEFAULT_MAINTENANCE_ITEMS_PER_CALL);
    }

}
