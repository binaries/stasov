package com.pocketmath.stasov.util.multimaps;

import com.pocketmath.stasov.util.PrettyPrintable;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;

import java.util.Set;

/**
 * Created by etucker on 2/2/16.
 */
public interface ILong2Long2ObjectMultiValueMap<V> extends PrettyPrintable {

    public void put(final long key1, final long key2, final V value);

    public void put(final long key1, final long[] keys2, final V value);

    public boolean containsKey(final long key1, final long key2);

    public boolean containsKey(final long key1);

    public ObjectSet<V> get(final long key1, final long key2);

    public ObjectSets.UnmodifiableSet<V> getUnmodifiable(final long key1, final long key2);

    public LongSet getKeys1();

    public LongSet getKeys2(final long key1);

    public void remove(final long key1);

    public void remove(final long key1, final long key2);

    public void remove(final long key1, final long key2, final long value);

    /**
     *
     * @param key1
     * @param keys2
     * @param value
     * @return true if all the keys2 are found
     */
    public boolean matchesAll(final long key1, final long[] keys2, final V value);

    public boolean isEmpty();

    public String prettyPrint(String prefix, String key1Prefix, String key2Prefix, String valuePrefix);
}
