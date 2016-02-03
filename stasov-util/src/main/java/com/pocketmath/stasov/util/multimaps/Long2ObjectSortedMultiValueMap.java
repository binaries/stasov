package com.pocketmath.stasov.util.multimaps;

import com.pocketmath.stasov.util.TreeAlgorithm;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by etucker on 3/21/15.
 */
public class Long2ObjectSortedMultiValueMap<V extends Comparable<V>> extends AbstractSortedMultiValueMap<V>
    implements ILong2ObjectMultiValueSortedMap<V> {

    private final Long2ObjectMap<ObjectSortedSet<V>> map;

    public Long2ObjectSortedMultiValueMap(final Comparator<V> valueComparator, final TreeAlgorithm treeAlgorithm) {
        super(valueComparator, treeAlgorithm);
        switch (treeAlgorithm) {
            case REDBLACK:  { map = new Long2ObjectRBTreeMap<ObjectSortedSet<V>>(); break; }
            case AVL: { map = new Long2ObjectAVLTreeMap<ObjectSortedSet<V>>(); break; }
            default: { throw new IllegalStateException(); }
        }
    }

    public Long2ObjectSortedMultiValueMap(final Comparator<V> valueComparator) {
        this(valueComparator, TreeAlgorithm.AVL);
    }

    public Long2ObjectSortedMultiValueMap() {
        this(Comparator.naturalOrder());
    }

    public void put(long key, V value) {
        ObjectSortedSet<V> set = map.get(key);
        if (set == null) {
            switch(treeAlgorithm) {
                case REDBLACK:  { set = new ObjectRBTreeSet<V>(valueComparator); break; }
                case AVL:       { set = new ObjectAVLTreeSet<V>(valueComparator); break; }
                default: { throw new IllegalStateException(); }
            }
            map.put(key, set);
        }
        set.add(value);
    }

    public ObjectSortedSet<V> getSorted(final long key) {
        return map.get(key);
    }

    public ObjectSet<V> get(final long key) {
        return getSorted(key);
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

    public boolean matchesAll(final long[] keys, V value) {
        if (value == null) throw new IllegalArgumentException("cannot compare null value");
        for (final long key: keys) {
            ObjectSortedSet<V> set = map.get(key);
            if (set == null) return false;
            if (!set.contains(key)) return false;
        }
        return true;
    }

    public void remove(final long key, final long value) {
        final ObjectSet set = map.get(key);
        if (set != null) set.remove(value);
        else return;
        if (set.isEmpty()) map.remove(key);
    }

    @Override
    public void remove(final long key, final V value) {
        final ObjectSet set = map.get(key);
        if (set != null) set.remove(value);
        else return;
        if (set.isEmpty()) map.remove(key);
    }

    @Override
    public void remove(final long key) {
        map.remove(key);
    }

    @Override
    public LongSet getKeys() {
        return map.keySet();
    }

    public void clear() {
        map.clear();
    }

    public ObjectSet<Map.Entry<Long, ObjectSortedSet<V>>> entrySet() {
        return map.entrySet();
    }

    @Override
    public String toString() {
        return "Long2ObjectMultiValueMap{" +
                "multiValueMap=" + map +
                '}';
    }
}
