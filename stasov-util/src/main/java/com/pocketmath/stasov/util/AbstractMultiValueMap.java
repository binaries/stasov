package com.pocketmath.stasov.util;

import java.util.Comparator;

/**
 * Created by etucker on 3/26/15.
 */
public class AbstractMultiValueMap<V extends Comparable<V>> {

    // Using a Comparator improves performance by avoiding casting to Comparable type inside the
    // compare methods of data structures holding the values.
    protected final Comparator<V> valueComparator;

    protected final TreeAlgorithm treeAlgorithm;

    public AbstractMultiValueMap(final Comparator<V> valueComparator, final TreeAlgorithm treeAlgorithm) {
        this.valueComparator = valueComparator;
        this.treeAlgorithm = treeAlgorithm;
    }

}
