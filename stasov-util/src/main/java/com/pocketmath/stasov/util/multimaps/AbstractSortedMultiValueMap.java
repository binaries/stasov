package com.pocketmath.stasov.util.multimaps;

import com.pocketmath.stasov.util.TreeAlgorithm;

import java.util.Comparator;

/**
 * Created by etucker on 3/26/15.
 */
abstract class AbstractSortedMultiValueMap<V extends Comparable<V>> extends AbstractMultiValueMap<V> {

    // Using a Comparator improves performance by avoiding casting to Comparable type inside the
    // compare methods of data structures holding the values.
    protected final Comparator<V> valueComparator;

    protected final TreeAlgorithm treeAlgorithm;

    public AbstractSortedMultiValueMap(final Comparator<V> valueComparator, final TreeAlgorithm treeAlgorithm) {
        this.valueComparator = valueComparator;
        this.treeAlgorithm = treeAlgorithm;
    }

}
