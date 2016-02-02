package com.pocketmath.stasov.util.multimaps;

import it.unimi.dsi.fastutil.objects.ObjectSortedSet;

/**
 * Created by etucker on 2/2/16.
 */
public interface ILong2ObjectMultiValueSortedMap<V extends Comparable<V>> extends ILong2ObjectMultiValueMap<V> {

    public ObjectSortedSet<V> getSorted(final long key);

}
