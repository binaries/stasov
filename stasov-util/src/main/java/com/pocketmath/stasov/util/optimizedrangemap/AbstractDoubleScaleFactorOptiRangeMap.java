package com.pocketmath.stasov.util.optimizedrangemap;

/**
 * Created by etucker on 2/7/16.
 */
abstract class AbstractDoubleScaleFactorOptiRangeMap<T extends Comparable<T>, K extends Comparable<K>, E extends AbstractOptiRMEntry<T,K>> extends OptiRangeMap<T,K,E> {

    protected final double scaleFactor;
    protected final double scaleConst;

    protected AbstractDoubleScaleFactorOptiRangeMap(
            final double scaleFactor,
            final double tuneFactor,
            final long initialInterval,
            final int newIntervalCalculationAfterNPuts) {
        super(tuneFactor, initialInterval, newIntervalCalculationAfterNPuts);

        if (scaleFactor <= 1000D ) throw new IllegalArgumentException();
        this.scaleFactor = scaleFactor;

        final double scaleConst = Long.MAX_VALUE / 2 - getScaleFactor() / 2;
        if (scaleConst <= 0) throw new IllegalStateException("error calculating scaling");
        this.scaleConst = scaleConst;
    }

    protected AbstractDoubleScaleFactorOptiRangeMap(final double scaleFactor) {
        super();
        if (scaleFactor <= 1000D ) throw new IllegalArgumentException();
        this.scaleFactor = scaleFactor;

        final double scaleConst = Long.MAX_VALUE / 2 - getScaleFactor() / 2;
        if (scaleConst <= 0) throw new IllegalStateException("error calculating scaling");
        this.scaleConst = scaleConst;
    }

    protected AbstractDoubleScaleFactorOptiRangeMap() {
        this(10*1000*1000d);
    }

    protected double getScaleFactor() {
        return scaleFactor;
    }

    protected double getScaleConst() {
        return scaleConst;
    }

}
