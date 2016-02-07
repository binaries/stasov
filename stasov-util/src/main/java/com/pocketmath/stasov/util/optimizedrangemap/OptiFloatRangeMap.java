package com.pocketmath.stasov.util.optimizedrangemap;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Created by etucker on 2/6/16.
 */
public class OptiFloatRangeMap<T extends Comparable<T>> extends AbstractDoubleScaleFactorOptiRangeMap<T,Float, OptiFloatRMEntry<T>> {

    public OptiFloatRangeMap(double scaleFactor, double tuneFactor, long initialInterval, int newIntervalCalculationAfterNPuts) {
        super(scaleFactor, tuneFactor, initialInterval, newIntervalCalculationAfterNPuts);
    }

    @Override
    protected long calcScaled(@Nonnull final Float x) {
        return calcScaled(x.floatValue());
    }

    protected long calcScaled(final float x) {
        if (x >= scaleConst) throw new IllegalArgumentException(); // TODO: doesn't cover all edge cases
        final double xscaledd = (x * getScaleFactor() + getScaleConst());
        if (xscaledd > Long.MAX_VALUE) throw new ArithmeticException("too big");
        return (long) xscaledd;
    }

    @Override
    protected OptiFloatRMEntry<T> newEntry(@Nonnull final Float x0, @Nonnull final Float x1, @Nonnull final T t) {
        return new OptiFloatRMEntry<T>(x0, x1, t);
    }

    public void forEach(final float x, final Consumer<T> consumer) {
        final long xscaled = calcScaled(x);
        for (final long interval : getIntervals())
            for (final OptiFloatRMEntry<T> entry : getPossibilities(xscaled, interval))
                if (entry.floatInBounds(x))
                    consumer.accept(entry.getT());
    }

}
