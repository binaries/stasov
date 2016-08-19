package com.pocketmath.stasov.util.optimizedrangemap;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Created by etucker on 2/6/16.
 */
public abstract class AbstractOptiDoubleRangeMap<T extends Comparable<T>, E extends OptiDoubleRMEntry<T>> extends AbstractDoubleScaleFactorOptiRangeMap<T,Double,E> {

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

    public void forEachEntry(final Double x, final Consumer<AbstractOptiDoubleRangeMap<T,E>> consumer) {
        forEachEntry(x.doubleValue(), consumer);
    }

}
