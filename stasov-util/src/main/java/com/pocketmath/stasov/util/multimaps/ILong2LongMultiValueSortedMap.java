package com.pocketmath.stasov.util.multimaps;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;

/**
 * Created by etucker on 8/14/16.
 */
public interface ILong2LongMultiValueSortedMap {
    void put(long key, long value);

    void fastPut(long key, LongSortedSet set);

    LongSortedSet getSorted(long key);

    boolean contains(long key, long value);

    boolean containsKey(long key);

    void remove(long key, long value);

    void remove(long key);

    LongSet getKeys();

    void clear();
}
