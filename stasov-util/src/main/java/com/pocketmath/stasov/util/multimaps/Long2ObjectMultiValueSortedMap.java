package com.pocketmath.stasov.util.multimaps;

import com.pocketmath.stasov.util.IndexAlgorithm;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by etucker on 3/21/15.
 */
public class Long2ObjectMultiValueSortedMap<V extends Comparable<V>> extends AbstractSortedMultiValueMap<V>
    implements ILong2ObjectMultiValueSortedMap<V> {

    private final Long2ObjectMap<ObjectSortedSet<V>> map;

    private long size = 0;

    public Long2ObjectMultiValueSortedMap(final Comparator<V> valueComparator, final IndexAlgorithm indexAlgorithm) {
        super(valueComparator, indexAlgorithm);
        switch (indexAlgorithm) {
            case REDBLACK:  { map = new Long2ObjectRBTreeMap<ObjectSortedSet<V>>(); break; }
            case AVL: { map = new Long2ObjectAVLTreeMap<ObjectSortedSet<V>>(); break; }
            default: { throw new IllegalStateException(); }
        }
    }

    public Long2ObjectMultiValueSortedMap(final Comparator<V> valueComparator) {
        this(valueComparator, IndexAlgorithm.AVL);
    }

    public Long2ObjectMultiValueSortedMap() {
        this(Comparator.naturalOrder());
    }

    public void put(long key, V value) {
        ObjectSortedSet<V> set = map.get(key);
        if (set == null) {
            switch(indexAlgorithm) {
                case REDBLACK:  { set = new ObjectRBTreeSet<V>(valueComparator); break; }
                case AVL:       { set = new ObjectAVLTreeSet<V>(valueComparator); break; }
                default: { throw new IllegalStateException(); }
            }
            map.put(key, set);
        }
        if (set.add(value)) size++;
    }

    public ObjectSortedSet<V> getSorted(final long key) {
        return map.get(key);
    }

    public ObjectSet<V> get(final long key) {
        return getSorted(key);
    }

    public ObjectSortedSets.UnmodifiableSortedSet<V> getUnmodifiable(final long key) {
        throw new UnsupportedOperationException("not yet supported");
    }

    public boolean contains(final long key, final V value) {
        ObjectSortedSet<V> set = map.get(key);
        if (set == null) return false;
        return set.contains(value);
    }

    @Override
    public int occurrences(final long key) {
        final ObjectSet<V> set = map.get(key);
        if (set == null) return 0;
        if (set.isEmpty()) throw new IllegalStateException("empty set is not supposed to exist");
        return set.size();
    }

    public boolean containsKey(final long key) {
        return map.containsKey(key);
    }

    public boolean matchesAll(final long[] keys, final V value) {
        if (value == null) throw new IllegalArgumentException("cannot compare null value");
        for (final long key: keys) {
            ObjectSortedSet<V> set = map.get(key);
            if (set == null) return false;
            if (!set.contains(value)) return false;
        }
        return true;
    }

    public void remove(final long key, final long value) {
        final ObjectSet set = map.get(key);
        if (set != null)
            if (set.remove(value)) size--;
        else return;
        if (set.isEmpty()) map.remove(key);
    }

    @Override
    public void remove(final long key, final V value) {
        final ObjectSet set = map.get(key);
        if (set != null)
            if (set.remove(value)) size--;
        else return;
        if (set.isEmpty()) map.remove(key);
    }

    @Override
    public void remove(final long key) {
        final ObjectSortedSet<V> removed = map.remove(key);
        if (removed == null) return;
        size -= removed.size();
    }

    @Override
    public LongSet getKeys() {
        return map.keySet();
    }

    public void clear() {
        map.clear();
        size = 0;
    }

    public ObjectSet<Map.Entry<Long, ObjectSortedSet<V>>> entrySet() {
        return map.entrySet();
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public String toString() {
        return "Long2ObjectMultiValueMap{" +
                "multiValueMap=" + map +
                '}';
    }
}
