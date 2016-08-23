package com.pocketmath.stasov.util.multimaps;

import java.util.Set;

/**
 * Created by etucker on 3/19/16.
 */
public interface IMultiValueMap<K,V> {

    public abstract void put(final K key, final V value);

    public abstract boolean containsKey(final K key);

    public Set<V> get(final K key);

    public void remove(final K key);

    public void remove(final K key, final V value);

    public int occurrences(final K key);

    public Set<K> getKeys();

    public long size();

    public boolean matchesAll(K[] keys, V value);

}
