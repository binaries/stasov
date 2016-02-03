package com.pocketmath.stasov.util.multimaps;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;

/**
 * Created by etucker on 2/2/16.
 */
public interface ILong2ObjectMultiValueMap<V> {

    public abstract void put(final long key, final V value);

    public abstract boolean containsKey(final long key);

    public ObjectSet<V> get(final long key);

    public void remove(final long key);

    public void remove(final long key, final V value);

    public int occurrences(final long key);

    public LongSet getKeys();


}
