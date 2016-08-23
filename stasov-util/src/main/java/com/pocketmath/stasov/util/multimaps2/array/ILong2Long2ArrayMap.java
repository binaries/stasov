package com.pocketmath.stasov.util.multimaps2.array;

import it.unimi.dsi.fastutil.longs.LongSet;

/**
 * Created by etucker on 8/22/16.
 */
public interface ILong2Long2ArrayMap<V> {

    public IArraySet<V> getArraySet(final long key1, final long key2);

    public void put(final long key1, final long key2, final V value);

    public void put(final long key1, final long[] keys2, final V value);

    public boolean containsKey(final long key1, final long key2);

    public boolean containsKey(final long key1);

    public LongSet getKeys1();

    public LongSet getKeys2(final long key1);

    public void remove(final long key1);

    public void remove(final long key1, final long key2);

    public void remove(final long key1, final long key2, final V value);

    public boolean isEmpty();

    public String prettyPrint(String prefix, String key1Prefix, String key2Prefix, String valuePrefix);

    public String prettyPrint(String prefix);

}
