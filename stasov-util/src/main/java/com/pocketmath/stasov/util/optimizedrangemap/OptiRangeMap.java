package com.pocketmath.stasov.util.optimizedrangemap;

import com.pocketmath.stasov.util.IndexAlgorithm;
import com.pocketmath.stasov.util.multimaps.ILong2ObjectMultiValueMap;
import com.pocketmath.stasov.util.multimaps.ILong2ObjectMultiValueSortedMap;
import com.pocketmath.stasov.util.multimaps.Long2ObjectMultiValueHashMap;
import com.pocketmath.stasov.util.multimaps.Long2ObjectMultiValueSortedMap;
import com.pocketmath.stasov.util.validate.ValidationException;
import com.pocketmath.stasov.util.validate.ValidationRuntimeException;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import javax.annotation.Nonnull;
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
public abstract class OptiRangeMap<T extends Comparable<T>, K extends Comparable<K>, E extends AbstractOptiRMEntry<T,K>> {

    public static final int DEFAULT_MAINTENANCE_ITEMS_PER_CALL = 50;

    private ILong2ObjectMultiValueMap<E> intervalToEntries =
            new Long2ObjectMultiValueHashMap<E>();

    // TODO: Use hashmap for better performance?
    private ILong2ObjectMultiValueMap<E> map =
            new Long2ObjectMultiValueHashMap<E>(); // Comparator.naturalOrder(), IndexAlgorithm.AVL);

    private final double tuneFactor;

    private final int newIntervalCalculationAfterNPuts; // TODO: count will be imperfect as this is not synchronized

    private final long initialInterval;

    private long interval;

    private long putsSinceLastIntervalCalculation = 0;

    public OptiRangeMap(
            final double tuneFactor,
            final long initialInterval,
            final int newIntervalCalculationAfterNPuts) {
        if (tuneFactor <= 0D ) throw new IllegalArgumentException();
        if (tuneFactor > 10D ) throw new IllegalArgumentException();
        if (initialInterval < 100D) throw new IllegalArgumentException();
        if (newIntervalCalculationAfterNPuts < 1) throw new IllegalArgumentException();
        this.tuneFactor = tuneFactor;
        this.initialInterval = initialInterval;
        this.interval = initialInterval;
        this.newIntervalCalculationAfterNPuts = newIntervalCalculationAfterNPuts;
    }

    public OptiRangeMap() {
        this(1.5d, 1*1000*1000, 100);
    }

    protected abstract long calcScaled(@Nonnull final K x);

    protected abstract E newEntry(@Nonnull final K x0, @Nonnull final K x1, @Nonnull final T t);

    public void put(@Nonnull final K x0, @Nonnull final K x1, @Nonnull final T t) {
        final long x0scaled = calcScaled(x0);
        final long x1scaled = calcScaled(x1);
        try {
            put(newEntry(x0, x1, t));
        } catch (ValidationException ve) {
            throw new ValidationRuntimeException(ve);
        }
    }

    protected void fastPut(@Nonnull final E entry) {
        final long x0scaled = calcScaled(entry.getX0());
        final long x1scaled = calcScaled(entry.getX1());
        final T t = entry.getT();

        if (t == null) throw new IllegalStateException();

        intervalToEntries.put(interval, entry);
        for (long i = x0scaled; i < x1scaled; i = Math.addExact(i, interval)) map.put(i, entry);
    }

    protected void put(@Nonnull final E entry) throws ValidationException {
        entry.validate();
        fastPut(entry);
    }

    public void forEachEntry(final K x, final Consumer<E> consumer) {
        final long xscaled = calcScaled(x);
        for (final long interval : getIntervals())
            for (final E entry : getPossibilities(xscaled, interval))
                if (entry.inBounds(x))
                    consumer.accept(entry);
    }

    protected ObjectSet<E> getPossibilities(final long x, final long interval) {
        return map.get( x - x % interval );
    }

    protected LongSet getIntervals() {
        return intervalToEntries.getKeys();
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

    private void doMaintain(final int n) {
        if (n <= 0) throw new IllegalArgumentException();

        if (putsSinceLastIntervalCalculation >= newIntervalCalculationAfterNPuts) {
            interval = calculateNewInterval();
            putsSinceLastIntervalCalculation = 0;
        }

        int i = 0;
        for (final long interval : intervalToEntries.getKeys()) {
            if (interval == this.interval) continue;
            for (final E entry : intervalToEntries.get(interval)) {
                if (i >= n) return;
                try {
                    final int startCount = intervalToEntries.get(interval).size() + intervalToEntries.get(this.interval).size();

                    fastPut(entry);
                    intervalToEntries.remove(interval, entry);

                    final int endCount = intervalToEntries.get(interval).size() + intervalToEntries.get(this.interval).size();
                    if (startCount != endCount) throw new IllegalStateException();
                } finally {
                    i++;
                }
            }
        }
    }

    /**
     *
     * @param maxItems The maximum number of items (or entries) upon which to perform maintenance.
     */
    public synchronized void maintain(final int maxItems) {
        doMaintain(maxItems);
    }

    public synchronized void maintain() {
        doMaintain(DEFAULT_MAINTENANCE_ITEMS_PER_CALL);
    }

}
