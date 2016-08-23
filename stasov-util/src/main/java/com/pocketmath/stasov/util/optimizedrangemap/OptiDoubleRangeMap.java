package com.pocketmath.stasov.util.optimizedrangemap;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by etucker on 2/7/16.
 */
public class OptiDoubleRangeMap<T extends Comparable<T> & Serializable> extends AbstractOptiDoubleRangeMap<T,OptiDoubleRMEntry<T>> {

    @Override
    protected OptiDoubleRMEntry<T> newEntry(@Nonnull final Double x0, @Nonnull final Double x1, @Nonnull final T t) {
        return new OptiDoubleRMEntry<T>(x0, x1, t);
    }

}
