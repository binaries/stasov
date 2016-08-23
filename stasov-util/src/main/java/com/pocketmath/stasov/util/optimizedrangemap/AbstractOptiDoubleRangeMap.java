package com.pocketmath.stasov.util.optimizedrangemap;

import com.pocketmath.stasov.scoring.ScoredResultWithMeta;
import com.pocketmath.stasov.scoring.ScoredResultWithMetaConsumer;
import com.pocketmath.stasov.scoring.Scorer2D;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Created by etucker on 2/6/16.
 */
public abstract class AbstractOptiDoubleRangeMap<T extends Comparable<T> & Serializable, E extends OptiDoubleRMEntry<T>> extends AbstractDoubleScaleFactorOptiRangeMap<T,Double,E> {

    public AbstractOptiDoubleRangeMap(double scaleFactor, double tuneFactor, long initialInterval, int newIntervalCalculationAfterNPuts) {
        super(scaleFactor, tuneFactor, initialInterval, newIntervalCalculationAfterNPuts);
    }

    public AbstractOptiDoubleRangeMap() {
        super();
    }

    @Override
    protected long calcScaled(@Nonnull final Double x) {
        return calcScaled(x);
    }

    protected long calcScaled(final double x) {
        if (x >= scaleConst) throw new IllegalArgumentException(); // TODO: doesn't cover all edge cases
        final double xscaledd = (x * getScaleFactor() + getScaleConst());
        if (xscaledd > Long.MAX_VALUE) throw new ArithmeticException("too big");
        return (long) xscaledd;
    }

    protected abstract E newEntry(@Nonnull final Double x0, @Nonnull final Double x1, @Nonnull final T t);

    public void forEachEntry(final double x, final Consumer<OptiDoubleRMEntry<T>> consumer) {
        final long xscaled = calcScaled(x);
        for (final long interval : getIntervals())
            for (final OptiDoubleRMEntry<T> entry : getPossibilities(xscaled, interval))
                if (entry.doubleInBounds(x))
                    consumer.accept(entry);
    }

    /**
     *
     * @param x0 First dimension point.
     * @param x1 Second dimension point.
     * @param resultsConsumer
     * @param sf
     */
    public <MetaType extends Serializable> void forEachScored2DEntry(
            final double x0,
            final double x1,
            //@Nonnull final Consumer<ScoredResultWithMeta<T, MetaType>> resultsConsumer,
            @Nonnull final ScoredResultWithMetaConsumer resultsConsumer,
            @Nonnull final Scorer2D sf) {

        final long x0scaled = calcScaled(x0);
        for (final long interval : getIntervals()) {
            for (final OptiDoubleRMEntry<T> entry : getPossibilities(x0scaled, interval)) {
                if (entry.doubleInBounds(x0)) {
                    sf.scoreWithMeta(x0, x1, entry.getT(), resultsConsumer);
                }
            }
        }
    }

}
