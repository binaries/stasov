package com.pocketmath.stasov.util.multimaps;

import com.pocketmath.stasov.util.IndexAlgorithm;
import it.unimi.dsi.fastutil.longs.*;

/**
 * Created by etucker on 3/26/15.
 */
public class Long2LongMultiValueSortedMap<V extends Comparable<V>> extends AbstractSortedMultiValueMap<V> {

    private final Long2ObjectMap<LongSortedSet> map;

    public Long2LongMultiValueSortedMap(final IndexAlgorithm indexAlgorithm) {
        super(null, indexAlgorithm);
        switch (indexAlgorithm) {
            case REDBLACK:  { map = new Long2ObjectRBTreeMap<LongSortedSet>(); break; }
            case AVL: { map = new Long2ObjectAVLTreeMap<LongSortedSet>(); break; }
            default: { throw new IllegalStateException(); }
        }
    }

    public Long2LongMultiValueSortedMap() {
        this(IndexAlgorithm.AVL);
    }

    public void put(final long key, final long value) {
        LongSortedSet set = map.get(key);
        if (set == null) {
            switch(indexAlgorithm) {
                case REDBLACK:  { set = new LongRBTreeSet(); break; }
                case AVL:       { set = new LongAVLTreeSet(); break; }
                default: { throw new IllegalStateException(); }
            }
            map.put(key, set);
        }
        set.add(value);
    }

    public void fastPut(final long key, final LongSortedSet set) {
        map.put(key, set);
    }

    public LongSortedSet getSorted(final long key) {
        return map.get(key);
    }

    public boolean contains(final long key, final long value) {
        LongSortedSet set = map.get(key);
        if (set == null) return false;
        return set.contains(value);
    }

    public boolean containsKey(final long key) {
        return map.containsKey(key);
    }

    public void remove(final long key, final long value) {
        final LongSortedSet set = map.get(key);
        if (set != null) set.remove(value);
        else return;
        if (set.isEmpty()) map.remove(key);
    }

    public void remove(final long key) {
        map.remove(key);
    }

    public LongSet getKeys() {
        return map.keySet();
    }

    public void clear() {
        map.clear();
    }

    @Override
    public String toString() {
        return "Long2LongMultiValueMap{" +
                "multiValueMap=" + map +
                '}';
    }
}
