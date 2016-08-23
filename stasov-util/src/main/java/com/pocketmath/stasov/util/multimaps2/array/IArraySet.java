package com.pocketmath.stasov.util.multimaps2.array;

import java.util.Collection;
import java.util.Set;

/**
 * Created by etucker on 8/23/16.
 */
public interface IArraySet<V> {
    int startIndex();

    int endIndex();

    V fastGet(int index);

    V get(int index);

    int size();

    boolean isEmpty();

    void fastAdd(V value);

    boolean add(V value);

    void addAll(Collection<V> values);

    void addAll(V[] values);

    boolean remove(V value);

    void removeAll(Set<V> values);

    void readOptimize();

    void writeOptimize();

    void clear();

    boolean contains(V value);
}
