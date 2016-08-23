package com.pocketmath.stasov.util.multimaps;

import com.pocketmath.stasov.util.IndexAlgorithm;
import net.nicoulaj.compilecommand.annotations.Inline;

/**
 * Created by etucker on 8/20/16.
 */
public abstract class Long2Long2ObjectMultiValueReadCachedHashMap<V extends Comparable<V>> extends Long2Long2ObjectMultiValueHashMap<V> {

    private class SecondLevelMap extends Long2ObjectMultiValueHashReadCachedMap<V> {
        @Override
        protected V[] newValuesArray(final int size) {
            return newValuesArrayProxy(size);
        }
    }

    @Inline
    private V[] newValuesArrayProxy(final int size) {
        return newValuesArray(size);
    }

    protected abstract V[] newValuesArray(final int size);

    public Long2Long2ObjectMultiValueReadCachedHashMap() {
        super();
    }

    @Override
    protected ILong2ObjectMultiValueMap<V> newSecondLevelMap() {
        return new SecondLevelMap();
    }
}
