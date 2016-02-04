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
    HASH}
