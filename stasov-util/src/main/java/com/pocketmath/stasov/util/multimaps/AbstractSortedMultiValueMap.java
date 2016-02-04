package com.pocketmath.stasov.util.multimaps;

import com.pocketmath.stasov.util.IndexAlgorithm;

import java.util.Comparator;

/**
 * Created by etucker on 3/26/15.
 */
abstract class AbstractSortedMultiValueMap<V extends Comparable<V>> extends AbstractMultiValueMap<V> {

    // Using a Comparator improves performance by avoiding casting to Comparable type inside the
    // compare methods of data structures holding the values.
    protected final Comparator<V> valueComparator;

    protected final IndexAlgorithm indexAlgorithm;

    public AbstractSortedMultiValueMap(final Comparator<V> valueComparator, final IndexAlgorithm indexAlgorithm) {
        this.valueComparator = valueComparator;
        this.indexAlgorithm = indexAlgorithm;
    }

}
