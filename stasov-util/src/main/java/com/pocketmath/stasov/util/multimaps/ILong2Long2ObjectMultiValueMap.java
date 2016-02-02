package com.pocketmath.stasov.util.multimaps;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.Set;

/**
 * Created by etucker on 2/2/16.
 */
public interface ILong2Long2ObjectMultiValueMap<V> {

    public void put(final long key1, final long key2, final V value);

    public boolean containsKey(final long key1, final long key2);

    public boolean containsKey(final long key1);

    public ObjectSet<V> get(final long key1, final long key2);

    public LongSet getKeys2(final long key1);

    public void remove(final long key1);

    public void remove(final long key1, final long key2);

}
