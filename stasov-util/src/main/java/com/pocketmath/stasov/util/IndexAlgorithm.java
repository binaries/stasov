package com.pocketmath.stasov.util;

/**
* Created by etucker on 3/22/15.
*/
public enum IndexAlgorithm {
    /**
     *  Better for frequent reads and writes with compactness.
     */
    REDBLACK,
    /**
     * Better for reads with compactness.
     */
    AVL,
    /**
     * Better for constant time reads and writes.  Not compact.
     */
    HASH,
    /**
     * Compact with fast iteration performance for small sets or infrequent lookups.
     */
    ARRAY,
    /**
     * Structures may change based on the size of what's being stored.
     */
    DYNAMIC1
}
