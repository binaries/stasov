package com.pocketmath.stasov.util.optimizedrangemap;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Created by etucker on 2/6/16.
 */
public class OptiDoubleRangeMap<T extends Comparable<T>> extends AbstractDoubleScaleFactorOptiRangeMap<T,Double,OptiDoubleRMEntry<T>> {

    public OptiDoubleRangeMap(double scaleFactor, double tuneFactor, long initialInterval, int newIntervalCalculationAfterNPuts) {
        super(scaleFactor, tuneFactor, initialInterval, newIntervalCalculationAfterNPuts);
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

    @Override
    protected OptiDoubleRMEntry<T> newEntry(@Nonnull final Double x0, @Nonnull final Double x1, @Nonnull final T t) {
        return new OptiDoubleRMEntry<T>(x0, x1, t);
    }

    public void forEach(final double x, final Consumer<T> consumer) {
        final long xscaled = calcScaled(x);
        for (final long interval : getIntervals())
            for (final OptiDoubleRMEntry<T> entry : getPossibilities(xscaled, interval))
                if (entry.doubleInBounds(x))
                    consumer.accept(entry.getT());
    }

}
